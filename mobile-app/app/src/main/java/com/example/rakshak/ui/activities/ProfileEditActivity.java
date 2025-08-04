package com.example.rakshak.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.rakshak.R; // Make sure this import is correct

public class ProfileEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout file for this activity.
        // You will need to create an XML layout file named 'activity_profile_edit.xml'
        setContentView(R.layout.activity_profile_edit);
    }
}