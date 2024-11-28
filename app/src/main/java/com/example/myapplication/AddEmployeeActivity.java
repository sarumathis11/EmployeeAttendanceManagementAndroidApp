package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEmployeeActivity extends AppCompatActivity {

    // UI elements
    private EditText nameEditText, emailEditText, positionEditText, salaryEditText, departmentEditText, usernameEditText, passwordEditText;
    private Button addButton;

    // Firebase database references
    private FirebaseDatabase database;
    private DatabaseReference employeesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        employeesRef = database.getReference("employees");

        // Find views
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        positionEditText = findViewById(R.id.position);
        salaryEditText = findViewById(R.id.salary);
        departmentEditText = findViewById(R.id.department);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        addButton = findViewById(R.id.add);

        // Set onClickListener for the Add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmployee();
            }
        });
    }

    private void addEmployee() {
        // Get data from the input fields
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String position = positionEditText.getText().toString().trim();
        String salaryString = salaryEditText.getText().toString().trim();
        String department = departmentEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || position.isEmpty() || salaryString.isEmpty() ||
                department.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(AddEmployeeActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate salary format
        int salary;
        try {
            salary = Integer.parseInt(salaryString);
        } catch (NumberFormatException e) {
            Toast.makeText(AddEmployeeActivity.this, "Invalid salary format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new employee object
        Employee newEmployee = new Employee(name, email, position, salary, department, username, password);

        // Save the employee data into Firebase Realtime Database
        employeesRef.push().setValue(newEmployee, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e("AddEmployeeActivity", "Error adding employee: " + databaseError.getMessage());
                    Toast.makeText(AddEmployeeActivity.this, "Failed to add employee", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddEmployeeActivity.this, "Employee added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                }
            }
        });
    }

    // Method to clear input fields after successful submission
    private void clearFields() {
        nameEditText.setText("");
        emailEditText.setText("");
        positionEditText.setText("");
        salaryEditText.setText("");
        departmentEditText.setText("");
        usernameEditText.setText("");
        passwordEditText.setText("");
    }

    // Employee class to hold employee data
    public static class Employee {
        private String name;
        private String email;
        private String position;
        private int salary;
        private String department;
        private String username;
        private String password;

        // Default constructor required for calls to DataSnapshot.getValue(Employee.class)
        public Employee() {
        }

        // Constructor to initialize the employee data
        public Employee(String name, String email, String position, int salary, String department, String username, String password) {
            this.name = name;
            this.email = email;
            this.position = position;
            this.salary = salary;
            this.department = department;
            this.username = username;
            this.password = password;
        }

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public int getSalary() {
            return salary;
        }

        public void setSalary(int salary) {
            this.salary = salary;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
