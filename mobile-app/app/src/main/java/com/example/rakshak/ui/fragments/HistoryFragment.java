package com.example.rakshak.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rakshak.R;
import com.example.rakshak.model.AccidentHistory;
import com.example.rakshak.ui.adapters.HistoryAdapter;
import com.example.rakshak.ui.viewmodels.HistoryViewModel;
import com.google.android.material.chip.Chip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HistoryFragment extends Fragment {

    private HistoryViewModel viewModel;
    private HistoryAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout emptyStateView;
    private TextView tvTotalIncidents, tvThisMonth;
    private List<AccidentHistory> fullHistoryList=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerView();
    

        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        observeViewModel();

        // Fetch the data from the backend
        viewModel.fetchHistory();
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.rv_history);

        tvTotalIncidents = view.findViewById(R.id.tv_total_incidents);
        tvThisMonth = view.findViewById(R.id.tv_this_month);
        emptyStateView = view.findViewById(R.id.dialogs);
        
        // Setup chip listeners
        Chip chipAll = view.findViewById(R.id.chip_all);
        Chip chipToday = view.findViewById(R.id.chip_today);
        Chip chipWeek = view.findViewById(R.id.chip_week);
        Chip chipMonth = view.findViewById(R.id.chip_month);

        chipAll.setOnClickListener(v -> filterList("all"));
        chipToday.setOnClickListener(v -> filterList("today"));
        chipWeek.setOnClickListener(v -> filterList("week"));
        chipMonth.setOnClickListener(v -> filterList("month"));
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getHistory().observe(getViewLifecycleOwner(), historyItems -> {
            if (historyItems == null || historyItems.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyStateView.setVisibility(View.VISIBLE);
                fullHistoryList.clear();
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyStateView.setVisibility(View.GONE);
                fullHistoryList=historyItems;
            }
            updateStats();
            filterList("all");
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        });
    }
    private void updateStats() {
        if (fullHistoryList == null) return;
        
        tvTotalIncidents.setText(String.valueOf(fullHistoryList.size()));

        int thisMonthCount = 0;
        for (AccidentHistory item : fullHistoryList) {
            if (isThisMonth(item)) {
                thisMonthCount++;
            }
        }
        tvThisMonth.setText(String.valueOf(thisMonthCount));
    }
    private void filterList(String period) {
        if (fullHistoryList == null) return;

        List<AccidentHistory> filteredList=new ArrayList<>();

       switch (period) {
            case "today":
                for (AccidentHistory item : fullHistoryList) {
                    if (isToday(item)) {
                        filteredList.add(item);
                    }
                }
                break;
            case "week":
                for (AccidentHistory item : fullHistoryList) {
                    if (isThisWeek(item)) {
                        filteredList.add(item);
                    }
                }
                break;
            case "month":
                for (AccidentHistory item : fullHistoryList) {
                    if (isThisMonth(item)) {
                        filteredList.add(item);
                    }
                }
                break;
            default:
                filteredList.addAll(fullHistoryList);
                break;
        }
        adapter.submitList(filteredList);
    }

    // Helper methods for filtering
    private boolean isToday(AccidentHistory item) {
        Calendar now = Calendar.getInstance();
        Calendar itemCal = parseTimestamp(item.getTimestamp());
        return itemCal != null && now.get(Calendar.DAY_OF_YEAR) == itemCal.get(Calendar.DAY_OF_YEAR) &&
               now.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR);
    }

    private boolean isThisWeek(AccidentHistory item) {
        Calendar now = Calendar.getInstance();
        Calendar itemCal = parseTimestamp(item.getTimestamp());
        return itemCal != null && now.get(Calendar.WEEK_OF_YEAR) == itemCal.get(Calendar.WEEK_OF_YEAR) &&
               now.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR);
    }

    private boolean isThisMonth(AccidentHistory item) {
        Calendar now = Calendar.getInstance();
        Calendar itemCal = parseTimestamp(item.getTimestamp());
        return itemCal != null && now.get(Calendar.MONTH) == itemCal.get(Calendar.MONTH) &&
               now.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR);
    }
    
    private Calendar parseTimestamp(String timestamp) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = parser.parse(timestamp);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (ParseException e) {
            return null;
        }
    }
}