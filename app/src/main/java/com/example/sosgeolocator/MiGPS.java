package com.example.sosgeolocator;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MiGPS extends CalcularGrid implements LocationListener {

    private Context mContext;
    private LocationManager mLocationManager;
    private double longitudGPS, latitudGPS, longitudGrid, latitudGrid;
    private String miGridLocator = "";
    private final int precision_minima = 100;


    public MiGPS(Context context) {
        this.mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        MainActivity mainActivity = (MainActivity) this.mContext;

        TextView tvMensajes = mainActivity.findViewById(R.id.tvMensajes);
        tvMensajes.setText(R.string.buscando_GPS);

        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "Permiso concedido", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Permiso no concedido", Toast.LENGTH_SHORT).show();
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }

    public void startLocationUpdates() {

        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        TextView tvGridLocator = mainActivity.findViewById(R.id.tvGridLocator);
        TextView tvLatitudGPS = mainActivity.findViewById((R.id.tvLatitudGPS));
        TextView tvLongitudGPS = mainActivity.findViewById((R.id.tvLongitudGPS));
        TextView tvLatitudGRID = mainActivity.findViewById(R.id.tvLatitudGrid);
        TextView tvLongitudGRID = mainActivity.findViewById(R.id.tvLongitudGrid);
        TextView tvVerMapaGrid = mainActivity.findViewById(R.id.tvVerMapaGrid);
        TextView tvVerMapaCoordenadas = mainActivity.findViewById(R.id.tvVerMapaCordenadas);
        TextView tvPrecision = mainActivity.findViewById(R.id.tvPrecision);


        int precision = (int) location.getAccuracy();

        if (precision < precision_minima) {

            tvLatitudGPS.setText(Double.toString(latitudGPS));
            tvLatitudGPS.setText(String.format("%.6f", latitudGPS).replace(',', '.'));
            tvLongitudGPS.setText(String.format("%.6f", longitudGPS).replace(',', '.'));
            tvPrecision.setText(Integer.toString(precision));
            tvMensajes.setText(R.string.datos_gps_correctos);

            miGridLocator = CalcularGrid.grid(latitudGPS, longitudGPS);
            tvGridLocator.setText(miGridLocator);

            latitudGrid = CalcularCoordenadasDesdeGrid.latitud(miGridLocator);
            longitudGrid = CalcularCoordenadasDesdeGrid.longitud(miGridLocator);

            tvLatitudGRID.setText(String.format("%.6f", latitudGrid).replace(',', '.'));
            tvLongitudGRID.setText(String.format("%.6f", longitudGrid).replace(',', '.'));

        } else {
            tvMensajes.setText(R.string.precision_mala);
            tvLatitudGPS.setText("00.000000");
            tvLongitudGPS.setText("00.000000");
            tvGridLocator.setText("AA00AA00AA");
            tvLatitudGRID.setText("00.000000");
            tvLongitudGRID.setText("00.000000");
            tvPrecision.setText("000");
        }

        tvVerMapaCoordenadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirURL("https://www.openstreetmap.org/?mlat=" + latitudGrid + "&mlon=" + longitudGrid);
            }
        });
        tvVerMapaGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirURL("https://k7fry.com/grid/?qth=" + miGridLocator);
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Manejar cambios en el estado del proveedor de ubicación
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Manejar la habilitación del proveedor de ubicación
        Toast.makeText(mContext, R.string.buscando_GPS, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Manejar la deshabilitación del proveedor de ubicación
        // Acción a realizar cuando el proveedor de ubicación se deshabilita
        Toast.makeText(mContext, R.string.GPS_desactivado, Toast.LENGTH_LONG).show();
    }


    private void abrirURL(String url) {
        MainActivity mainActivity = (MainActivity) mContext;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        mainActivity.startActivity(intent);
    }


}