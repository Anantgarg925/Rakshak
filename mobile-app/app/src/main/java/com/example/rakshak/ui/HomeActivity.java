package com.example.rakshak.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.rakshak.R;
import com.example.rakshak.model.User;
import com.example.rakshak.ui.activities.LoginActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display user info if passed from registration
        displayUserInfo();

        // Initialize other UI components
        TextView welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText(getString(R.string.your_safety_our_priority));
    }

    @SuppressLint("StringFormatInvalid")
    private void displayUserInfo() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_DATA")) {
            User user = (User) intent.getParcelableExtra("USER_DATA");
            TextView userInfo = findViewById(R.id.user_info);
            assert user != null;
            userInfo.setText(getString(R.string.user_info_format,
                    user.getName(),
                    user.getPhoneNumber()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Clear session/data if needed
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
