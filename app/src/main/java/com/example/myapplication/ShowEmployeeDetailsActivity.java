package com.example.myapplication;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowEmployeeDetailsActivity extends AppCompatActivity {

    // UI elements
    private TextView employeeDetailsTextView;
    private ProgressBar progressBar;

    // Firebase database references
    private FirebaseDatabase database;
    private DatabaseReference employeesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_employee_details);

        // Initialize UI components
        employeeDetailsTextView = findViewById(R.id.employeeDetailsTextView);
        progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        employeesRef = database.getReference("employees");

        // Show the progress bar while loading data
        progressBar.setVisibility(ProgressBar.VISIBLE);

        // Fetch employee details from Firebase
        employeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder employeeDetails = new StringBuilder("Employee List:\n");

                // Loop through the employees and fetch their details
                for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
                    String name = employeeSnapshot.child("name").getValue(String.class);
                    String email = employeeSnapshot.child("email").getValue(String.class);
                    String position = employeeSnapshot.child("position").getValue(String.class);
                    Long salary = employeeSnapshot.child("salary").getValue(Long.class);                    String department = employeeSnapshot.child("department").getValue(String.class);

                    // Append each employee's details to the StringBuilder
                    employeeDetails.append("Name: ").append(name).append("\n")
                            .append("Email: ").append(email).append("\n")
                            .append("Position: ").append(position).append("\n")
                            .append("Salary: ").append(salary != null ? salary : "N/A").append("\n")                            .append("Department: ").append(department).append("\n\n");
                }

                // Hide the progress bar and show employee details
                progressBar.setVisibility(ProgressBar.GONE);
                employeeDetailsTextView.setText(employeeDetails.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hide the progress bar if the operation fails
                progressBar.setVisibility(ProgressBar.GONE);
                // Show a toast if fetching data fails
                Toast.makeText(ShowEmployeeDetailsActivity.this, "Failed to load employee details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
