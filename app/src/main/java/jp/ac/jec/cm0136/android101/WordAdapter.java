package jp.ac.jec.cm0136.android101;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class WordAdapter extends ArrayAdapter<Word> {

    private Context context;
    private List<Word> originalList;
    private List<Word> filteredList;
    private String currentFilter = "all";
    private String currentSearch = "";

    public WordAdapter(@NonNull Context context, List<Word> words) {
        super(context, 0, words);
        this.context = context;
        this.originalList = new ArrayList<>(words);
        this.filteredList = new ArrayList<>(words);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_word, parent, false);
        }

        Word word = getItem(position);
        if (word == null) return convertView;

        // 设置UI组件
        TextView wordText = convertView.findViewById(R.id.word_text);
        TextView meaningText = convertView.findViewById(R.id.meaning_text);
        TextView typeBadge = convertView.findViewById(R.id.type_badge);
        ImageView favoriteIcon = convertView.findViewById(R.id.favorite_icon);
        LinearLayout dangerLevelLayout = convertView.findViewById(R.id.danger_level_layout);

        // 设置数据
        wordText.setText(word.getWord());
        meaningText.setText(word.getMeaning());

        // 设置类型徽章
        if ("jirai".equals(word.getType())) {
            typeBadge.setText("Risk");
            typeBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.red_light));
            typeBadge.setTextColor(ContextCompat.getColor(context, R.color.red_dark));
        } else {
            typeBadge.setText("Safe");
            typeBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_light));
            typeBadge.setTextColor(ContextCompat.getColor(context, R.color.blue_dark));
        }

        // 设置危险等级
        setupDangerLevel(dangerLevelLayout, word.getDangerLevel());

        // 设置收藏状态
        boolean isFavorite = SharedPrefManager.isFavorite(context, word.getId());
        favoriteIcon.setActivated(isFavorite);

        // -- 点击事件修复 --

        // 1. 给星星图标设置点击事件
        favoriteIcon.setOnClickListener(v -> {
            boolean currentlyFavorite = SharedPrefManager.isFavorite(context, word.getId());
            if (currentlyFavorite) {
                SharedPrefManager.removeFavorite(context, word.getId());
            } else {
                SharedPrefManager.addFavorite(context, word.getId());
            }
            favoriteIcon.setActivated(!currentlyFavorite);
        });

        // 2. 给整个列表项设置点击事件来打开详情页
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WordDetailActivity.class);
            intent.putExtra("word_id", word.getId());
            context.startActivity(intent);
        });

        return convertView;
    }

    // (后面的代码保持不变...)

    private void setupDangerLevel(LinearLayout layout, int level) {
        layout.removeAllViews();
        int dotWidth = dpToPx(16), dotHeight = dpToPx(8), margin = dpToPx(2);
        for (int i = 0; i < 5; i++) {
            View dot = new View(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotWidth, dotHeight);
            params.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(params);
            if (i < level) {
                dot.setBackgroundColor(ContextCompat.getColor(context, level >= 4 ? R.color.red_dark : R.color.orange));
            } else {
                dot.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_light));
            }
            layout.addView(dot);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    public void filter(String query, String filterType) {
        filteredList.clear();
        currentSearch = query != null ? query.toLowerCase() : "";
        currentFilter = filterType;

        for (Word word : originalList) {
            boolean matchesSearch = currentSearch.isEmpty() ||
                    word.getWord().toLowerCase().contains(currentSearch);

            boolean matchesFilter;
            if ("all".equals(filterType) || "すべて".equals(filterType)) {
                matchesFilter = true;
            } else if ("standard".equals(filterType) || "定番".equals(filterType)) {
                matchesFilter = "standard".equals(word.getType());
            } else if ("jirai".equals(filterType) || "地雷".equals(filterType)) {
                matchesFilter = "jirai".equals(word.getType());
            } else {
                matchesFilter = true;
            }

            if (matchesSearch && matchesFilter) {
                filteredList.add(word);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Nullable
    @Override
    public Word getItem(int position) {
        if (position >= 0 && position < filteredList.size()) {
            return filteredList.get(position);
        }
        return null;
    }
}
