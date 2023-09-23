package com.example.sosgeolocator;


import android.location.Location;
import android.os.CountDownTimer;
import android.util.Log;

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
        //cargado location conrola que la clase se carge con el "Location" para que no de error
        cargadoLocation = true;
    }

    public boolean isFixGPS() {
        return fixGPS;
    }

    private CountDownTimer mCountDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (cargadoLocation) {
                long tiempoLocation = mLocation.getTime();
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                long utcTime = calendar.getTimeInMillis();
                long tiempoUltimoGPS = ultimoTimeGPS - tiempoLocation;
                if (ultimoTimeGPS != 0) {
                    if ((Math.abs(tiempoUltimoGPS)) < 4000) {

                        // mCountDownTimer.cancel();
                        fixGPS = true;
                    } else {
                        fixGPS = false;
                    }
                }
                Log.d("PEPE", "actual: " + tiempoLocation + " - " + ultimoTimeGPS + " - " + Math.abs(tiempoUltimoGPS) + " " + fixGPS);

                ultimoTimeGPS = utcTime;
            }
        }

        @Override
        public void onFinish() {

        }

    };
}
