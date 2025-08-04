
package com.example.rakshak.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rakshak.R;
import com.example.rakshak.model.EmergencyContact;
import com.example.rakshak.model.User;
import com.example.rakshak.ui.activities.LoginActivity;
import com.example.rakshak.ui.adapters.EmergencyContactAdapter;
import com.example.rakshak.ui.viewmodels.ProfileViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private EmergencyContactAdapter contactAdapter;

    private LinearLayout loggedInView, loggedOutView;
    private Button btnLogin, btnLogout;
    private ImageButton btnEditProfile, btnAddContact, btnEditEmergencyInfo;
    private RecyclerView recyclerView;
    private TextView tvUserName, tvUserEmail, tvUserPhone, tvEmergencyInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        initializeViews(view);
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void initializeViews(View view) {
        loggedInView = view.findViewById(R.id.view_logged_in);
        loggedOutView = view.findViewById(R.id.view_logged_out);
        btnLogin = view.findViewById(R.id.btn_login);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        recyclerView = view.findViewById(R.id.recycler_view_contacts);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserPhone = view.findViewById(R.id.tv_user_phone);
        tvEmergencyInfo = view.findViewById(R.id.tv_emergency_info_body);
        btnAddContact = view.findViewById(R.id.btn_add_contact);
        btnEditEmergencyInfo = view.findViewById(R.id.btn_edit_emergency_info);
    }

    private void setupRecyclerView() {
        contactAdapter = new EmergencyContactAdapter(contact -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Contact")
                    .setMessage("Are you sure you want to delete " + contact.getName() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> viewModel.deleteContact(contact))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(contactAdapter);
    }

    private void observeViewModel() {
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvUserName.setText(user.getName());
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fbUser != null) tvUserEmail.setText(fbUser.getEmail());
                tvUserPhone.setText(user.getPhoneNumber());
                String blood = user.getBloodType() != null ? user.getBloodType() : "Not Set";
                String cond = user.getMedicalConditions() != null ? user.getMedicalConditions() : "None";
                String allergy = user.getAllergies() != null ? user.getAllergies() : "None";
                String emergencyText = "• Blood Type: " + blood + "\n• Conditions: " + cond + "\n• Allergies: " + allergy;
                tvEmergencyInfo.setText(emergencyText);
            }
        });
        viewModel.getContacts().observe(getViewLifecycleOwner(), contacts -> contactAdapter.submitList(contacts));
        viewModel.getError().observe(getViewLifecycleOwner(), error -> Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show());
    }

    private void checkUserStatus() {
        boolean isLoggedIn = FirebaseAuth.getInstance().getCurrentUser() != null;
        loggedInView.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        loggedOutView.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        if (isLoggedIn) {
            viewModel.fetchUser();
            viewModel.fetchContacts();
        }
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            checkUserStatus();
        });
        btnAddContact.setOnClickListener(v -> showAddContactDialog());
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnEditEmergencyInfo.setOnClickListener(v -> showEditEmergencyInfoDialog());
    }

    private void showEditProfileDialog() {
        User user = viewModel.getUser().getValue();
        if (user == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Profile");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        final EditText etName = dialogView.findViewById(R.id.et_edit_name);
        final EditText etPhone = dialogView.findViewById(R.id.et_edit_phone);
        etName.setText(user.getName());
        etPhone.setText(user.getPhoneNumber());
        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            user.setName(etName.getText().toString().trim());
            user.setPhoneNumber(etPhone.getText().toString().trim());
            viewModel.updateUser(user);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditEmergencyInfoDialog() {
        User user = viewModel.getUser().getValue();
        if (user == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Emergency Info");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_emergency_info, null);
        final EditText etBlood = dialogView.findViewById(R.id.et_edit_blood_type);
        final EditText etConditions = dialogView.findViewById(R.id.et_edit_conditions);
        final EditText etAllergies = dialogView.findViewById(R.id.et_edit_allergies);
        etBlood.setText(user.getBloodType());
        etConditions.setText(user.getMedicalConditions());
        etAllergies.setText(user.getAllergies());
        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            user.setBloodType(etBlood.getText().toString().trim());
            user.setMedicalConditions(etConditions.getText().toString().trim());
            user.setAllergies(etAllergies.getText().toString().trim());
            viewModel.updateUser(user);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Emergency Contact");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);
        final EditText etName = dialogView.findViewById(R.id.et_contact_name);
        final EditText etPhone = dialogView.findViewById(R.id.et_contact_phone);
        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty()) {
                EmergencyContact newContact = new EmergencyContact();
                newContact.setName(name);
                newContact.setPhoneNumber(phone);
                viewModel.addContact(newContact);
            } else {
                Toast.makeText(getContext(), "Name and phone cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
