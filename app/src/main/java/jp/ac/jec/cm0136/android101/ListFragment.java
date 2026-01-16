package jp.ac.jec.cm0136.android101;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import java.util.List;

public class ListFragment extends Fragment {

    private EditText searchEditText;
    private Spinner filterSpinner;
    private ImageView searchIcon;
    private View emptyView;
    private ListView wordListView;

    private WordAdapter adapter;
    private List<Word> allWords;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        initViews(view);
        setupSpinner();
        setupListView();
        setupSearch();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.search_edit_text);
        filterSpinner = view.findViewById(R.id.filter_spinner);
        searchIcon = view.findViewById(R.id.search_icon);
        emptyView = view.findViewById(R.id.empty_text_view);
        wordListView = view.findViewById(R.id.word_list_view);
    }

    private void setupSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"すべて", "定番", "地雷"}
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                filterWords(searchEditText.getText().toString(), selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupListView() {
        allWords = DataManager.getAllWords();
        adapter = new WordAdapter(requireContext(), allWords);
        wordListView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filter = filterSpinner.getSelectedItem().toString();
                filterWords(s.toString(), filter);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchIcon.setOnClickListener(v -> {
            String filter = filterSpinner.getSelectedItem().toString();
            filterWords(searchEditText.getText().toString(), filter);
        });
    }

    private void filterWords(String query, String filterType) {
        if (adapter != null) {
            adapter.filter(query, filterType);

            if (adapter.getCount() == 0) {
                emptyView.setVisibility(View.VISIBLE);
                wordListView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                wordListView.setVisibility(View.VISIBLE);
            }
        }
    }
}