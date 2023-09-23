package com.example.sosgeolocator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.TimeZone;

public class MiGPS extends CalcularGrid implements LocationListener {

    private Context mContext;
    private LocationManager mLocationManager;
    private double longitudGPS, latitudGPS, longitudGrid, latitudGrid;
    private String gridLocator = "";
    private final int precision_minima = 10000;
    private int precision,altitud;

    private long ultimoTimeGPS;


    private boolean fix=false;

    //public Boolean primerpaso = true;
    private ComprobarFIX comprobarFixGPS= new ComprobarFIX();

    private Location mLocation=null;

    public MiGPS(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        MainActivity mainActivity = (MainActivity) this.mContext;

        TextView tvMensajes = mainActivity.findViewById(R.id.tvMensajes);

        tvMensajes.setText(R.string.buscando_GPS);
        mCountDownTimer.start();


    }

    public void startLocationUpdates() {

        //primerpaso = false;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

    }


    public void stopLocationUpdates() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation=location;


        latitudGPS = location.getLatitude();
        longitudGPS = location.getLongitude();

        MainActivity mainActivity = (MainActivity) mContext;
        TextView tvMensajes = mainActivity.findViewById(R.id.tvMensajes);


        Log.d("PEPEPE", "onLocationChanged: ");


        comprobarFixGPS.setmLocation(location);


        if (location.hasAccuracy()) {
            fix=true;
        } else {
            fix=false;
        }

        precision = (int) location.getAccuracy();
        altitud=(int) location.getAltitude();

        long timeSinceLastUpdate = location.getTime()-ultimoTimeGPS;

        if (timeSinceLastUpdate > 4000) {
            // Ha pasado mucho tiempo desde la última actualización,
            // lo que podría indicar una pérdida de señal
            // o que el GPS no está proporcionando actualizaciones con la frecuencia esperada
            Toast.makeText(mainActivity, "Exceso de tiempo sin señal", Toast.LENGTH_SHORT).show();
        }else{
            //Log.d("PEPE", "TIME correcto: "+ultimoTimeGPS+" - "+location.getTime()+"= "+timeSinceLastUpdate);
        }


        //ultimoTimeGPS=location.getTime();




        if (precision <= precision_minima) {
            //Si la precisón es buena, actualizo los campos de la pantalla.
            rellenarCampos(mainActivity);

        } else {
            //Si la precisión es mala, pongo los campos de pantalla a "0"
            tvMensajes.setText(R.string.precision_mala);
            ponerAcerosLosCampos(mainActivity);

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Manejar cambios en el estado del proveedor de ubicación
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Manejar la habilitación del proveedor de ubicación
        Toast.makeText(mContext, R.string.buscando_GPS, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Manejar la deshabilitación del proveedor de ubicación
        // Acción a realizar cuando el proveedor de ubicación se deshabilita
        Toast.makeText(mContext, R.string.GPS_desactivado, Toast.LENGTH_SHORT).show();
    }

    public double getLongitudGPS() {
        return longitudGPS;
    }

    public double getLatitudGPS() {
        return latitudGPS;
    }

    public double getLongitudGrid() {
        return longitudGrid;
    }

    public double getLatitudGrid() {
        return latitudGrid;
    }

    public String getGridLocator() {
        return gridLocator;
    }

    private void abrirURL(String url) {
        MainActivity mainActivity = (MainActivity) mContext;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        mainActivity.startActivity(intent);
    }


    private void ponerAcerosLosCampos(MainActivity mainActivity ){

        TextView tvMensajes = mainActivity.findViewById(R.id.tvMensajes);
        TextView tvGridLocator = mainActivity.findViewById(R.id.tvGridLocator);
        TextView tvLatitudGPS = mainActivity.findViewById((R.id.tvLatitudGPS));
        TextView tvLongitudGPS = mainActivity.findViewById((R.id.tvLongitudGPS));
        TextView tvLatitudGrid = mainActivity.findViewById(R.id.tvLatitudGrid);
        TextView tvLongitudGrid = mainActivity.findViewById(R.id.tvLongitudGrid);
        TextView tvPrecision = mainActivity.findViewById(R.id.tvPrecision);
        TextView tvAltitud = mainActivity.findViewById(R.id.tvAltitud);
        TextView tvFix = mainActivity.findViewById(R.id.tvFix);
        tvMensajes.setText(R.string.precision_mala);
        tvLatitudGPS.setText("00.000000");
        tvLongitudGPS.setText("00.000000");
        tvGridLocator.setText("AA00AA00AA");
        tvLatitudGrid.setText("00.000000");
        tvLongitudGrid.setText("00.000000");
        tvPrecision.setText("000");
        tvAltitud.setText(("--- m"));
        tvFix.setText("No Fix");
    }

    private void rellenarCampos(MainActivity mainActivity){

        TextView tvMensajes = mainActivity.findViewById(R.id.tvMensajes);
        TextView tvGridLocator = mainActivity.findViewById(R.id.tvGridLocator);
        TextView tvLatitudGPS = mainActivity.findViewById((R.id.tvLatitudGPS));
        TextView tvLongitudGPS = mainActivity.findViewById((R.id.tvLongitudGPS));
        TextView tvLatitudGrid = mainActivity.findViewById(R.id.tvLatitudGrid);
        TextView tvLongitudGrid = mainActivity.findViewById(R.id.tvLongitudGrid);
        TextView tvPrecision = mainActivity.findViewById(R.id.tvPrecision);
        TextView tvAltitud = mainActivity.findViewById(R.id.tvAltitud);
        TextView tvFix =mainActivity.findViewById(R.id.tvFix);

        if (fix){
            tvFix.setText("Si FiX");
        }else{
            tvFix.setText("No Fix");

        }

        tvLatitudGPS.setText(Double.toString(latitudGPS));
        tvLatitudGPS.setText(String.format("%.6f", latitudGPS).replace(',', '.'));
        tvLongitudGPS.setText(String.format("%.6f", longitudGPS).replace(',', '.'));
        tvPrecision.setText(Integer.toString(precision));
        tvAltitud.setText(Integer.toString(altitud)+" m");
        tvMensajes.setText(R.string.datos_gps_correctos);

        gridLocator = CalcularGrid.grid(latitudGPS, longitudGPS);
        tvGridLocator.setText(gridLocator);

        latitudGrid = CalcularCoordenadasDesdeGrid.latitud(gridLocator);
        longitudGrid = CalcularCoordenadasDesdeGrid.longitud(gridLocator);

        tvLatitudGrid.setText(String.format("%.6f", latitudGrid).replace(',', '.'));
        tvLongitudGrid.setText(String.format("%.6f", longitudGrid).replace(',', '.'));
    }

    private void iconoGPS (MainActivity mm,boolean activar){
        TextView tv=mm.findViewById(R.id.tvGPS);
        if (activar){
            tv.setText("GPS");
        }else {
            tv.setText("");
        }
    }

    private CountDownTimer mCountDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
        int intentos=0;
        @Override
        public void onTick(long millisUntilFinished) {
            if (mLocation!=null) {
                long tiempoLocation = mLocation.getTime();
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                long utcTime = calendar.getTimeInMillis();
                long tiempoUltimoGPS = ultimoTimeGPS - tiempoLocation;
                if (ultimoTimeGPS != 0) {
                    if ((Math.abs(tiempoUltimoGPS)) < 4000) {

                        // mCountDownTimer.cancel();
                        fix = true;
                        intentos++;
                    } else {
                        fix = false;
                        intentos--;
                    }
                }
                Log.d("PEPEPE", "actual: " + tiempoLocation + " - " + ultimoTimeGPS + " - " + Math.abs(tiempoUltimoGPS) + " " + fix);
                if (intentos>=4){
                    intentos=4;
                    Log.d("PEPEPE", "encender icono gps");
                }
                if (intentos<=0 && fix) {
                    intentos=0;
                    Log.d("PEPEPE", "apagar icono gps");
                }
                iconoGPS((MainActivity) mContext,fix);
                ultimoTimeGPS = utcTime;
            }
        }

        @Override
        public void onFinish() {

        }

    };

}