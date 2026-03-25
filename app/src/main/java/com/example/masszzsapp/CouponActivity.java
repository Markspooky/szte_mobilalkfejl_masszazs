package com.example.masszzsapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CouponActivity extends AppCompatActivity {

    private TextView tvCouponPoints;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private long currentPoints = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tvCouponPoints = findViewById(R.id.tvCouponPoints);

        loadUserPoints();

        // Gombok beállítása
        setupCouponButton(R.id.btnBuyCoupon5, 3, "5% Kedvezmény", "DISCOUNT_5");
        setupCouponButton(R.id.btnBuyCoupon10, 5, "10% Kedvezmény", "DISCOUNT_10");
        setupCouponButton(R.id.btnBuyMerch, 10, "Masszázs póló", "PRODUCT_MERCH");
        setupCouponButton(R.id.btnBuyBalm, 10, "Lóbalzsam (Extra)", "PRODUCT_BALM");
        setupCouponButton(R.id.btnBuyCandle, 8, "Illatgyertya szett", "PRODUCT_CANDLE");
    }

    private void loadUserPoints() {
        String uid = mAuth.getUid();
        if (uid == null) return;

        db.collection("users").document(uid).addSnapshotListener((value, error) -> {
            if (value != null && value.exists()) {
                currentPoints = value.getLong("loyaltyPoints") != null ? value.getLong("loyaltyPoints") : 0;
                tvCouponPoints.setText(String.valueOf(currentPoints));
            }
        });
    }

    private void setupCouponButton(int buttonId, int cost, String itemName, String itemCode) {
        findViewById(buttonId).setOnClickListener(v -> {
            if (currentPoints >= cost) {
                redeemPoints(cost, itemName, itemCode);
            } else {
                Toast.makeText(this, "Nincs elég pontod! Még " + (cost - currentPoints) + " pont kell.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redeemPoints(int cost, String itemName, String itemCode) {
        String uid = mAuth.getUid();
        
        // 1. Levonjuk a pontokat
        db.collection("users").document(uid)
                .update("loyaltyPoints", FieldValue.increment(-cost))
                .addOnSuccessListener(aVoid -> {
                    
                    // 2. Ha kedvezményről van szó, elmentjük a kuponjaiba
                    if (itemCode.startsWith("DISCOUNT")) {
                        saveCouponToUser(uid, itemName, itemCode);
                    } else {
                        Toast.makeText(this, "Sikeres vásárlás: " + itemName + ". Átvehető a recepción!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hiba a beváltás során.", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveCouponToUser(String uid, String name, String code) {
        Map<String, Object> coupon = new HashMap<>();
        coupon.put("name", name);
        coupon.put("code", code);
        coupon.put("used", false);

        db.collection("users").document(uid).collection("coupons").add(coupon)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Kupon elmentve! Felhasználhatod a következő foglalásnál.", Toast.LENGTH_LONG).show();
                });
    }
}