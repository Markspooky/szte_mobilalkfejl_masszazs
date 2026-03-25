package com.example.masszzsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // 1. Változók deklarálása a UI elemekhez
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ez köti hozzá a Java fájlhoz az imént megírt XML dizájnt
        setContentView(R.layout.activity_login);

        // 2. Elemek összekötése az XML ID-k alapján (findViewById)
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // 3. Kattintásfigyelő (ClickListener) beállítása a Bejelentkezés gombra
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                // Egyelőre csak egy egyszerű ellenőrzés (később ide jön a Firebase auth)
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Kérlek tölts ki minden mezőt!", Toast.LENGTH_SHORT).show();
                } else {
                    // Ha minden oké, Intent-tel átnavigálunk a főképernyőre
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Bezárjuk a Login Activity-t, hogy a "Vissza" gombbal ne jöjjön ide újra
                }
            }
        });

        // 4. Kattintásfigyelő a Regisztráció linkre
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Átnavigálunk a Regisztrációs képernyőre
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}