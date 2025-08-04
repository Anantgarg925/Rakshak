package com.example.rakshak.ui;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rakshak.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends AppCompatActivity {
    
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private TextView emailTextView;
    private Button resendButton, continueButton;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        
        mAuth = FirebaseAuth.getInstance();
        userEmail = getIntent().getStringExtra("email");
        
        // Initialize views
        emailTextView = findViewById(R.id.textEmail);
        resendButton = findViewById(R.id.buttonResend);
        continueButton = findViewById(R.id.buttonContinue);
        progressDialog = new ProgressDialog(this);
        
        emailTextView.setText(userEmail);
        
        resendButton.setOnClickListener(v -> resendVerificationEmail());
        continueButton.setOnClickListener(v -> checkEmailVerification());
        
        // Start checking verification status periodically
        startVerificationCheck();
    }

    private void startVerificationCheck() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    user.reload().addOnCompleteListener(task -> {
                        if (user.isEmailVerified()) {
                            navigateToHome();
                        } else {
                            handler.postDelayed(this, 5000); // Check every 5 seconds
                        }
                    });
                }
            }
        }, 5000);
    }

    private void resendVerificationEmail() {
        progressDialog.setMessage("Resending verification email...");
        progressDialog.show();
        
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Verification email resent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to resend: " + task.getException().getMessage(), 
                                     Toast.LENGTH_LONG).show();
                    }
                });
        }
    }

    private void checkEmailVerification() {
        progressDialog.setMessage("Checking verification status...");
        progressDialog.show();
        
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (user.isEmailVerified()) {
                    navigateToHome();
                } else {
                    Toast.makeText(this, "Email not verified yet. Please check your inbox.", 
                                 Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void navigateToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}