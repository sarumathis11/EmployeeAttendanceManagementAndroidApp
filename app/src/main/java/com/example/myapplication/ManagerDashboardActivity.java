package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ManagerDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        // Find Buttons
        Button showEmployeeButton = findViewById(R.id.showEmployeeButton);
        Button leaveApprovalButton = findViewById(R.id.leaveApprovalButton);
        Button fireEmployeeButton = findViewById(R.id.fireEmployeeButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button addEmployeeButton = findViewById(R.id.addEmployeeButton); // Add this line

        // Set onClickListeners
        showEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Show Employee Details Activity
                Intent intent = new Intent(ManagerDashboardActivity.this, ShowEmployeeDetailsActivity.class);
                startActivity(intent);
            }
        });

        leaveApprovalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Leave Approval Activity
                Intent intent = new Intent(ManagerDashboardActivity.this, LeaveApprovalActivity.class);
                startActivity(intent);
            }
        });

        fireEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Fire Employee Activity
                Intent intent = new Intent(ManagerDashboardActivity.this, FireEmployeeActivity.class);
                startActivity(intent);
            }
        });

        // Add new employee
        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Add Employee Activity
                Intent intent = new Intent(ManagerDashboardActivity.this, AddEmployeeActivity.class); // Add your activity here
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to Login Activity
                Intent intent = new Intent(ManagerDashboardActivity.this, ManagerLoginActivity.class);
                startActivity(intent);
                finish(); // Close Dashboard
            }
        });
    }
}
