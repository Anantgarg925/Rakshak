package com.example.rakshak.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rakshak.R;
import com.example.rakshak.data.database.NotificationHistoryItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationAdapter extends ListAdapter<NotificationHistoryItem, NotificationAdapter.NotificationViewHolder> {

    public NotificationAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<NotificationHistoryItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<NotificationHistoryItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull NotificationHistoryItem oldItem, @NonNull NotificationHistoryItem newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull NotificationHistoryItem oldItem, @NonNull NotificationHistoryItem newItem) {
            return oldItem.title.equals(newItem.title) &&
                    oldItem.message.equals(newItem.message) &&
                    oldItem.timestamp == newItem.timestamp;
        }
    };

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationHistoryItem currentNotification = getItem(position);
        holder.titleTextView.setText(currentNotification.title);
        holder.messageTextView.setText(currentNotification.message);

        // Format the timestamp to a readable date/time string
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String formattedDate = sdf.format(new Date(currentNotification.timestamp));
        holder.timestampTextView.setText(formattedDate);
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView messageTextView;
        private final TextView timestampTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_view_notification_title);
            messageTextView = itemView.findViewById(R.id.text_view_notification_message);
            timestampTextView = itemView.findViewById(R.id.text_view_notification_timestamp);
        }
    }
}