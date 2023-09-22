package com.example.sosgeolocator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.TimeZone;


public class ComprobarFIX {
    private long ultimoTimeGPS = 0;
    private Location mLocation;
    private boolean fixGPS;
    private boolean cargadoLocation = false;


    public ComprobarFIX() {
        mCountDownTimer.start();

    }

    public void setmLocation(Location mLocation) {
        this.mLocation = mLocation;
        cargadoLocation = true;


    }

    public boolean isFixGPS() {
        return fixGPS;
    }

    CountDownTimer mCountDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (cargadoLocation) {
                long a = mLocation.getTime();
                long pepe=0;
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                long utcTime = calendar.getTimeInMillis();

                long timeSinceLastUpdate = ultimoTimeGPS - a;
                if (ultimoTimeGPS != 0) {
                    pepe = Math.abs(timeSinceLastUpdate);
                    if (pepe < 4000) {

                        // mCountDownTimer.cancel();
                        fixGPS = true;
                    } else {
                        fixGPS = false;
                    }
                }
                Log.d("PEPE", "actual: " + a + " - " + ultimoTimeGPS + " - " + pepe+" "+  fixGPS);

                ultimoTimeGPS = utcTime;
            }
        }

        @Override
        public void onFinish() {

        }

    };
}
