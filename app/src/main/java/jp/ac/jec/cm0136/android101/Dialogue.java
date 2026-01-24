package jp.ac.jec.cm0136.android101;

public class Dialogue {
    private String speaker; // "A" or "B"
    private String text;

    public Dialogue(String speaker, String text) {
        this.speaker = speaker;
        this.text = text;
    }

    public String getSpeaker() { return speaker; }
    public String getText() { return text; }
}