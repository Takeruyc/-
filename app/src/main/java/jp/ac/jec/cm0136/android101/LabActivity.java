package jp.ac.jec.cm0136.android101;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class LabActivity extends AppCompatActivity {

    // Views
    private EditText inputEditText;
    private Button analyzeButton, retryButton;
    private CardView resultLayout;
    private ProgressBar progressBar;
    private CircularProgressBar scoreProgressBar;
    private TextView feedbackText, vibeText, warningText, charCountText;
    private LinearLayout dangerDotsLayout, emptyStateLayout;
    private ImageView historyButton;

    // Constants
    private static final String API_KEY = ""; // Simulate no API key
    private static final int MAX_INPUT_LENGTH = 80;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab);
        setupViews();

        if (API_KEY.isEmpty()) {
            warningText.setVisibility(View.VISIBLE);
        }
    }

    private void setupViews() {
        // Header
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
        historyButton = findViewById(R.id.history_button);
        historyButton.setOnClickListener(v -> showHistory());

        // Input
        inputEditText = findViewById(R.id.input_edit_text);
        charCountText = findViewById(R.id.char_count_text);
        analyzeButton = findViewById(R.id.analyze_button);

        // States
        emptyStateLayout = findViewById(R.id.empty_state);
        progressBar = findViewById(R.id.progress_bar);
        resultLayout = findViewById(R.id.result_layout);
        retryButton = findViewById(R.id.retry_button);
        warningText = findViewById(R.id.warning_text);

        // Result
        scoreProgressBar = findViewById(R.id.score_progress_bar);
        dangerDotsLayout = findViewById(R.id.danger_dots_layout);
        vibeText = findViewById(R.id.vibe_text);
        feedbackText = findViewById(R.id.feedback_text);

        // Listeners
        setupInputValidation();
        analyzeButton.setOnClickListener(v -> analyzeText());
        retryButton.setOnClickListener(v -> resetToInitialState());
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

        // Simulate API call
        handler.postDelayed(() -> {
            showLoadingState(false);
            AnalysisResult result = DataManager.getMockAnalysisResult();
            displayResults(input, result);
        }, 1500);
    }

    private void displayResults(String inputText, AnalysisResult result) {
        resultLayout.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);

        // Set score on CircularProgressBar
        scoreProgressBar.setProgress(result.getScore());

        // Set feedback and vibe
        feedbackText.setText(result.getFeedback());
        vibeText.setText("雰囲気: " + result.getVibe());

        // Set danger level dots
        setupDangerDots(result.getDangerLevel());

        // Save to history
        AnalysisHistoryItem historyItem = new AnalysisHistoryItem(
                inputText,
                result.getScore(),
                result.getDangerLevel(),
                result.getFeedback(),
                result.getVibe()
        );
        SharedPrefManager.saveAnalysisHistory(this, historyItem);
    }

    private void setupDangerDots(int level) {
        dangerDotsLayout.removeAllViews();
        for (int i = 0; i < 5; i++) {
            View dot = new View(this);
            int dotSize = dpToPx(16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.dot_background); // Use a drawable for shape
            dot.getBackground().setTint(i < level ? ContextCompat.getColor(this, R.color.red_dark) : ContextCompat.getColor(this, R.color.gray_light));
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
            retryButton.setVisibility(View.GONE);
        }
    }

    private void resetToInitialState() {
        resultLayout.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
        inputEditText.setText("");
        inputEditText.setEnabled(true);
    }

    private void showHistory() {
        HistoryDialogFragment dialogFragment = new HistoryDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "history_dialog");
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    public static Intent createIntent(android.content.Context context) {
        return new Intent(context, LabActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}