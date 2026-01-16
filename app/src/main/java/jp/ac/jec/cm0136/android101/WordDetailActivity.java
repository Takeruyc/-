package jp.ac.jec.cm0136.android101;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordDetailActivity extends AppCompatActivity {

    private Word word;
    private boolean isFavorite;
    private Handler handler = new Handler();
    private DialogueAdapter dialogueAdapter;
    private List<Dialogue> currentDialogues = new ArrayList<>();
    private Random random = new Random();

    // 音频播放相关
    private MediaPlayer mediaPlayer;
    private int currentAudioIndex = 0;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置为全屏对话框样式
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_word_detail);

        getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        );
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // 获取传递的单词ID
        int wordId = getIntent().getIntExtra("word_id", 1);
        word = DataManager.getWordById(wordId);
        isFavorite = SharedPrefManager.isFavorite(this, wordId);

        if (word != null) {
            setupUI();
        }
    }

    private void setupUI() {
        // 获取所有View
        ImageView closeButton = findViewById(R.id.close_button);
        TextView wordText = findViewById(R.id.word_text);
        TextView readingText = findViewById(R.id.reading_text);
        TextView meaningText = findViewById(R.id.meaning_text);
        TextView descriptionText = findViewById(R.id.description_text);
        TextView typeBadge = findViewById(R.id.type_badge);
        LinearLayout dangerLevelLayout = findViewById(R.id.danger_level_layout);
        ImageView favoriteIcon = findViewById(R.id.favorite_icon);
        Button remixButton = findViewById(R.id.remix_button);
        Button playButton = findViewById(R.id.play_button);
        RecyclerView dialogueRecycler = findViewById(R.id.dialogue_recycler);
        View headerBackground = findViewById(R.id.header_background);

        // 设置基础数据
        wordText.setText(word.getWord());
        readingText.setText(word.getReading());
        meaningText.setText(word.getMeaning());
        descriptionText.setText(word.getDescription());

        // 设置类型徽章
        if ("jirai".equals(word.getType())) {
            typeBadge.setText("地雷単語");
            typeBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.red_light));
            typeBadge.setTextColor(ContextCompat.getColor(this, R.color.red_dark));
            headerBackground.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
        } else {
            typeBadge.setText("定番");
            typeBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_light));
            typeBadge.setTextColor(ContextCompat.getColor(this, R.color.blue_dark));
            headerBackground.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
        }

        // 设置危险等级
        setupDangerLevel(dangerLevelLayout, word.getDangerLevel());

        // 设置收藏图标
        updateFavoriteIcon(favoriteIcon);

        // 设置对话列表
        currentDialogues.clear();
        if(word.getDialogues() != null){
            currentDialogues.addAll(word.getDialogues());
        }
        dialogueAdapter = new DialogueAdapter(this, currentDialogues);
        dialogueRecycler.setLayoutManager(new LinearLayoutManager(this));
        dialogueRecycler.setAdapter(dialogueAdapter);

        // 设置按钮点击事件
        closeButton.setOnClickListener(v -> finish());

        favoriteIcon.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            if (isFavorite) {
                SharedPrefManager.addFavorite(this, word.getId());
            } else {
                SharedPrefManager.removeFavorite(this, word.getId());
            }
            updateFavoriteIcon(favoriteIcon);
        });

        remixButton.setOnClickListener(v -> {
            remixButton.setEnabled(false);
            remixButton.setText("生成中...");

            // 模拟AI生成新对话
            handler.postDelayed(() -> {
                List<Dialogue> newDialogues = generateMockAIDialogue(word);
                currentDialogues.clear();
                currentDialogues.addAll(newDialogues);
                dialogueAdapter.notifyDataSetChanged();

                remixButton.setEnabled(true);
                remixButton.setText("AI Remix");
            }, 1000);
        });

        playButton.setOnClickListener(v -> toggleAudioPlayback(playButton));
        playButton.setCompoundDrawablesWithIntrinsicBounds(
                android.R.drawable.ic_media_play, 0, 0, 0
        );

        // 设置底部关闭按钮
        Button closeBtn = findViewById(R.id.close_button_bottom);
        if (closeBtn != null) {
            closeBtn.setOnClickListener(v -> finish());
        }
    }

    private void setupDangerLevel(LinearLayout layout, int level) {
        layout.removeAllViews();

        int dotWidth = dpToPx(16);
        int dotHeight = dpToPx(8);
        int margin = dpToPx(2);

        for (int i = 0; i < 5; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotWidth, dotHeight);
            params.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(params);

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

    private void updateFavoriteIcon(ImageView icon) {
        if (isFavorite) {
            icon.setImageResource(android.R.drawable.btn_star_big_on);
            icon.setColorFilter(ContextCompat.getColor(this, R.color.red_dark));
        } else {
            icon.setImageResource(android.R.drawable.btn_star_big_off);
            icon.setColorFilter(ContextCompat.getColor(this, R.color.gray));
        }
    }

    private void toggleAudioPlayback(Button playButton) {
//        boolean isPlaying = playButton.getText().toString().equals("停止");
//
//        System.out.println(word.getSoundsUrl());
//
//        if (isPlaying) {
//            // 停止播放
//            playButton.setText("音声を再生");
//            playButton.setCompoundDrawablesWithIntrinsicBounds(
//                    android.R.drawable.ic_media_play, 0, 0, 0
//            );
//        } else {
//            // 开始播放（模拟）
//            playButton.setText("停止");
//            playButton.setCompoundDrawablesWithIntrinsicBounds(
//                    android.R.drawable.ic_media_pause, 0, 0, 0
//            );
//
//            // 模拟3秒音频播放
//            handler.postDelayed(() -> {
//                if (playButton.getText().toString().equals("停止")) {
//                    playButton.setText("音声を再生");
//                    playButton.setCompoundDrawablesWithIntrinsicBounds(
//                            android.R.drawable.ic_media_play, 0, 0, 0
//                    );
//                }
//            }, 3000);
//        }

        List<String> soundUrls = word.getSoundsUrl();
        if (soundUrls == null || soundUrls.isEmpty()) {
            return;
        }

        if (isPlaying) {
            stopPlayback(playButton);
        } else {
            startPlayback(soundUrls, playButton);
        }
    }

    // 开始播放
    private void startPlayback(List<String> soundUrls, Button playButton) {
        isPlaying = true;
        currentAudioIndex = 0;

        playButton.setText("停止");
        playButton.setCompoundDrawablesWithIntrinsicBounds(
                android.R.drawable.ic_media_pause, 0, 0, 0
        );

        playNext(soundUrls, playButton);
    }

    // 顺序播放 - 播放下一个（递归）
    private void playNext(List<String> soundUrls, Button playButton) {

        if (!isPlaying || currentAudioIndex >= soundUrls.size()) {
            stopPlayback(playButton);
            return;
        }

        String url = soundUrls.get(currentAudioIndex);

        releaseMediaPlayer();

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);

            mediaPlayer.setOnCompletionListener(mp -> {
                currentAudioIndex++;
                playNext(soundUrls, playButton);
            });

            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
            stopPlayback(playButton);
        }
    }

    // 停止播放
    private void stopPlayback(Button playButton) {
        isPlaying = false;
        currentAudioIndex = 0;

        releaseMediaPlayer();

        playButton.setText("音声を再生");
        playButton.setCompoundDrawablesWithIntrinsicBounds(
                android.R.drawable.ic_media_play, 0, 0, 0
        );
    }

    // 释放 MediaPlayer
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private List<Dialogue> generateMockAIDialogue(Word word) {
        String[] scenarios = {
                "バイト先の休憩中",
                "デート中",
                "学校の放課後",
                "SNSのDM",
                "ゲーム中のチャット"
        };

        String scenario = scenarios[random.nextInt(scenarios.length)];
        List<Dialogue> dialogues = new ArrayList<>();

        dialogues.add(new Dialogue("A", "(" + scenario + ") ねえ、「" + word.getWord() + "」って最近よく聞くよね"));
        dialogues.add(new Dialogue("B", "そうそう！例えば「" + word.getMeaning() + "」って感じで使うんだよ"));
        dialogues.add(new Dialogue("A", "へえー！じゃあ次使ってみようかな"));

        return dialogues;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        isPlaying = false;
        releaseMediaPlayer();
    }
}