package com.example.masszzsapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MassageAdapter adapter;
    private List<MassageService> massageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewMassages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        massageList = new ArrayList<>();
        massageList.add(new MassageService("Svédmasszázs", "12 000 Ft / 60 perc"));
        massageList.add(new MassageService("Thai masszázs", "15 000 Ft / 60 perc"));
        massageList.add(new MassageService("Gyógymasszázs", "14 000 Ft / 50 perc"));
        massageList.add(new MassageService("Aromaterápiás masszázs", "13 000 Ft / 60 perc"));
        massageList.add(new MassageService("Talpmasszázs", "8 000 Ft / 30 perc"));

        adapter = new MassageAdapter(this, massageList);
        recyclerView.setAdapter(adapter);
    }
}