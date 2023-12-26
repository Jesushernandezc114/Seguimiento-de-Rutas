package com.example.seguimientoderutas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    Button Btn_Registro;
    Button btn_login;
    EditText email, password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Btn_Registro = findViewById(R.id.btnRegistro);
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.txtcorreo);
        password = findViewById(R.id.txtPass);
        btn_login = findViewById(R.id.BtnLogin);


        email = findViewById(R.id.txtcorreo);
        password = findViewById(R.id.txtPass);
        btn_login = findViewById(R.id.BtnLogin);
        Btn_Registro = findViewById(R.id.btnRegistro);

        mAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IniciarSesion();
            }
        });

        Btn_Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirecciona a la actividad de registro
                startActivity(new Intent(Login.this, Registrar.class));
            }
        });
    }
    private void IniciarSesion() {
        String correo = email.getText().toString().trim();
        String contraseña = password.getText().toString();

        if (correo.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inicia sesión con Firebase Authentication
        mAuth.signInWithEmailAndPassword(correo, contraseña)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Inicio de sesión exitoso
                        Toast.makeText(Login.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                        // Redirecciona a la actividad principal o realiza cualquier otra acción necesaria
                        // Ejemplo:
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error en el inicio de sesión
                        Toast.makeText(Login.this, "Error en el inicio de sesión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }
    }
}
