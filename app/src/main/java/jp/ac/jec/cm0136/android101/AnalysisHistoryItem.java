package jp.ac.jec.cm0136.android101;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AnalysisHistoryItem {
    @SerializedName("id")
    private final String id;
    @SerializedName("input")
    private final String inputText;
    @SerializedName("score")
    private final int score;
    @SerializedName("danger_level")
    private final int dangerLevel;

    @SerializedName("feedback")
    private final String feedback;
    private final String vibe;
    @SerializedName("create_time")
    private final String createTime;

    public AnalysisHistoryItem(String id, String inputText, int score, int dangerLevel, String feedback, String vibe, String createTime) {
        this.id = id;
        this.inputText = inputText;
        this.score = score;
        this.dangerLevel = dangerLevel;
        this.feedback = feedback;
        this.vibe = vibe;
        this.createTime = createTime;
    }

    // Getters
    public String getId() { return id; }

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

    public String getFormattedTimestamp() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
//        return sdf.format(new Date(timestamp));
        return createTime;
    }
}