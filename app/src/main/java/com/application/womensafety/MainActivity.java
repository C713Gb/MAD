package com.application.womensafety;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 99;
    Button sendBtn;
    EditText phoneTxt;
    String phoneNo = "";
    String message = "";
    private GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneTxt = findViewById(R.id.phoneTxt);
        sendBtn = findViewById(R.id.sendBtn);

        sendBtn.setOnClickListener(v -> {
            phoneNo = phoneTxt.getText().toString().trim();

            if (phoneNo != null) {
                if (phoneNo.length() == 10) {
                    phoneNo = "+91" + phoneNo;
                    sendSmsMessage();
                } else {
                    Toast.makeText(MainActivity.this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendSmsMessage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            sendSms();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSms();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private void sendSms() {
        message = getLocation();
        if (message != null) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Press again!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getLocation() {
        gpsTracker = new GpsTracker(MainActivity.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            Log.d("TAG", "getLocation: "+latitude+", "+longitude);
            String locationURL = "https://maps.google.com?q=" +
                    latitude+","+longitude;
            return "Save me, I'm in danger.\n\nMy location is:\n"+locationURL;
        }else{
            gpsTracker.showSettingsAlert();
            return null;
        }
    }


}