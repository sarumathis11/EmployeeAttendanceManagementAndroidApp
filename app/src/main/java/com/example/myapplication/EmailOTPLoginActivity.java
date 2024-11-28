package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailOTPLoginActivity extends AppCompatActivity {

    private EditText emailInput, otpInput;
    private Button sendOtpButton, verifyOtpButton;

    private String generatedOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_otplogin);

        emailInput = findViewById(R.id.emailInput);
        otpInput = findViewById(R.id.otpInput);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);


        // Send OTP on button click
        sendOtpButton.setOnClickListener(view -> sendOtp());

        // Verify OTP on button click
        verifyOtpButton.setOnClickListener(view -> verifyOtp());


    }

    // Method to send OTP to the user's email
    private void sendOtp() {
        String email = emailInput.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter email address", Toast.LENGTH_SHORT).show();
            return;
        }

        generatedOtp = String.valueOf((int) (Math.random() * 900000) + 100000); // Generate 6-digit OTP

        sendOtpButton.setEnabled(false); // Disable the button to prevent multiple OTP requests

        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("textstream26@gmail.com", "vmzl gaka kkgw zcqa ");
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("textstream26@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject("Your OTP Code");
                message.setText("Welcome To Textstream.\nYour OTP is: " + generatedOtp);

                Transport.send(message);

                runOnUiThread(() -> {
                    Toast.makeText(this, "OTP sent to email!", Toast.LENGTH_SHORT).show();
                    sendOtpButton.setEnabled(true); // Re-enable the button after sending OTP
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to send OTP: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    sendOtpButton.setEnabled(true); // Re-enable the button in case of error
                });
            }
        }).start();
    }

    // Method to verify the OTP entered by the user
    private void verifyOtp() {
        String enteredOtp = otpInput.getText().toString();
        String email = emailInput.getText().toString();

        if (enteredOtp.equals(generatedOtp)) {
            // OTP is correct, proceed with login and redirect
            new Thread(() -> {
                try {
                    // Send a success email after verification
                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.port", "587");

                    Session session = Session.getInstance(props, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("textstream26@gmail.com", "vmzl gaka kkgw zcqa");
                        }
                    });

                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("textstream26@gmail.com"));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                    message.setSubject("Successfully Logged In");
                    message.setText("You successfully logged in to the TextStream application. Thank you for preferring us.");

                    Transport.send(message);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EmailOTPLoginActivity.this, ManagerDashboardActivity.class);
                        startActivity(intent); // Start the EmployeeDashboardActivity
                        finish(); // Optionally finish the current activity to prevent going back to it
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Failed To Log In: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        } else {
            // OTP is incorrect
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
        }
    }
}
