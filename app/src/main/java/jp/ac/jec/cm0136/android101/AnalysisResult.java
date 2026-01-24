package jp.ac.jec.cm0136.android101;

public class AnalysisResult {
    private int score;
    private int dangerLevel;
    private String feedback;
    private String vibe;

    public AnalysisResult(int score, int dangerLevel, String feedback, String vibe) {
        this.score = score;
        this.dangerLevel = dangerLevel;
        this.feedback = feedback;
        this.vibe = vibe;
    }

    // Getters
    public int getScore() { return score; }
    public int getDangerLevel() { return dangerLevel; }
    public String getFeedback() { return feedback; }
    public String getVibe() { return vibe; }
}