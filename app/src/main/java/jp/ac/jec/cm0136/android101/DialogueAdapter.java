package jp.ac.jec.cm0136.android101;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DialogueAdapter extends RecyclerView.Adapter<DialogueAdapter.DialogueViewHolder> {

    private final Context context;
    private final List<Dialogue> dialogues;

    public DialogueAdapter(Context context, List<Dialogue> dialogues) {
        this.context = context;
        this.dialogues = dialogues;
    }

    @NonNull
    @Override
    public DialogueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dialogue, parent, false);
        return new DialogueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogueViewHolder holder, int position) {
        Dialogue dialogue = dialogues.get(position);
        holder.bind(dialogue);
    }

    @Override
    public int getItemCount() {
        return dialogues.size();
    }

    static class DialogueViewHolder extends RecyclerView.ViewHolder {
        private final TextView speakerText;
        private final TextView dialogueText;

        public DialogueViewHolder(@NonNull View itemView) {
            super(itemView);
            speakerText = itemView.findViewById(R.id.speaker_text);
            dialogueText = itemView.findViewById(R.id.dialogue_text);
        }

        public void bind(Dialogue dialogue) {
            speakerText.setText(dialogue.getSpeaker() + ":");
            dialogueText.setText(dialogue.getText());
        }
    }
}