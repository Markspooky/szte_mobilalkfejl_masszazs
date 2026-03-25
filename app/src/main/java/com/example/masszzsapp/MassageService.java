package com.example.masszzsapp;

public class MassageService {
    private String id;
    private String name;
    private String price;
    private int durationMinutes;

    public MassageService() {}

    public MassageService(String name, String price, int durationMinutes) {
        this.name = name;
        this.price = price;
        this.durationMinutes = durationMinutes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getDurationMinutes() { return durationMinutes; }
}