package com.example.sosgeolocator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PatrocinadorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_patrocinador);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Iniciar la actividad principal (MainActivity) después de 5 segundos
                Intent intent = new Intent(PatrocinadorActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Cierra la actividad actual para que no vuelva al presionar "Atrás"
                Log.d("pepe", "run: ");
            }
        }, 2000); // 2000 milisegundos = 5 segundos
    }
}
