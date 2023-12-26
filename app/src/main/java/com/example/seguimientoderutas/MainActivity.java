package com.example.seguimientoderutas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button BtnExit, BtnMap, BtnLocationList; // Agregado el botón para la lista de ubicaciones
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        // Inicializar botones
        BtnExit = findViewById(R.id.btnCerrar);
        BtnMap = findViewById(R.id.BtnMAPA);
        BtnLocationList = findViewById(R.id.BtnLocationList); // Agregado el botón para la lista de ubicaciones

        // Botón para cerrar sesión
        BtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        // Botón para abrir la actividad del mapa
        BtnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

        // Agregado el botón para la lista de ubicaciones
        BtnLocationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LocationListActivity.class));
            }
        });
    }
}