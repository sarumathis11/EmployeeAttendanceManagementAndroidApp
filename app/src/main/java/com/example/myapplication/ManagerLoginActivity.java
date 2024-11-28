package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ManagerLoginActivity extends AppCompatActivity {

    Button loginUsernamePasswordButton, loginEmailOtpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_login); // Make sure this is your layout file

        // Initialize buttons
        loginUsernamePasswordButton = findViewById(R.id.loginUsernamePasswordButton);
        loginEmailOtpButton = findViewById(R.id.loginEmailOtpButton);

        // Button click listener for "Login with Username & Password"
        loginUsernamePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the Username & Password login screen
                Intent intent = new Intent(ManagerLoginActivity.this, UsernamePasswordLoginActivity.class);
                startActivity(intent);
            }
        });

        // Button click listener for "Login with Email & OTP"
        loginEmailOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the Email & OTP login screen
                Intent intent = new Intent(ManagerLoginActivity.this, EmailOTPLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
