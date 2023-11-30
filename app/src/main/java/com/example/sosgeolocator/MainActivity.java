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
    public boolean primerPaso = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        miGPS = new MiGPS(this);

        //Creo un listener en el GridLocator para copiarlo al portapapeles.
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

        //Creo un listener para abrir un mapa con las coordenadas obtenidas-
        TextView tvVerMapaCoordenadas=findViewById(R.id.tvVerMapaCordenadas);
        tvVerMapaCoordenadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirURL("https://www.openstreetmap.org/?mlat=" + miGPS.getLatitudGrid() + "&mlon=" + miGPS.getLongitudGrid());
            }
        });

        //Creo un listener para abrir en un mapa el GridLocator obtenido.
        TextView tvVerMapaGrid=findViewById(R.id.tvVerMapaGrid);
        tvVerMapaGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirURL("https://k7fry.com/grid/?qth=" + miGPS.getmGridLocator());
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
            if (primerPaso) {
                ActivityCompat.requestPermissions((Activity) this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                countDownTimer.start();
                primerPaso = false;
                tvMensajes.setText(R.string.se_necesitan_permisos);
            }
        }

        Log.d("tick", "onResume: " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));

        if (permisoGPS) {
            tvMensajes.setText(R.string.buscando_GPS);
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
    //Este metodo abre el map conlas coordenadas o con el GridLocator.
    private void abrirURL(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        this.startActivity(intent);
    }
}


