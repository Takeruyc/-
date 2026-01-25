package jp.ac.jec.cm0136.android101;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryDialogFragment extends DialogFragment implements HistoryAdapter.OnHistoryItemClickListener {

    private HistoryAdapter adapter;
    private final List<AnalysisHistoryItem> historyList = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private AlertDialog detailDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.history_recycler_view);
        ImageView closeButton = view.findViewById(R.id.close_history_button);

        adapter = new HistoryAdapter(historyList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchHistoryFromApi();

        closeButton.setOnClickListener(v -> dismiss());
    }

    // 获取所有历史记录并触发渲染
    private void fetchHistoryFromApi() {
        Request request = new Request.Builder()
                .url("https://takeruyc.codemoe.com/api/v1/app/aiLab/history")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(
                                    requireContext(),
                                    "履歴の取得に失敗しました",
                                    Toast.LENGTH_SHORT
                            ).show()
                    );
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {

                if (!response.isSuccessful()) return;

                String json = response.body().string();

                Type type = new TypeToken<ApiResponse<List<AnalysisHistoryItem>>>() {}.getType();
                ApiResponse<List<AnalysisHistoryItem>> apiResponse =
                        gson.fromJson(json, type);

                if (apiResponse == null || apiResponse.result == null) return;

                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        historyList.clear();
                        historyList.addAll(apiResponse.result);
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }

    // 删除
    private void deleteHistoryFromApi(AnalysisHistoryItem item) {
        ProgressDialog loading = new ProgressDialog(requireContext());
        loading.setMessage("削除中...");
        loading.setCancelable(false);
        loading.show();

        String url = "https://takeruyc.codemoe.com/api/v1/app/aiLab/deleteHistory?id=" + item.getId();

        System.out.println(url);

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    loading.dismiss();
                    Toast.makeText(
                            requireContext(),
                            "削除に失敗しました",
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {

                String json = response.body().string();
                ApiResponse<?> apiResponse = new Gson().fromJson(json, ApiResponse.class);

                requireActivity().runOnUiThread(() -> {
                    loading.dismiss();

                    System.out.println(response);

                    if (response.isSuccessful() && apiResponse != null && apiResponse.success) {
                        Toast.makeText(
                                requireContext(),
                                "削除しました",
                                Toast.LENGTH_SHORT
                        ).show();

                        detailDialog.dismiss();
                        fetchHistoryFromApi();
                    } else {
                        Toast.makeText(
                                requireContext(),
                                "削除に失敗しました",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        });
    }

    // 删除前弹窗确认
    private void showDeleteConfirmDialog(AnalysisHistoryItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("確認")
                .setMessage("この履歴を削除しますか？")
                .setPositiveButton("削除", (d, w) -> {
                    d.dismiss();
                    deleteHistoryFromApi(item);
                })
                .setNegativeButton("キャンセル", null)
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onItemClick(AnalysisHistoryItem item) {
        detailDialog = new AlertDialog.Builder(requireContext())
                .setTitle("AIからのアドバイス")
                .setMessage(item.getFeedback())
                .setPositiveButton("閉じる", null)
                .setNegativeButton("削除", (dialog, which) -> {
                    showDeleteConfirmDialog(item);
                })
                .create();

        detailDialog.show();
    }
}