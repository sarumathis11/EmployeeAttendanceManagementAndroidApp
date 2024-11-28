package com.example.myapplication;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;

public class MarkAttendanceActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String CHANNEL_ID = "AttendanceChannel";

    EditText nameEditText, passwordEditText;
    TextView locationTextView;
    Button saveButton, locationButton;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private double fetchedLatitude = 0.0, fetchedLongitude = 0.0;
    private boolean isLocationValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        nameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        locationTextView = findViewById(R.id.locationTextView);
        locationButton = findViewById(R.id.locationButton);
        saveButton = findViewById(R.id.saveButton);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Logic to fetch and display location
        locationButton.setOnClickListener(v -> fetchLocation());

        // Logic to save attendance
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(MarkAttendanceActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!isLocationValid) {
                Toast.makeText(MarkAttendanceActivity.this, "Location is invalid. Attendance not marked.", Toast.LENGTH_SHORT).show();
            } else {
                showNotification(name);
                Toast.makeText(MarkAttendanceActivity.this, "Attendance marked for " + name, Toast.LENGTH_SHORT).show();
            }
        });

        createNotificationChannel();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Fetch the current location for accuracy
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
                        })
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            fetchedLatitude = location.getLatitude();
                            fetchedLongitude = location.getLongitude();

                            // Validate the fetched location
                            isLocationValid = validateLocation(fetchedLatitude, fetchedLongitude);

                            locationTextView.setText("Latitude: " + fetchedLatitude + "\nLongitude: " + fetchedLongitude +
                                    "\nLocation Valid: " + (isLocationValid ? "Yes" : "No"));
                        } else {
                            locationTextView.setText("Unable to fetch location");
                        }
                    }
                });
    }

    private boolean validateLocation(double latitude, double longitude) {
        // Define valid latitude and longitude range
        double minLatitude = 13.0100;
        double maxLatitude = 13.0200;
        double minLongitude = 80.2300;
        double maxLongitude = 80.2400;

        // Check if latitude and longitude fall within the valid range
        return latitude >= minLatitude && latitude <= maxLatitude &&
                longitude >= minLongitude && longitude <= maxLongitude;
    }

    private void showNotification(String name) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Attendance Marked")
                .setContentText("Attendance successfully marked for " + name)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
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
