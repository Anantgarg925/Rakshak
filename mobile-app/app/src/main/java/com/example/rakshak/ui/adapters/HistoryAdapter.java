package com.example.rakshak.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rakshak.R;
import com.example.rakshak.model.AccidentHistory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter extends ListAdapter<AccidentHistory, HistoryAdapter.HistoryViewHolder> {

    public HistoryAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<AccidentHistory> DIFF_CALLBACK = new DiffUtil.ItemCallback<AccidentHistory>() {
        @Override
        public boolean areItemsTheSame(@NonNull AccidentHistory oldItem, @NonNull AccidentHistory newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull AccidentHistory oldItem, @NonNull AccidentHistory newItem) {
            return oldItem.getTimestamp().equals(newItem.getTimestamp());
        }
    };

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        AccidentHistory currentItem = getItem(position);

        // Format the timestamp from the backend
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = parser.parse(currentItem.getTimestamp());

            String timeString = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(date);
            holder.tvAlertTime.setText(timeString);
        } catch (ParseException e) {
            holder.tvAlertTime.setText("Invalid Date");
        }

        // Set the location text
        String locationText = String.format(Locale.US, "📍 Lat: %.4f, Lng: %.4f",
                currentItem.getLatitude(), currentItem.getLongitude());
        holder.tvAlertLocation.setText(locationText);

        // For now, status is hardcoded as in your layout. You can add a 'status' field later.
        holder.tvAlertStatus.setText("Resolved");
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAlertTitle, tvAlertStatus, tvAlertTime, tvAlertLocation;
        private final ImageView ivAlertIcon;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlertTitle = itemView.findViewById(R.id.tv_alert_title);
            tvAlertStatus = itemView.findViewById(R.id.tv_alert_status);
            tvAlertTime = itemView.findViewById(R.id.tv_alert_time);
            tvAlertLocation = itemView.findViewById(R.id.tv_alert_location);
            ivAlertIcon = itemView.findViewById(R.id.iv_alert_icon);
        }
    }
}