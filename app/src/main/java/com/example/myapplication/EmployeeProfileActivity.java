package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmployeeProfileActivity extends AppCompatActivity {

    TextView nameTextView, emailTextView, positionTextView, salaryTextView, departmentTextView;
    FirebaseDatabase database;
    DatabaseReference employeesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        employeesRef = database.getReference("employees");

        // Find views
        nameTextView = findViewById(R.id.name);
        emailTextView = findViewById(R.id.email);
        positionTextView = findViewById(R.id.position);
        salaryTextView = findViewById(R.id.salary);
        departmentTextView = findViewById(R.id.department);

        // Fetch logged-in user's ID (replace with actual logic to get logged-in user's ID)
        String employeeId = getIntent().getStringExtra("employeeId");

        if (employeeId != null) {
            fetchEmployeeDetails(employeeId);
        } else {
            Toast.makeText(this, "Employee not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchEmployeeDetails(String employeeId) {
        // Retrieve employee details from Firebase
        employeesRef.child(employeeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String position = dataSnapshot.child("position").getValue(String.class);
                    int salary = dataSnapshot.child("salary").getValue(Integer.class);
                    String department = dataSnapshot.child("department").getValue(String.class);

                    // Display the details
                    nameTextView.setText("Name: " + name);
                    emailTextView.setText("Email: " + email);
                    positionTextView.setText("Position: " + position);
                    salaryTextView.setText("Salary: " + salary);
                    departmentTextView.setText("Department: " + department);
                } else {
                    Toast.makeText(EmployeeProfileActivity.this, "Employee not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(EmployeeProfileActivity.this, "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Employee class
    public static class Employee {
        public String name;
        public String email;
        public String position;
        public int salary;
        public String department;

        public Employee() {
            // Default constructor required for calls to DataSnapshot.getValue(Employee.class)
        }

        public Employee(String name, String email, String position, int salary, String department) {
            this.name = name;
            this.email = email;
            this.position = position;
            this.salary = salary;
            this.department = department;
        }
    }
}
