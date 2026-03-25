package com.example.masszzsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.rvAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(this, appointmentList);
        recyclerView.setAdapter(adapter);

        loadUserAppointments();
    }

    private void loadUserAppointments() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            finish();
            return;
        }

        db.collection("appointments")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appointmentList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Appointment appointment = document.toObject(Appointment.class);
                            appointment.setId(document.getId());
                            appointmentList.add(appointment);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Hiba a foglalások betöltésekor.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showReviewDialog(Appointment app) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_review, null);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText etReview = view.findViewById(R.id.etReviewText);

        builder.setView(view)
                .setTitle("Értékelés: " + app.getMassageName())
                .setPositiveButton("Mentés", (dialog, which) -> {
                    // Kifejezetten a mi saját Review osztályunkat használjuk
                    com.example.masszzsapp.Review review = new com.example.masszzsapp.Review(
                            mAuth.getUid(),
                            app.getTherapistId(),
                            (int)ratingBar.getRating(),
                            etReview.getText().toString()
                    );
                    db.collection("reviews").add(review)
                            .addOnSuccessListener(doc -> Toast.makeText(this, "Köszönjük!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Hiba!", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Mégse", null)
                .show();
    }
}