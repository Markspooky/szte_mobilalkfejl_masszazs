package com.example.masszzsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MassageAdapter adapter;
    private List<MassageService> massageList;
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    
    private ImageButton btnProfile;
    private CardView cvProfileDropdown;
    private TextView tvUserEmail;
    private Button btnLogoutOverlay, btnMyData, btnMyBookings, btnCoupons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // UI elemek inicializálása
        recyclerView = findViewById(R.id.recyclerViewMassages);
        btnProfile = findViewById(R.id.btnProfile);
        cvProfileDropdown = findViewById(R.id.cvProfileDropdown);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnLogoutOverlay = findViewById(R.id.btnLogoutOverlay);
        btnMyData = findViewById(R.id.btnMyData);
        btnMyBookings = findViewById(R.id.btnMyBookings);
        btnCoupons = findViewById(R.id.btnCoupons);

        // Fő lista (Masszázsok)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        massageList = new ArrayList<>();
        adapter = new MassageAdapter(this, massageList);
        recyclerView.setAdapter(adapter);

        if (mAuth.getCurrentUser() != null) {
            tvUserEmail.setText(mAuth.getCurrentUser().getEmail());
        }

        // Profil gomb kattintás (Dropdown megjelenítése/elrejtése)
        btnProfile.setOnClickListener(v -> {
            if (cvProfileDropdown.getVisibility() == View.GONE) {
                cvProfileDropdown.setVisibility(View.VISIBLE);
            } else {
                cvProfileDropdown.setVisibility(View.GONE);
            }
        });

        // Dropdown gombok kezelése
        btnMyData.setOnClickListener(v -> {
            cvProfileDropdown.setVisibility(View.GONE);
            startActivity(new Intent(MainActivity.this, DataActivity.class));
        });

        btnMyBookings.setOnClickListener(v -> {
            cvProfileDropdown.setVisibility(View.GONE);
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        btnCoupons.setOnClickListener(v -> {
            cvProfileDropdown.setVisibility(View.GONE);
            startActivity(new Intent(MainActivity.this, CouponActivity.class));
        });

        btnLogoutOverlay.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Ha a profil menü nyitva van, zárjuk be
                if (cvProfileDropdown.getVisibility() == View.VISIBLE) {
                    cvProfileDropdown.setVisibility(View.GONE);
                } else {
                    // Ha nincs nyitva, engedjük a normál visszalépést
                    setEnabled(false); // Átmenetileg kikapcsoljuk ezt a figyelőt
                    getOnBackPressedDispatcher().onBackPressed(); // Végrehajtjuk a visszalépést
                    setEnabled(true); // Visszakapcsoljuk a következő alkalomra
                }
            }
        });

        loadServices();
        checkAndSeedTherapists();
    }

    private void loadServices() {
        db.collection("services")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        massageList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MassageService service = document.toObject(MassageService.class);
                            service.setId(document.getId());
                            massageList.add(service);
                        }
                        adapter.notifyDataSetChanged();
                        
                        if (massageList.isEmpty()) {
                            seedData();
                        }
                    }
                });
    }

    private void checkAndSeedTherapists() {
        db.collection("therapists").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().isEmpty()) {
                seedTherapists();
            }
        });
    }

    private void seedTherapists() {
        String[][] therapists = {
            {"Kovács Anna", "10 éves tapasztalat svédmasszázsban.", "Svédmasszázs"},
            {"Nagy Márk", "Specialitása a thai masszázs és nyújtás.", "Thai masszázs"},
            {"Szabó Éva", "Gyógymasszőr, rehabilitációs fókusszal.", "Gyógymasszázs"}
        };

        for (String[] t : therapists) {
            db.collection("therapists").add(new Therapist(null, t[0], t[1], t[2]));
        }
    }

    private void seedData() {
        Object[][] services = {
            {"Svédmasszázs", "12 000 Ft / 60 perc", 60},
            {"Thai masszázs", "15 000 Ft / 60 perc", 60},
            {"Gyógymasszázs", "14 000 Ft / 50 perc", 50},
            {"Aromaterápiás masszázs", "13 000 Ft / 60 perc", 60},
            {"Talpmasszázs", "8 000 Ft / 30 perc", 30}
        };

        for (Object[] s : services) {
            db.collection("services").add(new MassageService((String) s[0], (String) s[1], (Integer) s[2]));
        }
        loadServices();
    }

}