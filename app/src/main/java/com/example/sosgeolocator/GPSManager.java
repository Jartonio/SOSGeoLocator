package com.example.sosgeolocator;


import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class GPSManager {

    private final Context mContext;
    private final LocationManager mLocationManager;
    private final LocationListener mLocationListener;


    public GPSManager(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Acción a realizar cuando la ubicación cambia
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double latitudObtenida = 0, longitudObtenida = 0;

                MainActivity mainActivity = (MainActivity) context;

                TextView tvMensajes = mainActivity.findViewById(R.id.tvMensajes);
                TextView tvGridLocator = mainActivity.findViewById(R.id.tvGridLocator);
                TextView tvLatitudGPS = mainActivity.findViewById((R.id.tvLatitudGPS));
                TextView tvLongitudGPS = mainActivity.findViewById((R.id.tvLongitudGPS));
                TextView tvLatitudGRID = mainActivity.findViewById(R.id.tvLatitudGrid);
                TextView tvLongitudGRID = mainActivity.findViewById(R.id.tvLongitudGrid);
                TextView tvVerMapaGrid = mainActivity.findViewById(R.id.tvVerMapaGrid);
                TextView tvVerMapaCoordenadas = mainActivity.findViewById(R.id.tvVerMapaCordenadas);
                TextView tvPrecision = mainActivity.findViewById(R.id.tvPrecision);

                String miGridLocator = "";

                int precision_minima = 100;
                int precision = (int) location.getAccuracy();

                if (precision < precision_minima) {


                    tvLatitudGPS.setText(Double.toString(latitude));

                    tvLatitudGPS.setText(String.format("%.6f", latitude).replace(',', '.'));
                    tvLongitudGPS.setText(String.format("%.6f", longitude).replace(',', '.'));
                    tvPrecision.setText(Integer.toString(precision));
                    tvMensajes.setText(R.string.datos_gps_correctos);

                    miGridLocator = CalcularGrid.grid(latitude, longitude);
                    tvGridLocator.setText(miGridLocator);

                    latitudObtenida = CalcularCoordenadasDesdeGrid.latitud(miGridLocator);
                    longitudObtenida = CalcularCoordenadasDesdeGrid.longitud(miGridLocator);

                    tvLatitudGRID.setText(String.format("%.6f", latitudObtenida).replace(',', '.'));
                    tvLongitudGRID.setText(String.format("%.6f", longitudObtenida).replace(',', '.'));
                } else {
                    tvMensajes.setText(R.string.precision_mala);
                    tvGridLocator.setText("");
                    tvLatitudGRID.setText("");
                    tvLongitudGRID.setText("");
                }


                double finalLatitudObtenida = latitudObtenida;
                double finalLongitudObtenida = longitudObtenida;
                tvVerMapaCoordenadas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        abrirURL(v, "https://www.openstreetmap.org/?mlat=" + finalLatitudObtenida + "&mlon=" + finalLongitudObtenida);
                        abrirURL(v, "https://www.openstreetmap.org/?mlat=" + finalLatitudObtenida + "&mlon=" + finalLongitudObtenida);
                    }
                });
                String finalMiGrid = miGridLocator;
                tvVerMapaGrid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        abrirURL(v, "https://k7fry.com/grid/?qth=" + finalMiGrid);
                    }
                });
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Acción a realizar cuando cambia el estado del proveedor de ubicación
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                // Acción a realizar cuando el proveedor de ubicación se habilita
                Toast.makeText(context, R.string.buscando_GPS, Toast.LENGTH_LONG).show();
                getCurrentLocation();
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                // Acción a realizar cuando el proveedor de ubicación se deshabilita
                Toast.makeText(context, R.string.GPS_desactivado, Toast.LENGTH_LONG).show();
            }
        };
    }

    public Location getCurrentLocation() {
        //Location location = null;

        // Verificar si se tiene permiso para acceder al GPS
        if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Obtener la última ubicación conocida
            //location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // Escuchar cambios en la ubicación
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, mLocationListener);
        } else {
            MainActivity mainActivity = (MainActivity) this.mContext;
            TextView tvMensajes = mainActivity.findViewById(R.id.tvMensajes);
            tvMensajes.setText(R.string.se_necesitan_permisos);
        }
        //return location;
        return null;
    }

    public void abrirURL(View v, String url) {
        MainActivity mainActivity = (MainActivity) mContext;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        mainActivity.startActivity(intent);
    }


}

