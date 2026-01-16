package jp.ac.jec.cm0136.android101;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DataManager {

    private static List<Word> words;
    private static Random random = new Random();

    static {
        initializeWords();
    }

    private static void initializeWords() {
        words = new ArrayList<>();

        words.add(new Word(
                1,
                "エモい",
                "Emoi",
                "感情が揺さぶられる、懐かしい、センチメンタル",
                "standard",
                3,
                "「emotional」が由来。景色や音楽、雰囲気に対して感動した時に使う万能な言葉。ただし、ビジネスや公の場では避けるべき。",
                Arrays.asList(
                        new Dialogue("A", "ねえ、この夕焼け見て。"),
                        new Dialogue("B", "うわ、めっちゃエモいね写真撮ろう。")
                )
        ));

        words.add(new Word(
                2,
                "詰んだ",
                "Tsunda",
                "解決策がない、終わった、絶望的状況",
                "standard",
                2,
                "将棋の「詰み」から。試験前や失敗した時によく使うが、状況の深刻さを軽妙に表現するネットスラング。",
                Arrays.asList(
                        new Dialogue("A", "明日テストなのに教科書学校に忘れた。"),
                        new Dialogue("B", "それは詰んだわ（笑）")
                )
        ));

        words.add(new Word(
                3,
                "草",
                "Kusa",
                "笑える、面白い",
                "standard",
                1,
                "ネットスラング。「www」が草に見えることから。日常会話でも「それは草」と言う人がいるが、非常口語化しておりリスクは低い。",
                Arrays.asList(
                        new Dialogue("A", "寝坊してパジャマで来ちゃった。"),
                        new Dialogue("B", "まじ？それは草生えるわ。")
                )
        ));

        words.add(new Word(
                4,
                "地雷",
                "Jirai",
                "触れてはいけない話題、関わると面倒な人",
                "jirai",
                5,
                "人の容姿や特定の趣味など、踏んではいけないポイント。精神的に不安定な人を指すこともあり、直接使うとトラブルの原因になる。",
                Arrays.asList(
                        new Dialogue("A", "あのアイドルの話、彼にしない方がいいよ。"),
                        new Dialogue("B", "え、なんで？"),
                        new Dialogue("A", "彼にとってそれは地雷だから。ガチギレされるよ。")
                )
        ));

        words.add(new Word(
                5,
                "メンヘラ",
                "Menhera",
                "精神的に不安定、構ってちゃん",
                "jirai",
                4,
                "「メンタルヘルス」から。他人に対して使うと悪口になる可能性が非常に高い。極度な自虐としてネットで使うことはある。",
                Arrays.asList(
                        new Dialogue("A", "最近、彼女からのLINEが多すぎて...。"),
                        new Dialogue("B", "うわ、メンヘラ気質かもね。気をつけな。")
                )
        ));
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

    public static Word getTodayWord() {
        return words.get(0);
    }

    public static List<Word> getWordsByType(String type) {
        List<Word> filtered = new ArrayList<>();
        for (Word word : words) {
            if (type.equals("all") || word.getType().equals(type)) {
                filtered.add(word);
            }
        }
        return filtered;
    }

    public static List<Dialogue> generateNewDialogue(Word word) {
        String[] scenarios = {
                "バイト先の休憩中",
                "デート中",
                "学校の放課後",
                "SNSのDM",
                "ゲーム中のチャット"
        };

        String scenario = scenarios[random.nextInt(scenarios.length)];

        return Arrays.asList(
                new Dialogue("A", scenario + "での会話例だよ。"),
                new Dialogue("B", word.getWord() + "を使う場面を想像してみて。")
        );
    }
}