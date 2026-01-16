package jp.ac.jec.cm0136.android101;

import android.content.Context;
import android.graphics.Color;
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

    public WordAdapter(@NonNull Context context, List<Word> words) {
        super(context, R.layout.item_word, words);
        this.context = context;
        this.originalList = new ArrayList<>(words);
        this.filteredList = new ArrayList<>(words);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Word word = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_word, parent, false);
        }

        if (word != null) {
            TextView wordText = convertView.findViewById(R.id.word_text);
            TextView meaningText = convertView.findViewById(R.id.meaning_text);
            TextView typeText = convertView.findViewById(R.id.type_text);
            ImageView dangerIcon = convertView.findViewById(R.id.danger_icon);
            LinearLayout dangerLevelLayout = convertView.findViewById(R.id.danger_level_layout);

            wordText.setText(word.getWord());

            String meaning = word.getMeaning();
            if (meaning.length() > 30) {
                meaning = meaning.substring(0, 30) + "...";
            }
            meaningText.setText(meaning);

            if ("jirai".equals(word.getType())) {
                typeText.setText("Risk");
                typeText.setBackgroundResource(R.drawable.bg_red_tag);
                typeText.setTextColor(ContextCompat.getColor(context, R.color.red_dark));
                dangerIcon.setVisibility(View.VISIBLE);
            } else {
                typeText.setText("Safe");
                typeText.setBackgroundResource(R.drawable.bg_blue_tag);
                typeText.setTextColor(ContextCompat.getColor(context, R.color.blue_dark));
                dangerIcon.setVisibility(View.GONE);
            }

            setupDangerLevel(dangerLevelLayout, word.getDangerLevel());
        }

        return convertView;
    }

    private void setupDangerLevel(LinearLayout layout, int level) {
        layout.removeAllViews();

        for (int i = 0; i < 5; i++) {
            View dot = new View(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(16), dpToPx(8)
            );
            params.setMargins(dpToPx(2), 0, dpToPx(2), 0);
            dot.setLayoutParams(params);

            if (i < level) {
                if (level >= 4) {
                    dot.setBackgroundColor(ContextCompat.getColor(context, R.color.red_dark));
                } else {
                    dot.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
                }
            } else {
                dot.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_light));
            }
            dot.setBackgroundResource(R.drawable.danger_dot_bg);

            layout.addView(dot);
        }
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public void filter(String query, String filterType) {
        filteredList.clear();

        for (Word word : originalList) {
            boolean matchesQuery = true;
            boolean matchesType = true;

            if (query != null && !query.isEmpty()) {
                String lowerQuery = query.toLowerCase();
                matchesQuery = word.getWord().toLowerCase().contains(lowerQuery) ||
                        word.getMeaning().toLowerCase().contains(lowerQuery) ||
                        word.getReading().toLowerCase().contains(lowerQuery);
            }

            if (filterType != null && !filterType.equals("すべて")) {
                if (filterType.equals("定番")) {
                    matchesType = "standard".equals(word.getType());
                } else if (filterType.equals("地雷")) {
                    matchesType = "jirai".equals(word.getType());
                }
            }

            if (matchesQuery && matchesType) {
                filteredList.add(word);
            }
        }

        clear();
        addAll(filteredList);
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