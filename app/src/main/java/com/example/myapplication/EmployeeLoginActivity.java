package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmployeeLoginActivity extends AppCompatActivity {

    private DatabaseReference employeesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_login);

        // Initialize Firebase reference
        employeesRef = FirebaseDatabase.getInstance().getReference("employees");

        // Find Views
        EditText usernameEditText = findViewById(R.id.editTextUsername);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.loginButton);

        // Set Login Button Action
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Validate fields
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(EmployeeLoginActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Query Firebase for the username
                employeesRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Check password
                            for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
                                String dbPassword = employeeSnapshot.child("password").getValue(String.class);
                                if (dbPassword != null && dbPassword.equals(password)) {
                                    // Login successful
                                    String employeeId = employeeSnapshot.getKey();

                                    Intent intent = new Intent(EmployeeLoginActivity.this, EmployeeDashboardActivity.class);
                                    intent.putExtra("employeeId", employeeId); // Pass employeeId
                                    startActivity(intent);
                                    finish(); // Close Login Activity
                                    return;
                                }
                            }
                            // Invalid password
                            Toast.makeText(EmployeeLoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        } else {
                            // Username not found
                            Toast.makeText(EmployeeLoginActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(EmployeeLoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
