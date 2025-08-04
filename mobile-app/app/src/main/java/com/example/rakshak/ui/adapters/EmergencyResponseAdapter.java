package com.example.rakshak.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rakshak.R;
import com.example.rakshak.model.EmergencyRecord;
import java.util.ArrayList;
import java.util.List;

public class EmergencyResponseAdapter extends RecyclerView.Adapter<EmergencyResponseAdapter.ViewHolder> {

    private List<EmergencyRecord> responses = new ArrayList<>();
    private OnResponseClickListener listener;

    public interface OnResponseClickListener {
        void onResponseClick(EmergencyRecord response);
    }

    public EmergencyResponseAdapter(OnResponseClickListener listener) {
        this.listener = listener;
    }

    public void updateResponses(List<EmergencyRecord> newResponses) {
        responses = newResponses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_emergency_response, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmergencyRecord response = responses.get(position);
        holder.bind(response);
        holder.itemView.setOnClickListener(v -> listener.onResponseClick(response));
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvResponseId, tvAlertId, tvResponder, tvTime, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResponseId = itemView.findViewById(R.id.tv_response_id);
            tvAlertId = itemView.findViewById(R.id.tv_alert_id);
            tvResponder = itemView.findViewById(R.id.tv_responder);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        public void bind(EmergencyRecord response) {
            tvResponseId.setText("RES#" + response.getId());
            tvAlertId.setText("For Alert #" + response.getAlertId());
            tvResponder.setText(response.getResponderId());
            tvTime.setText(response.getResponseTime());
            tvStatus.setText(response.getStatus().ordinal());
        }
    }
}