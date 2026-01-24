package jp.ac.jec.cm0136.android101;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefManager {
    private static final String PREFS_NAME = "RealJapPrefs";
    private static final String KEY_FAVORITES = "favorites";
    private static final String KEY_ANALYSIS_HISTORY = "analysis_history";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Favorite Word Management
    public static List<Integer> getFavorites(Context context) {
        String json = getPrefs(context).getString(KEY_FAVORITES, "[1, 4]"); // Default favorites
        Type type = new TypeToken<List<Integer>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void saveFavorites(Context context, List<Integer> favorites) {
        String json = new Gson().toJson(favorites);
        getPrefs(context).edit().putString(KEY_FAVORITES, json).apply();
    }

    public static void addFavorite(Context context, int wordId) {
        List<Integer> favorites = getFavorites(context);
        if (!favorites.contains(wordId)) {
            favorites.add(wordId);
            saveFavorites(context, favorites);
        }
    }

    public static void removeFavorite(Context context, int wordId) {
        List<Integer> favorites = getFavorites(context);
        if (favorites.remove(Integer.valueOf(wordId))) {
            saveFavorites(context, favorites);
        }
    }

    public static boolean isFavorite(Context context, int wordId) {
        return getFavorites(context).contains(wordId);
    }

    // AI Lab History Management
    public static List<AnalysisHistoryItem> getAnalysisHistory(Context context) {
        String json = getPrefs(context).getString(KEY_ANALYSIS_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<AnalysisHistoryItem>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void saveAnalysisHistory(Context context, AnalysisHistoryItem newItem) {
        List<AnalysisHistoryItem> history = getAnalysisHistory(context);
        history.add(0, newItem); // Add to the top of the list

        // Optional: Limit history size
        if (history.size() > 50) {
            history = history.subList(0, 50);
        }

        String json = new Gson().toJson(history);
        getPrefs(context).edit().putString(KEY_ANALYSIS_HISTORY, json).apply();
    }
}