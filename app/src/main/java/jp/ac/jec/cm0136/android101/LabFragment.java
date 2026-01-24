package jp.ac.jec.cm0136.android101;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LabFragment extends Fragment {

    // Views
    private EditText inputEditText;
    private Button analyzeButton;
    private CardView resultLayout;
    private ProgressBar progressBar;
    private CircularProgressBar scoreProgressBar;
    private TextView feedbackText, vibeText, warningText, charCountText;
    private LinearLayout dangerDotsLayout, emptyStateLayout;
    private ImageView historyButton;

    // Constants
    private static final int MAX_INPUT_LENGTH = 80;

    private final Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    private void setupViews(View view) {
        // Header
        historyButton = view.findViewById(R.id.history_button);
        historyButton.setOnClickListener(v -> showHistory());

        // Input
        inputEditText = view.findViewById(R.id.input_edit_text);
        charCountText = view.findViewById(R.id.char_count_text);
        analyzeButton = view.findViewById(R.id.analyze_button);

        // States
        emptyStateLayout = view.findViewById(R.id.empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
        resultLayout = view.findViewById(R.id.result_layout);
        warningText = view.findViewById(R.id.warning_text);

        // Result
        scoreProgressBar = view.findViewById(R.id.score_progress_bar);
        dangerDotsLayout = view.findViewById(R.id.danger_dots_layout);
        vibeText = view.findViewById(R.id.vibe_text);
        feedbackText = view.findViewById(R.id.feedback_text);

        // Listeners
        setupInputValidation();
        analyzeButton.setOnClickListener(v -> analyzeText());
    }

    private void setupInputValidation() {
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                charCountText.setText(length + "/" + MAX_INPUT_LENGTH);
                analyzeButton.setEnabled(length > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MAX_INPUT_LENGTH) {
                    s.delete(MAX_INPUT_LENGTH, s.length());
                }
            }
        });
        // Initial state
        analyzeButton.setEnabled(false);
    }

    private void analyzeText() {
        String input = inputEditText.getText().toString().trim();
        if (input.isEmpty()) return;

        showLoadingState(true);

        try {
            String encodedText = URLEncoder.encode(input, "UTF-8");
            String url = "https://takeruyc.codemoe.com/api/v1/app/aiLab/kotobaCheck?text=" + encodedText;

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> showLoadingState(false));
                    }
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                         if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> showLoadingState(false));
                        }
                        return;
                    }

                    String json = response.body().string();
                    AnalysisResult result = parseAnalysisResult(json);

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showLoadingState(false);
                            if (result != null) {
                                displayResults(input, result);
                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showLoadingState(false);
        }
    }

    private AnalysisResult parseAnalysisResult(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<ApiResponse<AnalysisResult>>() {}.getType();
        ApiResponse<AnalysisResult> response = gson.fromJson(json, type);
        return response != null ? response.result : null;
    }

    private void displayResults(String inputText, AnalysisResult result) {
        resultLayout.setVisibility(View.VISIBLE);

        scoreProgressBar.setProgress(result.getScore());
        feedbackText.setText(result.getFeedback());
        vibeText.setText(String.format("雰囲気: %s", result.getVibe()));

        setupDangerDots(result.getDangerLevel());
    }

    private void setupDangerDots(int level) {
        dangerDotsLayout.removeAllViews();
        for (int i = 0; i < 5; i++) {
            View dot = new View(getContext());
            int dotSize = dpToPx(16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.dot_background);
            if (getContext() != null) {
                 dot.getBackground().setTint(i < level ? ContextCompat.getColor(getContext(), R.color.red_dark) : ContextCompat.getColor(getContext(), R.color.gray_light));
            }
            dangerDotsLayout.addView(dot);
        }
    }

    private void showLoadingState(boolean isLoading) {
        emptyStateLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        analyzeButton.setEnabled(!isLoading);
        inputEditText.setEnabled(!isLoading);

        if (isLoading) {
            resultLayout.setVisibility(View.GONE);
        }
    }

    private void showHistory() {
        if (getFragmentManager() != null) {
            HistoryDialogFragment dialogFragment = new HistoryDialogFragment();
            dialogFragment.show(getParentFragmentManager(), "history_dialog");
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
