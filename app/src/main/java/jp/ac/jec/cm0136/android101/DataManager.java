package jp.ac.jec.cm0136.android101;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DataManager {
    private static final OkHttpClient client = new OkHttpClient();
    private static List<Word> words;
    private static Random random = new Random();
    private static Word todayWord = null; // 用于缓存单词的变量

    public static void initializeWords(OnWordsLoadedListener listener) {
        words = new ArrayList<>();

        Request request = new Request.Builder()
                .url("https://takeruyc.codemoe.com/api/v1/app/word/list?pageSize=100000&current=1&column=create_time&order=asc&isDelete=0")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) return;

                assert response.body() != null;
                String json = response.body().string();
                parseWords(json);

                if (listener != null) {
                    listener.onWordsLoaded();
                }
            }
        });
    }

    // 获取缓存的或新的随机单词的方法
    public static Word getRandomWord() {
        if (words == null || words.isEmpty()) {
            return null;
        }
        // 如果 todayWord 尚未被赋值，则随机获取一个并缓存
        if (todayWord == null) {
            todayWord = words.get(random.nextInt(words.size()));
        }
        // 返回缓存的单词
        return todayWord;
    }

    /**
     * 【新增方法】
     * 强制刷新并获取一个新的随机单词。
     * 这个方法专门给刷新按钮使用。
     * @return 一个新的随机单词
     */
    public static Word refreshTodayWord() {
        // 1. 清除已缓存的单词
        todayWord = null;

        // 2. 调用 getRandomWord() 来获取一个全新的单词并返回
        return getRandomWord();
    }


    // 网络请求单词列表的回调接口
    public interface OnWordsLoadedListener {
        void onWordsLoaded();
    }

    private static void parseWords(String json) {
        Gson gson = new Gson();

        Type type = new TypeToken<ApiResponse<List<Word>>>() {}.getType();
        ApiResponse<List<Word>> response = gson.fromJson(json, type);

        if (response != null && response.result != null) {
            words = response.result;
        } else {
            words = new ArrayList<>();
        }

        System.out.println("反序列化完成，单词数量：" + words.size());
    }

    public static List<Word> getAllWords() {
        return new ArrayList<>(words);
    }

    public static Word getWordById(int id) {
        for (Word word : words) {
            if (word.getId() == id) {
                return word;
            }
        }
        return words.get(0);
    }

    public static List<Word> getWordsByType(String type) {
        List<Word> filtered = new ArrayList<>();
        for (Word word : words) {
            if ("all".equals(type) || word.getType().equals(type)) {
                filtered.add(word);
            }
        }
        return filtered;
    }

    public static AnalysisResult getMockAnalysisResult() {
        return new AnalysisResult(
                85,
                2,
                "「マジで」は親しい友達間ならOK！でも、目上の人には「本当に」を使いましょう。",
                "カジュアル"
        );
    }

    public static List<Dialogue> generateAIDialogue(Word word) {
        String[] scenarios = {
                "バイト先の休憩中",
                "デート中",
                "学校の放課後",
                "SNSのDM",
                "ゲーム中のチャット"
        };
        String scenario = scenarios[random.nextInt(scenarios.length)];

        return Arrays.asList(
                new Dialogue("A", "(" + scenario + ") この状況で「" + word.getWord() + "」を使ってみよう！"),
                new Dialogue("B", "うん、例えば..."),
                new Dialogue("A", "「" + word.getMeaning() + "」って感じで使えるね")
        );
    }
}
