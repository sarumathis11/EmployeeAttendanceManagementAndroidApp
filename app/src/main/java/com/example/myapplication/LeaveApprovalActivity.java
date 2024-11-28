package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LeaveApprovalActivity extends AppCompatActivity {

    private TextView leaveRequestTextView;
    private Button approveButton, rejectButton;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private List<LeaveRequest> leaveRequests = new ArrayList<>();
    private int currentRequestIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_approval);

        leaveRequestTextView = findViewById(R.id.leaveRequestTextView);
        approveButton = findViewById(R.id.approveButton);
        rejectButton = findViewById(R.id.rejectButton);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("leaveRequests");

        // Fetch leave requests
        fetchLeaveRequests();

        // Set up button listeners
        approveButton.setOnClickListener(v -> handleLeaveAction(true));
        rejectButton.setOnClickListener(v -> handleLeaveAction(false));
    }

    private void fetchLeaveRequests() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leaveRequests.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LeaveRequest request = snapshot.getValue(LeaveRequest.class);
                    if (request != null && "Pending".equals(request.getStatus())) {
                        request.setId(Integer.parseInt(snapshot.getKey())); // Store numeric ID
                        leaveRequests.add(request);
                    }
                }
                displayCurrentRequest();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LeaveApprovalActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLeaveAction(boolean isApproved) {
        if (currentRequestIndex < leaveRequests.size()) {
            LeaveRequest currentRequest = leaveRequests.get(currentRequestIndex);
            String action = isApproved ? "Approved" : "Rejected";
            currentRequest.setStatus(action);

            // Update status in Firebase using numeric ID
            databaseReference.child(String.valueOf(currentRequest.getId())).setValue(currentRequest);

            Toast.makeText(this, "Leave " + action + " for " + currentRequest.getEmployeeName(), Toast.LENGTH_SHORT).show();

            // Move to the next request
            currentRequestIndex++;
            displayCurrentRequest();
        }
    }

    private void displayCurrentRequest() {
        if (currentRequestIndex < leaveRequests.size()) {
            LeaveRequest currentRequest = leaveRequests.get(currentRequestIndex);
            leaveRequestTextView.setText(
                    "Request ID: " + currentRequest.getId() + "\n" +
                            "Employee: " + currentRequest.getEmployeeName() + "\n" +
                            "Reason: " + currentRequest.getReason() + "\n" +
                            "From: " + currentRequest.getStartDate() + "\n" +
                            "To: " + currentRequest.getEndDate() + "\n" +
                            "Status: " + currentRequest.getStatus()
            );
        } else {
            leaveRequestTextView.setText("No more pending leave requests.");
            approveButton.setEnabled(false);
            rejectButton.setEnabled(false);
        }
    }

    // LeaveRequest class to hold request details
    public static class LeaveRequest {
        private int id; // Numeric ID
        private String employeeName;
        private String reason;
        private String startDate;
        private String endDate;
        private String status;

        // Default constructor for Firebase
        public LeaveRequest() {}

        public LeaveRequest(int id, String employeeName, String reason, String startDate, String endDate, String status) {
            this.id = id;
            this.employeeName = employeeName;
            this.reason = reason;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public String getReason() {
            return reason;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
