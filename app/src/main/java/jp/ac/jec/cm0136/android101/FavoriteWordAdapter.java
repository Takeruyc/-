package jp.ac.jec.cm0136.android101;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoriteWordAdapter extends RecyclerView.Adapter<FavoriteWordAdapter.ViewHolder> {

    private List<Word> favoriteWords;
    private OnWordClickListener listener;

    public interface OnWordClickListener {
        void onWordClick(Word word);
    }

    public FavoriteWordAdapter(List<Word> favoriteWords, OnWordClickListener listener) {
        this.favoriteWords = favoriteWords;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_word, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Word word = favoriteWords.get(position);
        holder.bind(word);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWordClick(word);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteWords.size();
    }

    public void updateData(List<Word> newWords) {
        this.favoriteWords = newWords;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView wordText;
        private ImageView typeIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wordText = itemView.findViewById(R.id.favorite_word_text);
            typeIcon = itemView.findViewById(R.id.favorite_type_icon);
            // arrowIcon 已删除，不需要了
        }

        public void bind(Word word) {
            wordText.setText(word.getWord());
            typeIcon.setImageResource(android.R.drawable.btn_star); // Always use star icon
            typeIcon.setBackgroundResource(R.drawable.bg_favorite_icon_circle);

            Context context = itemView.getContext();
            if ("jirai".equals(word.getType())) {
                typeIcon.setColorFilter(ContextCompat.getColor(context, R.color.red_dark));
                typeIcon.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.red_light));
            } else {
                typeIcon.setColorFilter(ContextCompat.getColor(context, R.color.blue_dark));
                typeIcon.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.blue_light));
            }
        }
    }
}