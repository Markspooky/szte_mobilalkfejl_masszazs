package com.example.masszzsapp; // Saját csomagnévre átírni!

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {

    private TextView tvSelectedMassage;
    private CalendarView calendarView;
    private EditText etTime;
    private Button btnConfirmBooking;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        tvSelectedMassage = findViewById(R.id.tvSelectedMassage);
        calendarView = findViewById(R.id.calendarView);
        etTime = findViewById(R.id.etTime);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        // 1. Átvesszük a kiválasztott masszázs nevét az Intentből
        String massageName = getIntent().getStringExtra("MASSAGE_NAME");
        if (massageName != null) {
            tvSelectedMassage.setText("Kiválasztott: " + massageName);
        }

        // 2. Naptár dátumválasztás figyelése
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // A hónap 0-tól indul (Január = 0), ezért kell +1
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            }
        });

        // 3. Foglalás megerősítése
        btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = etTime.getText().toString();

                if (selectedDate.isEmpty() || time.isEmpty()) {
                    Toast.makeText(BookingActivity.this, "Kérlek válassz dátumot és időpontot!", Toast.LENGTH_SHORT).show();
                } else {
                    // Itt később a Firebase-be fogjuk menteni a foglalást (2. mérföldkő)
                    Toast.makeText(BookingActivity.this, "Sikeres foglalás: " + selectedDate + " " + time, Toast.LENGTH_LONG).show();

                    // Visszaugrunk a főoldalra
                    Intent intent = new Intent(BookingActivity.this, MainActivity.class);
                    // Letisztítjuk az Activity stacket, hogy ne lehessen visszalépni a foglalásba
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}