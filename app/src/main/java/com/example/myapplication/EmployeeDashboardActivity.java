package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class EmployeeDashboardActivity extends AppCompatActivity {

    String employeeId; // Field to store the logged-in employee's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        // Retrieve employee ID from Intent
        employeeId = getIntent().getStringExtra("employeeId");

        if (employeeId == null || employeeId.isEmpty()) {
            // Handle error if employee ID is missing
            finish(); // Close the activity if no valid ID is provided
            return;
        }

        // Find Buttons
        Button profileButton = findViewById(R.id.profileButton);
        Button markAttendanceButton = findViewById(R.id.markAttendanceButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set onClickListeners for Profile Button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Profile Activity and pass employee ID
                Intent intent = new Intent(EmployeeDashboardActivity.this, EmployeeProfileActivity.class);
                intent.putExtra("employeeId", employeeId); // Pass the employee ID
                startActivity(intent);
            }
        });

        // Set onClickListeners for Mark Attendance Button
        markAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Mark Attendance Activity
                Intent intent = new Intent(EmployeeDashboardActivity.this, MarkAttendanceActivity.class);
                startActivity(intent);
            }
        });

        // Set onClickListeners for Logout Button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to Login Activity
                Intent intent = new Intent(EmployeeDashboardActivity.this, EmployeeLoginActivity.class);
                startActivity(intent);
                finish(); // Close Dashboard Activity
            }
        });
    }
}
