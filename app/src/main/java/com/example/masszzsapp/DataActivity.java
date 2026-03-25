package com.example.masszzsapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DataActivity extends AppCompatActivity {

    private TextView tvUserNameDetail, tvUserEmailDetail;
    private EditText etCurrentPassword, etNewPassword;
    private Button btnUpdatePassword, btnDeleteAccount;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        tvUserNameDetail = findViewById(R.id.tvUserNameDetail);
        tvUserEmailDetail = findViewById(R.id.tvUserEmailDetail);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        if (user != null) {
            tvUserEmailDetail.setText(user.getEmail());
            loadUserData(user.getUid());
        }

        btnUpdatePassword.setOnClickListener(v -> {
            String currentPass = etCurrentPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();

            if (currentPass.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Töltsd ki mindkét jelszó mezőt!", Toast.LENGTH_SHORT).show();
                return;
            }

            user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), currentPass))
                    .addOnCompleteListener(reAuthTask -> {
                        if (reAuthTask.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    Toast.makeText(this, "Jelszó sikeresen módosítva!", Toast.LENGTH_SHORT).show();
                                    etCurrentPassword.setText("");
                                    etNewPassword.setText("");
                                }
                            });
                        } else {
                            Toast.makeText(this, "Helytelen jelenlegi jelszó!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        btnDeleteAccount.setOnClickListener(v -> {
            String currentPass = etCurrentPassword.getText().toString().trim();
            if (currentPass.isEmpty()) {
                Toast.makeText(this, "A törléshez add meg a jelszavad!", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Fiók törlése")
                    .setMessage("Biztosan törölni szeretnéd a fiókodat?")
                    .setPositiveButton("Igen, törlöm", (dialog, which) -> {
                        user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), currentPass))
                                .addOnCompleteListener(reAuthTask -> {
                                    if (reAuthTask.isSuccessful()) {
                                        db.collection("users").document(user.getUid()).delete();
                                        user.delete().addOnCompleteListener(deleteTask -> {
                                            if (deleteTask.isSuccessful()) {
                                                finish();
                                            }
                                        });
                                    }
                                });
                    })
                    .setNegativeButton("Mégse", null)
                    .show();
        });
    }

    private void loadUserData(String userId) {
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                tvUserNameDetail.setText(documentSnapshot.getString("name"));
            }
        });
    }
}