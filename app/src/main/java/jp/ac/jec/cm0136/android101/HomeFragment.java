package jp.ac.jec.cm0136.android101;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    /* ===================== Views ===================== */
    private TextView todayWordText;
    private TextView todayWordReading;
    private TextView todayWordDate;
    private ImageView todayWordFavoriteIcon;
    private ImageButton skipButton;
    private View aiLabPromo;
    private RecyclerView favoriteRecycler;
    private View standardTabButton; // Changed from Button to View
    private View jiraiTabButton; // Changed from Button to View
    private TextView standardCountText;
    private TextView jiraiCountText;
    private View todayWordCard;
    private ProgressBar bottomProgressBar;

    /* ===================== Data ===================== */
    private Word todayWord;
    private boolean isTodayFavorite;
    private FavoriteWordAdapter favoriteAdapter;
    private String currentFavoriteType = "standard";
    private boolean isDataLoaded = false;

    /* ===================== Lifecycle ===================== */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isDataLoaded) {
            updateFavoriteList();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupListeners();
        loadInitialData();
    }

    /* ===================== Init ===================== */
    private void initViews(View view) {
        todayWordText = view.findViewById(R.id.today_word_text);
        todayWordReading = view.findViewById(R.id.today_word_reading);
        todayWordDate = view.findViewById(R.id.today_word_date);
        todayWordFavoriteIcon = view.findViewById(R.id.today_favorite_icon);
        skipButton = view.findViewById(R.id.skip_button);
        aiLabPromo = view.findViewById(R.id.ai_lab_promo);
        favoriteRecycler = view.findViewById(R.id.favorite_recycler);
        standardTabButton = view.findViewById(R.id.standard_tab_button);
        jiraiTabButton = view.findViewById(R.id.jirai_tab_button);
        standardCountText = view.findViewById(R.id.standard_count_text);
        jiraiCountText = view.findViewById(R.id.jirai_count_text);
        todayWordCard = view.findViewById(R.id.today_word_card);
        bottomProgressBar = view.findViewById(R.id.bottom_progress_bar);
    }

    private void setupRecyclerView() {
        favoriteAdapter = new FavoriteWordAdapter(new ArrayList<>(), this::openWordDetail);
        favoriteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        favoriteRecycler.setAdapter(favoriteAdapter);
        favoriteRecycler.setNestedScrollingEnabled(false);
    }

    /* ===================== Data & UI ===================== */
    private void loadInitialData() {
        todayWordText.setText("Loading...");
        todayWordReading.setText("");

        bottomProgressBar.setVisibility(View.VISIBLE);
        setTabButtonsEnabled(false);

        DataManager.initializeWords(() -> {
            if (getContext() == null || getView() == null) {
                return;
            }
            isDataLoaded = true;
            requireActivity().runOnUiThread(this::refreshAllDataAndUI);
        });
    }

    private void refreshAllDataAndUI() {
        if (getContext() == null || getView() == null || !isDataLoaded) return;

        bottomProgressBar.setVisibility(View.GONE);
        setTabButtonsEnabled(true);

        updateTodayWordUI(DataManager.getRandomWord());
        updateFavoriteList();
    }

    private void updateTodayWordUI(Word wordToDisplay) {
        todayWord = wordToDisplay;
        if (todayWord == null) {
            todayWordText.setText("Error");
            todayWordReading.setText("Failed to load word.");
            return;
        }
        isTodayFavorite = SharedPrefManager.isFavorite(requireContext(), todayWord.getId());
        todayWordText.setText(todayWord.getWord());
        todayWordReading.setText(todayWord.getReading());
        todayWordDate.setText("今日の単語");
        updateFavoriteIcon();
    }

    private void updateFavoriteList() {
        if (getContext() == null || !isDataLoaded) return;

        List<Integer> favoriteIds = SharedPrefManager.getFavorites(requireContext());
        List<Word> allWords = DataManager.getAllWords();
        if (allWords == null || allWords.isEmpty()) {
            favoriteAdapter.updateData(new ArrayList<>());
            standardCountText.setText("(0)");
            jiraiCountText.setText("(0)");
            return;
        }

        List<Word> standardFavorites = new ArrayList<>();
        List<Word> jiraiFavorites = new ArrayList<>();
        for (Word word : allWords) {
            if (favoriteIds.contains(word.getId())) {
                if ("standard".equals(word.getType())) {
                    standardFavorites.add(word);
                } else if ("jirai".equals(word.getType())) {
                    jiraiFavorites.add(word);
                }
            }
        }

        standardCountText.setText("(" + standardFavorites.size() + ")");
        jiraiCountText.setText("(" + jiraiFavorites.size() + ")");

        List<Word> filteredFavorites = "standard".equals(currentFavoriteType) ? standardFavorites : jiraiFavorites;
        favoriteAdapter.updateData(filteredFavorites);
        updateTabButtons();
    }

    private void setupListeners() {
        todayWordCard.setOnClickListener(v -> {
            if (todayWord != null) {
                openWordDetail(todayWord);
            }
        });

        todayWordFavoriteIcon.setOnClickListener(v -> {
            if (todayWord == null || !isDataLoaded) return;
            isTodayFavorite = !isTodayFavorite;
            if (isTodayFavorite) {
                SharedPrefManager.addFavorite(requireContext(), todayWord.getId());
            } else {
                SharedPrefManager.removeFavorite(requireContext(), todayWord.getId());
            }
            updateFavoriteIcon();
            updateFavoriteList(); // Refresh list after favorite change
        });

        skipButton.setOnClickListener(v -> {
            if (!isDataLoaded) return;
            Word newWord = DataManager.refreshTodayWord();
            updateTodayWordUI(newWord);
        });

        aiLabPromo.setOnClickListener(v -> startActivity(LabActivity.createIntent(requireContext())));

        standardTabButton.setOnClickListener(v -> {
            if (!isDataLoaded) return;
            currentFavoriteType = "standard";
            updateFavoriteList();
        });

        jiraiTabButton.setOnClickListener(v -> {
            if (!isDataLoaded) return;
            currentFavoriteType = "jirai";
            updateFavoriteList();
        });
    }

    private void updateFavoriteIcon() {
        if (todayWordFavoriteIcon != null) {
            todayWordFavoriteIcon.setActivated(isTodayFavorite);
        }
    }

    private void updateTabButtons() {
        standardTabButton.setSelected("standard".equals(currentFavoriteType));
        jiraiTabButton.setSelected("jirai".equals(currentFavoriteType));
    }

    private void setTabButtonsEnabled(boolean isEnabled) {
        if (standardTabButton != null && jiraiTabButton != null) {
            standardTabButton.setClickable(isEnabled);
            jiraiTabButton.setClickable(isEnabled);

            standardTabButton.setAlpha(isEnabled ? 1.0f : 0.5f);
            jiraiTabButton.setAlpha(isEnabled ? 1.0f : 0.5f);
        }
    }

    private void openWordDetail(Word word) {
        if (getContext() == null) return;
        Intent intent = new Intent(requireActivity(), WordDetailActivity.class);
        intent.putExtra("word_id", word.getId());
        startActivity(intent);
    }
}