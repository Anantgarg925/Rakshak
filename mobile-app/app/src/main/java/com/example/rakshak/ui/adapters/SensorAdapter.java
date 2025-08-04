package com.example.rakshak.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rakshak.R;
import com.example.rakshak.model.SensorData;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    private final List<SensorData> sensorDataList;

    public SensorAdapter(List<SensorData> initialSensorDataList) {
        this.sensorDataList = (initialSensorDataList != null) ? new ArrayList<>(initialSensorDataList) : new ArrayList<>();
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        SensorData sensor = sensorDataList.get(position);
        holder.bind(sensor);
    }

    @Override
    public int getItemCount() {
        return sensorDataList.size();
    }

    public void updateSensorData(List<SensorData> newSensorDataList) {
        final List<SensorData> oldList = new ArrayList<>(this.sensorDataList);
        final List<SensorData> newList = (newSensorDataList != null) ? newSensorDataList : new ArrayList<>();

        SensorDiffCallback diffCallback = new SensorDiffCallback(oldList, newList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.sensorDataList.clear();
        this.sensorDataList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    // FIX 2: Added 'public' to the class definition
    public static class SensorViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivSensorIcon;
        private final ImageView ivStatusIcon;
        private final TextView tvSensorName, tvSensorValue, tvSensorUnit, tvSensorStatus;
        private final View statusIndicator;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSensorIcon = itemView.findViewById(R.id.iv_sensor_icon);
            ivStatusIcon = itemView.findViewById(R.id.iv_status_icon);
            tvSensorName = itemView.findViewById(R.id.tv_sensor_name);
            tvSensorValue = itemView.findViewById(R.id.tv_sensor_value);
            tvSensorUnit = itemView.findViewById(R.id.tv_sensor_unit);
            tvSensorStatus = itemView.findViewById(R.id.tv_sensor_status);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
        }

        public void bind(SensorData sensor) {
            if (sensor == null) {
                return;
            }

            tvSensorName.setText(sensor.getName());

            if ("Accelerometer".equals(sensor.getName())) {
                tvSensorValue.setText(sensor.getFormattedValue());
            } else {
                tvSensorValue.setText(sensor.getFormattedValue());
            }

            tvSensorUnit.setText(sensor.getUnit());

            // FIX 1: Convert the enum to a string with .name() before using it
            tvSensorStatus.setText(sensor.getStatus() != null ? sensor.getStatus().name().toUpperCase() : "UNKNOWN");
            setSensorIcon(sensor.getName());
            setStatusStyling(sensor.getStatus() != null ? sensor.getStatus().name() : "inactive");
        }

        private void setSensorIcon(String sensorName) {
            if (sensorName == null) {
                ivSensorIcon.setImageResource(R.drawable.ic_sensor);
                return;
            }
            switch (sensorName) {
                case "Accelerometer":
                    ivSensorIcon.setImageResource(R.drawable.ic_activity);
                    break;
                case "GPS Location":
                    ivSensorIcon.setImageResource(R.drawable.ic_location);
                    break;
                case "Connectivity":
                    ivSensorIcon.setImageResource(R.drawable.ic_wifi);
                    break;
                case "Device Status":
                    ivSensorIcon.setImageResource(R.drawable.ic_battery);
                    break;
                default:
                    ivSensorIcon.setImageResource(R.drawable.ic_sensor);
                    break;
            }
        }

        private void setStatusStyling(String status) {
            int statusColor;
            int statusIconRes;
            int backgroundColor;

            String currentStatus = (status != null) ? status.toLowerCase(Locale.ROOT) : "inactive";

            switch (currentStatus) {
                case "active":
                    statusColor = itemView.getContext().getColor(R.color.success_green);
                    statusIconRes = R.drawable.ic_check_circle;
                    backgroundColor = itemView.getContext().getColor(R.color.success_green_light);
                    break;
                case "alert":
                    statusColor = itemView.getContext().getColor(R.color.emergency_red);
                    statusIconRes = R.drawable.ic_alert_triangle;
                    backgroundColor = itemView.getContext().getColor(R.color.emergency_red_light);
                    break;
                case "searching":
                    statusColor = itemView.getContext().getColor(R.color.warning_yellow);
                    statusIconRes = R.drawable.ic_clock;
                    backgroundColor = itemView.getContext().getColor(R.color.warning_yellow_light);
                    break;
                default: // Handles "inactive" and any other cases
                    statusColor = itemView.getContext().getColor(R.color.emergency_gray);
                    statusIconRes = R.drawable.ic_x_circle;
                    backgroundColor = itemView.getContext().getColor(R.color.emergency_gray_light);
                    break;
            }

            tvSensorStatus.setTextColor(statusColor);
            ivStatusIcon.setImageResource(statusIconRes);
            ivStatusIcon.setColorFilter(statusColor);
            statusIndicator.setBackgroundColor(backgroundColor);
            ivSensorIcon.setColorFilter(statusColor);
        }
    }

    private static class SensorDiffCallback extends DiffUtil.Callback {
        private final List<SensorData> oldList;
        private final List<SensorData> newList;

        public SensorDiffCallback(List<SensorData> oldList, List<SensorData> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            SensorData oldSensor = oldList.get(oldItemPosition);
            SensorData newSensor = newList.get(newItemPosition);
            if (oldSensor == null || newSensor == null || oldSensor.getName() == null || newSensor.getName() == null) {
                return false;
            }
            return oldSensor.getName().equals(newSensor.getName());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            SensorData oldSensor = oldList.get(oldItemPosition);
            SensorData newSensor = newList.get(newItemPosition);

            if (oldSensor == null && newSensor == null) return true;
            if (oldSensor == null || newSensor == null) return false;

            // Use the Objects.equals for proper null-safe comparison of enums
            return Objects.equals(oldSensor.getName(), newSensor.getName()) &&
                    oldSensor.getValue() == newSensor.getValue() &&
                    Objects.equals(oldSensor.getUnit(), newSensor.getUnit()) &&
                    Objects.equals(oldSensor.getStatus(), newSensor.getStatus());
        }
    }
}