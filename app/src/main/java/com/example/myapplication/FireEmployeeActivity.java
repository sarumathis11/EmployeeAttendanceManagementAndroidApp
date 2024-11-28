package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FireEmployeeActivity extends AppCompatActivity {

    EditText employeeIdEditText;
    Button fireEmployeeButton;
    TextView resultTextView;

    private FirebaseDatabase database;
    private DatabaseReference employeesRef;
    private DatabaseReference firesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_employee);

        // Initialize UI components
        employeeIdEditText = findViewById(R.id.employeeIdEditText);
        fireEmployeeButton = findViewById(R.id.fireEmployeeButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Initialize Firebase references
        database = FirebaseDatabase.getInstance();
        employeesRef = database.getReference("employees");
        firesRef = database.getReference("fires");

        fireEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String employeeId = employeeIdEditText.getText().toString();

                if (!employeeId.isEmpty()) {
                    fireEmployee(employeeId);
                } else {
                    resultTextView.setText("Please enter an employee ID.");
                }
            }
        });
    }

    private void fireEmployee(final String employeeId) {
        employeesRef.child(employeeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get employee details with correct data types and null safety
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String position = dataSnapshot.child("position").getValue(String.class);
                    Long salary = dataSnapshot.child("salary").getValue(Long.class); // Or Integer.class if it's an integer
                    String department = dataSnapshot.child("department").getValue(String.class);
                    String password = dataSnapshot.child("password").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);

                    // Handle potential null values (optional, but recommended)
                    if (name == null || email == null || position == null || salary == null ||
                            department == null || password == null || username == null) {
                        resultTextView.setText("Employee data is incomplete.");
                        return; // Exit the method if data is incomplete
                    }

                    // Create a new employee object with the data
                    Employee employee = new Employee(name, email, position, salary, department, password, username);

                    // Add the employee to the 'fires' table
                    firesRef.child(employeeId).setValue(employee);

                    // Remove the employee from the 'employees' table
                    employeesRef.child(employeeId).removeValue();

                    // Update UI to show the result
                    resultTextView.setText("Employee with ID " + employeeId + " has been fired.");
                } else {
                    // If the employee does not exist in the 'employees' table
                    resultTextView.setText("Employee with ID " + employeeId + " does not exist.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Toast.makeText(FireEmployeeActivity.this, "Failed to fire employee: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                resultTextView.setText("Failed to fire employee.");
            }
        });
    }

    // Employee class to represent employee data
    public static class Employee {
        public String name;
        public String email;
        public String position;
        public Long salary; // Or Integer if it's an integer
        public String department;
        public String password;
        public String username;

        public Employee() {
            // Default constructor required for calls to DataSnapshot.getValue(Employee.class)
        }

        public Employee(String name, String email, String position, Long salary, String department, String password, String username) {
            this.name = name;
            this.email = email;
            this.position = position;
            this.salary = salary;
            this.department = department;
            this.password = password;
            this.username = username;
        }
    }
}