package com.example.sosgeolocator;


import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private MiGPS miGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        miGPS = new MiGPS(this);
        miGPS.startLocationUpdates();

        TextView tvGripLocator = findViewById(R.id.tvGridLocator);
        tvGripLocator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", tvGripLocator.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Grid Locator copiado al portapapeles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Detener las actualizaciones de ubicación cuando la actividad se detenga
        miGPS.stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        miGPS.startLocationUpdates();
    }
}

