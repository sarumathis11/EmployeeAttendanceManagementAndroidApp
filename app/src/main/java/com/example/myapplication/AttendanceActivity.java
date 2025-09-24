package com.example.myapplication;

//package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myapplication.AttendanceProgressView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {

    private LottieAnimationView lottieSuccess;
    private AttendanceProgressView progressView;
    private Button btnMark;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        lottieSuccess = findViewById(R.id.lottieSuccess);
        progressView = findViewById(R.id.attendanceProgress);
        btnMark = findViewById(R.id.btnMarkAttendance);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("attendances");

        btnMark.setOnClickListener(v -> markAttendance());

        // load metrics on start
        loadAttendanceMetrics();
    }

    private void markAttendance() {
        String uid = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : "anon";
        long ts = System.currentTimeMillis();

        AttendanceRecord rec = new AttendanceRecord(uid, ts, "present");
        String key = dbRef.push().getKey();
        if (key == null) {
            Toast.makeText(this, "Cannot get DB key", Toast.LENGTH_SHORT).show();
            return;
        }

        dbRef.child(key).setValue(rec)
                .addOnSuccessListener(aVoid -> {
                    playSuccessAnim();
                    loadAttendanceMetrics(); // refresh progress/trend
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to mark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void playSuccessAnim() {
        lottieSuccess.setVisibility(View.VISIBLE);
        lottieSuccess.playAnimation();
        lottieSuccess.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                lottieSuccess.setVisibility(View.GONE);
                lottieSuccess.removeAllAnimatorListeners();
            }
        });
    }

    private void loadAttendanceMetrics() {
        // fetch all attendance records and compute percent + 7-day trend
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                int total = 0, presentCount = 0;
                // compute last 7 days trend (present/total each day)
                long now = System.currentTimeMillis();
                long dayMs = 24L * 60L * 60L * 1000L;
                int DAYS = 7;
                int[] present = new int[DAYS];
                int[] totals = new int[DAYS];

                for (DataSnapshot child : snapshot.getChildren()) {
                    Long ts = child.child("timestamp").getValue(Long.class);
                    String status = child.child("status").getValue(String.class);
                    if (ts == null) continue;
                    int idx = (int) ((now - ts) / dayMs); // 0 = today, 1 = yesterday...
                    if (idx >= 0 && idx < DAYS) {
                        totals[idx]++;
                        if ("present".equalsIgnoreCase(status)) present[idx]++;
                    }
                    total++;
                    if ("present".equalsIgnoreCase(status)) presentCount++;
                }

                float overallPercent = total == 0 ? 0f : (presentCount / (float) total);
                // prepare trend oldest->newest as floats 0..1
                List<Float> trend = new ArrayList<>();
                for (int i = DAYS - 1; i >= 0; i--) { // oldest first
                    int t = totals[i];
                    float pct = t == 0 ? 0f : (present[i] / (float) t);
                    trend.add(pct);
                }

                // update custom view
                progressView.setTrendData(trend);
                progressView.animateProgress(overallPercent);
            }

            @Override public void onCancelled(DatabaseError error) {
                Log.e("DB", "loadAttendanceMetrics: " + error.getMessage());
            }
        });
    }

    // data holder used for Firebase
    public static class AttendanceRecord {
        public String uid;
        public long timestamp;
        public String status;

        public AttendanceRecord() { } // required empty constructor
        public AttendanceRecord(String uid, long timestamp, String status) {
            this.uid = uid; this.timestamp = timestamp; this.status = status;
        }
    }
}

