package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UsernamePasswordLoginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton;

    // Initialize Firebase Database reference
    FirebaseDatabase database;
    DatabaseReference managersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_password_login); // Make sure this layout file exists

        // Initialize views
        usernameEditText = findViewById(R.id.usernameInput);
        passwordEditText = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        managersRef = database.getReference("managers");

        // Set click listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(UsernamePasswordLoginActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Query the Firebase database for the manager with the entered username
                managersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Loop through all the managers to check if the password matches
                            for (DataSnapshot managerSnapshot : dataSnapshot.getChildren()) {
                                String storedPassword = managerSnapshot.child("password").getValue(String.class);

                                if (storedPassword != null && storedPassword.equals(password)) {
                                    // Successful login, open the Manager Dashboard
                                    Intent intent = new Intent(UsernamePasswordLoginActivity.this, ManagerDashboardActivity.class);
                                    startActivity(intent);
                                    finish();
                                    return;
                                }
                            }
                            // Password doesn't match
                            Toast.makeText(UsernamePasswordLoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                        } else {
                            // Username doesn't exist
                            Toast.makeText(UsernamePasswordLoginActivity.this, "Manager not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(UsernamePasswordLoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
