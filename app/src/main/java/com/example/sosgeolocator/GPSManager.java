package com.example.sosgeolocator;


import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class GPSManager {

    private final Context mContext;
    private final LocationManager mLocationManager;
    private final LocationListener mLocationListener;
    private Boolean primerPaso = true;

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

                MainActivity mainActivity = (MainActivity) context;

                TextView miDisplay = mainActivity.findViewById(R.id.display);
                TextView miGrid = mainActivity.findViewById(R.id.grid);

                int precision = (int) location.getAccuracy();

                if (primerPaso) {
                    primerPaso = false;
                    miDisplay.setText(R.string.buscando_GPS);
                } else {
                    if (precision < R.integer.precision_minima) {
                        miDisplay.setText(mainActivity.getString(R.string.latitud) + latitude + "\n" + mainActivity.getString(R.string.longitud) + longitude + mainActivity.getString(R.string.precision) + precision);
                        Toast.makeText(mContext, R.string.datos_gps_correctos, Toast.LENGTH_LONG).show();
                        miGrid.setText("" + CalcularGrid.grid(latitude, longitude));
                    } else {
                        miDisplay.setText(R.string.precision_mala);
                        miGrid.setText("");
                    }
                }
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
            TextView miDisplay = mainActivity.findViewById(R.id.display);
            miDisplay.setText(R.string.se_necesitan_permisos);
            Toast.makeText(mContext, R.string.no_permisos_GPS, Toast.LENGTH_LONG).show();
        }
        //return location;
        return null;
    }
}

