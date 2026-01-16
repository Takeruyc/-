package jp.ac.jec.cm0136.android101;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.media.MediaPlayer;
import java.util.List;

public class WordDetailActivity extends AppCompatActivity {

    private Word word;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        int wordId = getIntent().getIntExtra("word_id", 1);
        word = DataManager.getWordById(wordId);

        if (word != null) {
            setupUI();
        }

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        Button playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(v -> toggleAudioPlayback());

        Button remixButton = findViewById(R.id.remix_button);
        remixButton.setOnClickListener(v -> remixDialogue());
    }

    private void setupUI() {
        TextView wordText = findViewById(R.id.word_text);
        TextView readingText = findViewById(R.id.reading_text);
        TextView meaningText = findViewById(R.id.meaning_text);
        TextView descriptionText = findViewById(R.id.description_text);
        TextView typeText = findViewById(R.id.type_text);
        LinearLayout dangerLevelLayout = findViewById(R.id.danger_level_layout);

        wordText.setText(word.getWord());
        readingText.setText(word.getReading());
        meaningText.setText(word.getMeaning());
        descriptionText.setText(word.getDescription());

        if ("jirai".equals(word.getType())) {
            typeText.setText("地雷単語");
            typeText.setBackgroundColor(ContextCompat.getColor(this, R.color.red_light));
            typeText.setTextColor(ContextCompat.getColor(this, R.color.red_dark));
        } else {
            typeText.setText("定番");
            typeText.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_light));
            typeText.setTextColor(ContextCompat.getColor(this, R.color.blue_dark));
        }

        setupDangerLevel(dangerLevelLayout, word.getDangerLevel());
        setupDialogueExamples(word.getDialogues());
    }

    private void setupDangerLevel(LinearLayout layout, int level) {
        layout.removeAllViews();

        for (int i = 0; i < 5; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    24, 8
            );
            params.setMargins(2, 0, 2, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.danger_dot_bg);

            if (i < level) {
                if (level >= 4) {
                    dot.setBackgroundColor(ContextCompat.getColor(this, R.color.red_dark));
                } else {
                    dot.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
                }
            } else {
                dot.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_light));
            }

            layout.addView(dot);
        }
    }

    private void setupDialogueExamples(List<Dialogue> dialogues) {
        LinearLayout dialogueLayout = findViewById(R.id.dialogue_layout);
        dialogueLayout.removeAllViews();

        for (Dialogue dialogue : dialogues) {
            View dialogueView = getLayoutInflater().inflate(
                    R.layout.item_dialogue, dialogueLayout, false
            );

            TextView speakerText = dialogueView.findViewById(R.id.speaker_text);
            TextView dialogueText = dialogueView.findViewById(R.id.dialogue_text);

            speakerText.setText(dialogue.getSpeaker() + ":");
            dialogueText.setText(dialogue.getText());

            dialogueLayout.addView(dialogueView);
        }
    }

    private void toggleAudioPlayback() {
        Button playButton = findViewById(R.id.play_button);

        if (isPlaying) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            playButton.setText("音声を再生");
            isPlaying = false;
        } else {
            playButton.setText("停止");
            isPlaying = true;

            new android.os.Handler().postDelayed(() -> {
                if (isPlaying) {
                    playButton.setText("音声を再生");
                    isPlaying = false;
                }
            }, 3000);
        }
    }

    private void remixDialogue() {
        List<Dialogue> newDialogues = DataManager.generateNewDialogue(word);
        setupDialogueExamples(newDialogues);

        android.widget.Toast.makeText(
                this,
                "AIが新しい会話を生成しました！",
                android.widget.Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}