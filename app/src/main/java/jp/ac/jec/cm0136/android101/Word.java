package jp.ac.jec.cm0136.android101;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Word {
    private int id;
    private String word;
    private String reading;
    private String meaning;
    private String type; // "standard" or "jirai"
    @SerializedName("danger_level")
    private int dangerLevel; // 1-5
    private String description;
    private List<Dialogue> dialogues;

    @SerializedName("sounds_url")
    private List<String> soundsUrl;

    @SerializedName("create_time")
    private String createTime;

    @SerializedName("update_time")
    private String updateTime;

    @SerializedName("is_delete")
    private int isDelete;

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
    public List<String> getSoundsUrl() { return soundsUrl; }
    public void setDialogues(List<Dialogue> dialogues) { this.dialogues = dialogues; }
}