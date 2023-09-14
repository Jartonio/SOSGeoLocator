package com.example.sosgeolocator;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView miDisplay = findViewById(R.id.display);


        miDisplay.setText(R.string.buscando_GPS);

        GPSManager mGPSManager = new GPSManager(this);
        mGPSManager.getCurrentLocation();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "He vuelto", Toast.LENGTH_SHORT).show();
    }
}
