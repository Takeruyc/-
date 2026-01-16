package jp.ac.jec.cm0136.android101;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private CardView todayWordCard;
    private CardView aiLabCard;
    private CardView recommendCard1;
    private CardView recommendCard2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        todayWordCard = view.findViewById(R.id.today_word_card);
        aiLabCard = view.findViewById(R.id.ai_lab_card);
        recommendCard1 = view.findViewById(R.id.recommend_card_1);
        recommendCard2 = view.findViewById(R.id.recommend_card_2);

        setupTodayWord();

        todayWordCard.setOnClickListener(v -> {
            Word word = DataManager.getTodayWord();
            Intent intent = new Intent(getActivity(), WordDetailActivity.class);
            intent.putExtra("word_id", word.getId());
            startActivity(intent);
        });

        aiLabCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AILabActivity.class);
            startActivity(intent);
        });

        recommendCard1.setOnClickListener(v -> {
            openWordDetail(2);
        });

        recommendCard2.setOnClickListener(v -> {
            openWordDetail(4);
        });

        return view;
    }

    private void setupTodayWord() {
        Word todayWord = DataManager.getTodayWord();
        TextView wordText = todayWordCard.findViewById(R.id.word_text);
        TextView readingText = todayWordCard.findViewById(R.id.reading_text);

        if (wordText != null && readingText != null) {
            wordText.setText(todayWord.getWord());
            readingText.setText(todayWord.getReading());
        }
    }

    private void openWordDetail(int wordId) {
        Intent intent = new Intent(getActivity(), WordDetailActivity.class);
        intent.putExtra("word_id", wordId);
        startActivity(intent);
    }
}