package com.example.masszzsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<Appointment> appointmentList;
    private Context context;
    private FirebaseFirestore db;

    public AppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.tvName.setText(appointment.getMassageName());
        holder.tvDate.setText("Dátum: " + appointment.getDate());
        holder.tvTime.setText("Időpont: " + appointment.getTime());

        holder.btnDelete.setOnClickListener(v -> {
            showCancelDialog(appointment, position);
        });

        holder.btnRate.setOnClickListener(v -> {
            if (isAppointmentFinished(appointment)) {
                if (context instanceof ProfileActivity) {
                    ((ProfileActivity) context).showReviewDialog(appointment);
                }
            } else {
                Toast.makeText(context, "A masszázs még nem történt meg, értékelni csak utána tudsz!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isAppointmentFinished(Appointment app) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm", Locale.getDefault());
            Date bookingStart = sdf.parse(app.getDate() + " " + app.getTime());
            
            if (bookingStart != null) {
                // Kezdés + időtartam
                long endTimeMillis = bookingStart.getTime() + (app.getDurationMinutes() * 60000L);
                return System.currentTimeMillis() > endTimeMillis;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showCancelDialog(Appointment appointment, int position) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Foglalás lemondása")
                .setMessage("Biztosan le szeretnéd mondani ezt az időpontot?")
                .setPositiveButton("Igen, lemondom", (d, which) -> {
                    db.collection("appointments").document(appointment.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                appointmentList.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Foglalás lemondva!", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Nem, mégse", null)
                .create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.btn_text_color));
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvTime;
        Button btnDelete, btnRate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAppointName);
            tvDate = itemView.findViewById(R.id.tvAppointDate);
            tvTime = itemView.findViewById(R.id.tvAppointTime);
            btnDelete = itemView.findViewById(R.id.btnDeleteAppoint);
            btnRate = itemView.findViewById(R.id.btnRateAppoint);
        }
    }
}