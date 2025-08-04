package com.example.rakshak.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rakshak.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class RegisterActivity extends AppCompatActivity {

    private EditText editName, editEmail, editPassword, editPhone, editEmergency, editDeviceId;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editPhone = findViewById(R.id.editPhone);
        editEmergency = findViewById(R.id.editEmergency);
        editDeviceId = findViewById(R.id.editDeviceId);
        Button btnRegister = findViewById(R.id.btnRegister);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);

        // Set click listener
        btnRegister.setOnClickListener(v -> attemptRegistration());
    }

    private void attemptRegistration() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String emergency = editEmergency.getText().toString().trim();
        String deviceId = editDeviceId.getText().toString().trim();

        if (validateInputs(name, email, password, phone, emergency, deviceId)) {
            registerUser(name, email, password, phone, emergency, deviceId);
        }
    }

    private boolean validateInputs(String name, String email, String password,
                                   String phone, String emergency, String deviceId) {
        boolean valid = true;

        if (name.isEmpty()) {
            editName.setError("Name is required");
            valid = false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Valid email is required");
            valid = false;
        }

        if (password.isEmpty() || password.length() < 6) {
            editPassword.setError("Password must be at least 6 characters");
            valid = false;
        }

        if (phone.isEmpty() || phone.length() < 10) {
            editPhone.setError("Valid phone number is required");
            valid = false;
        }

        if (emergency.isEmpty() || emergency.length() < 10) {
            editEmergency.setError("Valid emergency number is required");
            valid = false;
        }

        if (deviceId.isEmpty()) {
            editDeviceId.setError("Device ID is required");
            valid = false;
        }

        return valid;
    }

    private void registerUser(String name, String email, String password,
                              String phone, String emergency, String deviceId) {
        showProgressDialog("Resending verification...");

        // Create user with email/password in Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration success, save additional user data to Firestore
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                saveUserToFirestore(firebaseUser.getUid(), name, email, phone, emergency, deviceId);
                            }
                        } else {
                            // Registration failed
                            dismissProgressDialog();
                            Toast.makeText(RegisterActivity.this, "Registration failed: " +
                                            task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    //method to handle email verification
// Add these new methods to your RegisterActivity

private void sendVerificationEmail(FirebaseUser user, String name, String email, 
                                 String phone, String emergency, String deviceId) {
    user.sendEmailVerification()
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Store verification timestamp
                Map<String, Object> verificationData = new HashMap<>();
                verificationData.put("verificationSentAt", new Date());
                
                db.collection("verificationAttempts").document(user.getUid())
                    .set(verificationData)
                    .addOnSuccessListener(aVoid -> {
                        showVerificationDialog(user, name, email, phone, emergency, deviceId);
                    });
            } else {
                handleVerificationError(user, task.getException());
            }
        });
}

    private void handleVerificationError(FirebaseUser user, Exception exception) {
    }

    private void showVerificationDialog(FirebaseUser user, String name, String email,
                                  String phone, String emergency, String deviceId) {
    new MaterialAlertDialogBuilder(this)
        .setTitle("Verify Your Email")
        .setMessage("We've sent a verification email to " + user.getEmail() + 
                   ". Please verify your email to continue.")
        .setPositiveButton("Continue", (dialog, which) -> {
            saveUserToFirestore(user.getUid(), name, email, phone, emergency, deviceId);
            navigateToEmailVerificationActivity(user);
        })
        .setNegativeButton("Resend Email", (dialog, which) -> {
            resendVerificationEmail(user, name, email, phone, emergency, deviceId);
        })
        .setCancelable(false)
        .show();
}

private void resendVerificationEmail(FirebaseUser user, String name, String email, 
                                   String phone, String emergency, String deviceId) {
    showProgressDialog("Resending verification...");
    
    // Check if we should allow resend (prevent spam)
    db.collection("verificationAttempts").document(user.getUid())
        .get()
        .addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Date lastSent = task.getResult().getDate("verificationSentAt");
                if (lastSent != null && System.currentTimeMillis() - lastSent.getTime() < 60000) {
                    // Only allow resend once per minute
                    dismissProgressDialog();
                    Toast.makeText(this, "Please wait 1 minute before resending", 
                                 Toast.LENGTH_SHORT).show();
                    showVerificationDialog(user, name, email, phone, emergency, deviceId);
                    return;
                }
            }
            // If checks pass, resend
            sendVerificationEmail(user, name, email, phone, emergency, deviceId);
        });
}

private void navigateToEmailVerificationActivity(FirebaseUser user) {
    Intent intent = new Intent(this, EmailVerificationActivity.class);
    intent.putExtra("email", user.getEmail());
    startActivity(intent);
    finish();
}

    private void saveUserToFirestore(String userId, String name, String email,
                                     String phone, String emergency, String deviceId) {
        // Create a new user document in Firestore
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("phone", phone);
        user.put("emergencyContact", emergency);
        user.put("deviceId", deviceId);
        user.put("createdAt", System.currentTimeMillis());

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    dismissProgressDialog();
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    navigateToHomeScreen();
                })
                .addOnFailureListener(e -> {
                    dismissProgressDialog();
                    Toast.makeText(RegisterActivity.this, "Failed to save user data: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    // Optional: delete the Firebase Auth user if Firestore fails
                    mAuth.getCurrentUser().delete();
                });
    }

    private void showProgressDialog(String s) {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void navigateToHomeScreen() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}