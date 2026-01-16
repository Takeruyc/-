package jp.ac.jec.cm0136.android101;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<AnalysisHistoryItem> historyItems;

    public HistoryAdapter(List<AnalysisHistoryItem> historyItems) {
        this.historyItems = historyItems;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        AnalysisHistoryItem item = historyItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView inputText;
        private final TextView scoreText;
        private final TextView timestampText;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            inputText = itemView.findViewById(R.id.history_input_text);
            scoreText = itemView.findViewById(R.id.history_score_text);
            timestampText = itemView.findViewById(R.id.history_timestamp_text);
        }

        public void bind(AnalysisHistoryItem item) {
            inputText.setText(item.getInputText());
            scoreText.setText(item.getScore() + "点");
            timestampText.setText(item.getFormattedTimestamp());
        }
    }
}