package com.example.sosgeolocator;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    private MiGPS miGPS;
    private boolean permisoGPS = false;
    public boolean primerpaso = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        miGPS = new MiGPS(this);

        //Creo un listener en el GridLocator para copirlo al portapapeles.
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

    //Este método lee cada segundo si tengo permisos de GPS y lo pasa a la variable 'permisoGPS'
    CountDownTimer countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permisoGPS = true;
                countDownTimer.cancel();
            }
            Log.d("tick", "onTick: " + permisoGPS);
        }

        @Override
        public void onFinish() {
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        miGPS.stopLocationUpdates();
    }


    @Override
    protected void onResume() {
        super.onResume();
        TextView tvMensajes = findViewById(R.id.tvMensajes);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permisoGPS = true;
        } else {
            if (primerpaso) {
                ActivityCompat.requestPermissions((Activity) this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                countDownTimer.start();
                primerpaso = false;
                tvMensajes.setText(R.string.se_necesitan_permisos);
            }
        }

        Log.d("tick", "onResume: " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));

        if (permisoGPS) {
            tvMensajes.setText(R.string.buscando_GPS);
            miGPS.startLocationUpdates();
        }
    }


}

