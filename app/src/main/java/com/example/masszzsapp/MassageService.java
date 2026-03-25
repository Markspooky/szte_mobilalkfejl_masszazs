package com.example.masszzsapp;

public class MassageService {
    private String name;
    private String price;

    // Konstruktor
    public MassageService(String name, String price) {
        this.name = name;
        this.price = price;
    }

    // Getterek az adatok lekéréséhez
    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}