package jp.ac.jec.cm0136.android101;

import android.app.AlertDialog;
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
        new AlertDialog.Builder(requireContext())
            .setTitle("AIからのアドバイス")
            .setMessage(item.getFeedback())
            .setPositiveButton("閉じる", null)
            .show();
    }
}