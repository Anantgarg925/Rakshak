package com.example.rakshak.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rakshak.R;
import com.example.rakshak.model.ActiveAlert;
import java.util.ArrayList;
import java.util.List;

public class ActiveAlertsAdapter extends RecyclerView.Adapter<ActiveAlertsAdapter.ViewHolder> {

    private List<ActiveAlert> alerts = new ArrayList<>();
    private OnAlertClickListener listener;

    public interface OnAlertClickListener {
        void onAlertClick(ActiveAlert alert);
    }

    public ActiveAlertsAdapter(OnAlertClickListener listener) {
        this.listener = listener;
    }

    public void updateAlerts(List<ActiveAlert> newAlerts) {
        alerts = newAlerts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_alert, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActiveAlert alert = alerts.get(position);
        holder.bind(alert);
        holder.itemView.setOnClickListener(v -> listener.onAlertClick(alert));
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAlertId, tvLocation, tvTime, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlertId = itemView.findViewById(R.id.tv_alert_id);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        public void bind(ActiveAlert alert) {
            tvAlertId.setText("Alert #" + alert.getId());
            tvLocation.setText(alert.getLocation());
            tvTime.setText(alert.getTime());
            tvStatus.setText(alert.getStatusText());
        }
    }
}