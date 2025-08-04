package com.example.rakshak.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import com.example.rakshak.MainActivity;
import com.example.rakshak.R;
import com.example.rakshak.model.User;
import com.example.rakshak.repository.UserRepository;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Button buttonGoogleSignIn;

     private EditText etPhoneNumber, etOtp;
    private Button btnSendOtp, btnVerifyOtp;
    private LinearLayout phoneAuthLayout, otpVerifyLayout;

    private String verificationId;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        buttonGoogleSignIn = findViewById(R.id.buttonGoogleSignIn);
        progressBar = findViewById(R.id.progress_bar_login);
         etPhoneNumber = findViewById(R.id.et_phone_number);
        etOtp = findViewById(R.id.et_otp);
        btnSendOtp = findViewById(R.id.btn_send_otp);
        btnVerifyOtp = findViewById(R.id.btn_verify_otp);
        phoneAuthLayout = findViewById(R.id.phone_auth_layout);
        otpVerifyLayout = findViewById(R.id.otp_verify_layout);

        if (mAuth.getCurrentUser() != null) {
            navigateToHome();
            return;
        }

        buttonGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
         btnSendOtp.setOnClickListener(v -> sendVerificationCode());
        btnVerifyOtp.setOnClickListener(v -> verifyCode());
    }
     private void sendVerificationCode() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }
         progressBar.setVisibility(View.VISIBLE);
        
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
     private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    // This is often auto-retrieved on some devices.
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    progressBar.setVisibility(View.GONE);
                    Log.w(TAG, "onVerificationFailed", e);
                    Toast.makeText(LoginActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    progressBar.setVisibility(View.GONE);
                    LoginActivity.this.verificationId = verificationId;
                    // Switch UI to OTP entry mode
                    phoneAuthLayout.setVisibility(View.GONE);
                    otpVerifyLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode() {
        String code = etOtp.getText().toString().trim();
        if (code.isEmpty() || code.length() < 6) {
            Toast.makeText(this, "Please enter the 6-digit OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            // If the user is new, their display name will be null
                            if (user.getDisplayName() == null || user.getDisplayName().isEmpty()) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName("User " + user.getPhoneNumber().substring(user.getPhoneNumber().length() - 4))
                                        .build();
                                user.updateProfile(profileUpdates);
                            }
                            registerUserWithBackend(user);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Sign-in failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void signInWithGoogle() {
        progressBar.setVisibility(View.VISIBLE);
        buttonGoogleSignIn.setEnabled(false);

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        CredentialManager credentialManager = CredentialManager.create(this);
        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                getMainExecutor(),
                new androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        try {
                            GoogleIdTokenCredential credential = GoogleIdTokenCredential.createFrom(result.getCredential().getData());
                            String idToken = credential.getIdToken();
                            firebaseAuthWithGoogle(idToken);
                        } catch (Exception e) {
                            Log.e(TAG, "Credential parsing failed", e);
                            handleSignInError("Failed to parse Google credential.");
                        }
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        if (e instanceof NoCredentialException) {
                            Toast.makeText(LoginActivity.this, "No Google account found. Please add one.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
                            intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
                            startActivity(intent);
                        } else if (e instanceof GetCredentialCancellationException) {
                            Log.w(TAG, "User cancelled the sign-in process.");
                        }
                        handleSignInError(e.getMessage());
                    }
                }
        );
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            registerUserWithBackend(user);
                        } else {
                            navigateToHome(); // Should not happen, but as a fallback
                        }
                    } else {
                        handleSignInError("Firebase authentication failed.");
                    }
                });
    }

    private void registerUserWithBackend(FirebaseUser firebaseUser) {
        String name = firebaseUser.getDisplayName();
        String phoneNumber = firebaseUser.getPhoneNumber();
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        User appUser = new User(name, phoneNumber, "_placeholder_", deviceId);

        UserRepository userRepository = new UserRepository();

        // FIX: Use the correct UserCallback interface and handle the Map response
        userRepository.registerUser(appUser, new UserRepository.UserCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> data) {
                String message = data != null ? data.get("message") : "Registered";
                Log.i("LoginActivity", "Backend registration successful: " + message);
                navigateToHome();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("LoginActivity", "Backend registration failed: " + errorMessage);
                // If registration fails (e.g., user already exists with code 409),
                // we still let them in.
                navigateToHome();
            }
        });
    }

    private void navigateToHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void handleSignInError(String message) {
        progressBar.setVisibility(View.GONE);
        buttonGoogleSignIn.setEnabled(true);
        Toast.makeText(this, "Sign-in failed: " + message, Toast.LENGTH_LONG).show();
    }
}
