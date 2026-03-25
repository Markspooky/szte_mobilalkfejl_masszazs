package com.example.masszzsapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BookingActivity extends AppCompatActivity {

    private TextView tvSelectedMassage;
    private CalendarView calendarView;
    private EditText etTime;
    private Button btnConfirmBooking;
    private Spinner spinnerTherapists;

    private String selectedDate = "";
    private String massageId = "";
    private String massageName = "";
    private int massageDuration = 60; // Alapértelmezett

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<Therapist> therapistList;
    private ArrayAdapter<Therapist> therapistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tvSelectedMassage = findViewById(R.id.tvSelectedMassage);
        calendarView = findViewById(R.id.calendarView);
        etTime = findViewById(R.id.etTime);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        spinnerTherapists = findViewById(R.id.spinnerTherapists);

        massageId = getIntent().getStringExtra("MASSAGE_ID");
        massageName = getIntent().getStringExtra("MASSAGE_NAME");
        
        loadMassageDetails();
        
        if (massageName != null) {
            tvSelectedMassage.setText("Kiválasztott: " + massageName);
        }

        therapistList = new ArrayList<>();
        therapistAdapter = new ArrayAdapter<Therapist>(this, android.R.layout.simple_spinner_item, therapistList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.WHITE);
                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.BLACK);
                return v;
            }
        };
        therapistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTherapists.setAdapter(therapistAdapter);
        
        loadTherapists();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
        });

        btnConfirmBooking.setOnClickListener(v -> saveBooking());
    }

    private void loadMassageDetails() {
        if (massageId != null) {
            db.collection("services").document(massageId).get().addOnSuccessListener(doc -> {
                if (doc.exists() && doc.contains("durationMinutes")) {
                    massageDuration = doc.getLong("durationMinutes").intValue();
                }
            });
        }
    }

    private void loadTherapists() {
        db.collection("therapists").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                therapistList.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Therapist t = doc.toObject(Therapist.class);
                    t.setId(doc.getId());
                    therapistList.add(t);
                }
                therapistAdapter.notifyDataSetChanged();
            }
        });
    }

    private void saveBooking() {
        String time = etTime.getText().toString().trim();
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        Therapist selectedTherapist = (Therapist) spinnerTherapists.getSelectedItem();

        if (userId == null || selectedTherapist == null) {
            Toast.makeText(this, "Hiba az adatoknál!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDate.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Kérlek válassz dátumot és időpontot!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int hour = Integer.parseInt(time.split(":")[0]);
            if (hour < 8 || hour >= 18) {
                Toast.makeText(this, "Csak 8:00 és 18:00 között foglalható időpont!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Helytelen időformátum! (pl. 14:30)", Toast.LENGTH_SHORT).show();
            return;
        }

        Appointment appointment = new Appointment(userId, massageId, massageName, 
                selectedTherapist.getId(), selectedTherapist.getName(), selectedDate, time, massageDuration);

        db.collection("appointments")
                .whereEqualTo("therapistId", selectedTherapist.getId())
                .whereEqualTo("date", selectedDate)
                .whereEqualTo("time", time)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Toast.makeText(this, "Az időpont nem megfelelő, " + selectedTherapist.getName() + " nem elérhető!", Toast.LENGTH_LONG).show();
                    } else {
                        performFinalBooking(appointment);
                    }
                });
    }

    private void performFinalBooking(Appointment appointment) {
        db.collection("appointments").add(appointment).addOnSuccessListener(doc -> {
            schedulePoints(appointment);
            Toast.makeText(this, "Sikeres foglalás!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void schedulePoints(Appointment app) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm", Locale.getDefault());
            Date bookingDate = sdf.parse(app.getDate() + " " + app.getTime());
            
            if (bookingDate != null) {
                long delayMillis = (bookingDate.getTime() + (massageDuration * 60000L)) - System.currentTimeMillis();
                
                if (delayMillis < 0) delayMillis = 0;

                Data data = new Data.Builder()
                        .putString("userId", mAuth.getUid())
                        .build();

                OneTimeWorkRequest pointsWork = new OneTimeWorkRequest.Builder(PointsWorker.class)
                        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                        .setInputData(data)
                        .build();

                WorkManager.getInstance(this).enqueue(pointsWork);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}