package com.example.masszzsapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MassageAdapter extends RecyclerView.Adapter<MassageAdapter.ViewHolder> {

    private List<MassageService> massageList;
    private Context context;

    public MassageAdapter(Context context, List<MassageService> massageList) {
        this.context = context;
        this.massageList = massageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_massage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MassageService massage = massageList.get(position);
        holder.tvName.setText(massage.getName());
        holder.tvPrice.setText(massage.getPrice());

        holder.btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingActivity.class);
            intent.putExtra("MASSAGE_ID", massage.getId());
            intent.putExtra("MASSAGE_NAME", massage.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return massageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPrice;
        Button btnBook;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMassageName);
            tvPrice = itemView.findViewById(R.id.tvMassagePrice);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}