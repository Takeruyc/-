package jp.ac.jec.cm0136.android101;

import java.util.List;

public class Word {
    private int id;
    private String word;
    private String reading;
    private String meaning;
    private String type; // "standard" or "jirai"
    private int dangerLevel;
    private String description;
    private List<Dialogue> dialogues;

    public Word(int id, String word, String reading, String meaning,
                String type, int dangerLevel, String description,
                List<Dialogue> dialogues) {
        this.id = id;
        this.word = word;
        this.reading = reading;
        this.meaning = meaning;
        this.type = type;
        this.dangerLevel = dangerLevel;
        this.description = description;
        this.dialogues = dialogues;
    }

    // Getters
    public int getId() { return id; }
    public String getWord() { return word; }
    public String getReading() { return reading; }
    public String getMeaning() { return meaning; }
    public String getType() { return type; }
    public int getDangerLevel() { return dangerLevel; }
    public String getDescription() { return description; }
    public List<Dialogue> getDialogues() { return dialogues; }
}

class Dialogue {
    private String speaker;
    private String text;

    public Dialogue(String speaker, String text) {
        this.speaker = speaker;
        this.text = text;
    }

    public String getSpeaker() { return speaker; }
    public String getText() { return text; }
}