package jp.ac.jec.cm0136.android101;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AnalysisHistoryItem {
    private final String inputText;
    private final int score;
    private final int dangerLevel;
    private final String feedback;
    private final String vibe;
    private final long timestamp;

    public AnalysisHistoryItem(String inputText, int score, int dangerLevel, String feedback, String vibe) {
        this.inputText = inputText;
        this.score = score;
        this.dangerLevel = dangerLevel;
        this.feedback = feedback;
        this.vibe = vibe;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getInputText() {
        return inputText;
    }

    public int getScore() {
        return score;
    }

    public int getDangerLevel() {
        return dangerLevel;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getVibe() {
        return vibe;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}