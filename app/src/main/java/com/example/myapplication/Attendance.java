package com.example.myapplication;
//package com.example.myapplication;

public class Attendance {
    public String name;
    public double latitude;
    public double longitude;
    public long timestamp;

    // Default constructor required for Firebase
    public Attendance() { }

    // Constructor with parameters
    public Attendance(String name, double latitude, double longitude, long timestamp) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }
}
