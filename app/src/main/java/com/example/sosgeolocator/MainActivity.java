package com.example.sosgeolocator;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
    private boolean primerpaso = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvGripLocator = findViewById(R.id.tvGridLocator);
        TextView tvVerMapaCoordenadas=findViewById(R.id.tvVerMapaCordenadas);
        TextView tvVerMapaGrid=findViewById(R.id.tvVerMapaGrid);



        //Listener en el GridLocator para copirlo al portapapeles.
        tvGripLocator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", tvGripLocator.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Grid Locator copiado al portapapeles", Toast.LENGTH_SHORT).show();
            }
        });
        //Listener para ve las coordenadas Grig en el mapa
        tvVerMapaCoordenadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirURL("https://www.openstreetmap.org/?mlat=" + miGPS.getLatitudGrid() + "&mlon=" + miGPS.getLongitudGrid());
            }
        });
        //Listener para ver el Grid locator por internet
        tvVerMapaGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirURL("https://k7fry.com/grid/?qth=" + miGPS.getGridLocator());
            }
        });
    }

    //Este método comprueba cada segundo si tengo permisos de GPS y lo pasa a la variable 'permisoGPS'
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

        if (permisoGPS) {
            tvMensajes.setText(R.string.buscando_GPS);
            miGPS = new MiGPS(this);
            miGPS.startLocationUpdates();
        }
    }

    /*
    si activo esto no vuelve de ver los mapas de internet, pero lanza siempre el patrocinador

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("pepe", "pausado");

*/
    private void abrirURL(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        this.startActivity(intent);
    }

}


