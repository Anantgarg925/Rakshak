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
import com.example.rakshak.model.EmergencyContact;

public class EmergencyContactAdapter extends ListAdapter<EmergencyContact, EmergencyContactAdapter.ContactViewHolder> {

    private OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onDeleteClick(EmergencyContact contact);
    }

    public EmergencyContactAdapter(OnDeleteClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<EmergencyContact> DIFF_CALLBACK = new DiffUtil.ItemCallback<EmergencyContact>() {
        @Override
        public boolean areItemsTheSame(@NonNull EmergencyContact oldItem, @NonNull EmergencyContact newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull EmergencyContact oldItem, @NonNull EmergencyContact newItem) {
            return oldItem.getName().equals(newItem.getName()) && oldItem.getPhoneNumber().equals(newItem.getPhoneNumber());
        }
    };

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        EmergencyContact currentContact = getItem(position);
        holder.nameTextView.setText(currentContact.getName());
        holder.phoneTextView.setText(currentContact.getPhoneNumber());
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(currentContact));
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView phoneTextView;
        private final ImageView deleteButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneTextView = itemView.findViewById(R.id.contact_phone);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}