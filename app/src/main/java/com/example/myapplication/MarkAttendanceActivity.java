package com.example.myapplication;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MarkAttendanceActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String CHANNEL_ID = "AttendanceChannel";

    EditText nameEditText, passwordEditText;
    TextView locationTextView;
    Button saveButton, locationButton;
    LottieAnimationView lottieSuccess;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private double fetchedLatitude = 0.0, fetchedLongitude = 0.0;
    private boolean isLocationValid = false;

    private DatabaseReference attendanceRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        // Initialize UI elements
        nameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        locationTextView = findViewById(R.id.locationTextView);
        locationButton = findViewById(R.id.locationButton);
        saveButton = findViewById(R.id.saveButton);
        lottieSuccess = findViewById(R.id.lottieSuccess);

        // Firebase database reference
        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance");

        // Initialize location provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Fetch location button
        locationButton.setOnClickListener(v -> fetchLocation());

        // Save attendance button
        saveButton.setOnClickListener(v -> saveAttendance());

        createNotificationChannel();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getCurrentLocation(
                com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY,
                new CancellationToken() {
                    @Override
                    public boolean isCancellationRequested() {
                        return false;
                    }

                    @NonNull
                    @Override
                    public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                        return this;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    fetchedLatitude = location.getLatitude();
                    fetchedLongitude = location.getLongitude();

                    isLocationValid = validateLocation(fetchedLatitude, fetchedLongitude);

                    locationTextView.setText("Latitude: " + fetchedLatitude +
                            "\nLongitude: " + fetchedLongitude +
                            "\nLocation Valid: " + (isLocationValid ? "Yes" : "No"));
                } else {
                    locationTextView.setText("Unable to fetch location");
                }
            }
        });
    }

    private boolean validateLocation(double latitude, double longitude) {
        double minLatitude = 13.0100;
        double maxLatitude = 13.0200;
        double minLongitude = 80.2300;
        double maxLongitude = 80.2400;

        return latitude >= minLatitude && latitude <= maxLatitude &&
                longitude >= minLongitude && longitude <= maxLongitude;
    }

    private void saveAttendance() {
        String name = nameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isLocationValid) {
            Toast.makeText(this, "Location is invalid. Attendance not marked.", Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp = System.currentTimeMillis();
        Attendance attendance = new Attendance(name, fetchedLatitude, fetchedLongitude, timestamp);

        // Save to Firebase
        String key = attendanceRef.push().getKey();
        if (key != null) {
            attendanceRef.child(key).setValue(attendance)
                    .addOnSuccessListener(aVoid -> {
                        // Show animation
                        lottieSuccess.setVisibility(View.VISIBLE);
                        lottieSuccess.playAnimation();

                        // Show notification
                        showNotification(name);

                        Toast.makeText(MarkAttendanceActivity.this, "Attendance marked for " + name, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MarkAttendanceActivity.this, "Failed to save attendance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void showNotification(String name) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Attendance Marked")
                .setContentText("Attendance successfully marked for " + name)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Attendance Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for attendance marking");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
