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
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

public class MiGPS extends CalcularGrid implements LocationListener {

    private Context mContext;
    private LocationManager mLocationManager;
    private double longitudGPS, latitudGPS, longitudGrid, latitudGrid;
    private String mGridLocator = "";
    private final int precision_minima = 100;
    private int precision;
    public Boolean primerPaso = true;


    public MiGPS(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        MainActivity mainActivity = (MainActivity) this.mContext;

        TextView tvMensajes = mainActivity.findViewById(R.id.tvMensajes);
        tvMensajes.setText(R.string.buscando_GPS);

        //startLocationUpdates(); Lo quito porque se carga en el onResume.
    }

    public void startLocationUpdates() {

        primerPaso = false;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }


    public void stopLocationUpdates() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {

        latitudGPS = location.getLatitude();
        longitudGPS = location.getLongitude();

        MainActivity mainActivity = (MainActivity) mContext;
        TextView tvMensajes = mainActivity.findViewById(R.id.tvMensajes);
        TextView tvVerMapaGrid = mainActivity.findViewById(R.id.tvVerMapaGrid);
        TextView tvVerMapaCoordenadas = mainActivity.findViewById(R.id.tvVerMapaCordenadas);


        if (location.hasAccuracy()) {
           // Toast.makeText(mainActivity, "GPS fijado", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(mainActivity, "GPS no fijado", Toast.LENGTH_SHORT).show();
        }

        precision = (int) location.getAccuracy();

        long timeSinceLastUpdate = System.currentTimeMillis() - location.getTime();

        if (timeSinceLastUpdate > 4000) {
            // Ha pasado mucho tiempo desde la última actualización,
            // lo que podría indicar una pérdida de señal
            // o que el GPS no está proporcionando actualizaciones con la frecuencia esperada
            Toast.makeText(mainActivity, "Exceso de tiempo sin señal", Toast.LENGTH_SHORT).show();
        }



        if (precision < precision_minima) {
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

    public String getmGridLocator() {
        return mGridLocator;
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
        tvMensajes.setText(R.string.precision_mala);
        tvLatitudGPS.setText("00.000000");
        tvLongitudGPS.setText("00.000000");
        tvGridLocator.setText("AA00AA00AA");
        tvLatitudGrid.setText("00.000000");
        tvLongitudGrid.setText("00.000000");
        tvPrecision.setText("000");
    }

    private void rellenarCampos(MainActivity mainActivity){

        TextView tvMensajes = mainActivity.findViewById(R.id.tvMensajes);
        TextView tvGridLocator = mainActivity.findViewById(R.id.tvGridLocator);
        TextView tvLatitudGPS = mainActivity.findViewById((R.id.tvLatitudGPS));
        TextView tvLongitudGPS = mainActivity.findViewById((R.id.tvLongitudGPS));
        TextView tvLatitudGrid = mainActivity.findViewById(R.id.tvLatitudGrid);
        TextView tvLongitudGrid = mainActivity.findViewById(R.id.tvLongitudGrid);
        TextView tvPrecision = mainActivity.findViewById(R.id.tvPrecision);

        tvLatitudGPS.setText(Double.toString(latitudGPS));
        tvLatitudGPS.setText(String.format("%.6f", latitudGPS).replace(',', '.'));
        tvLongitudGPS.setText(String.format("%.6f", longitudGPS).replace(',', '.'));
        tvPrecision.setText(Integer.toString(precision));
        tvMensajes.setText(R.string.datos_gps_correctos);

        mGridLocator = CalcularGrid.grid(latitudGPS, longitudGPS);
        tvGridLocator.setText(mGridLocator);

        latitudGrid = CalcularCoordenadasDesdeGrid.latitud(mGridLocator);
        longitudGrid = CalcularCoordenadasDesdeGrid.longitud(mGridLocator);

        tvLatitudGrid.setText(String.format("%.6f", latitudGrid).replace(',', '.'));
        tvLongitudGrid.setText(String.format("%.6f", longitudGrid).replace(',', '.'));
    }
}