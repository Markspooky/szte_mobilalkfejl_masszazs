package com.example.masszzsapp;

public class Therapist {
    private String id;
    private String name;
    private String bio;
    private String specialty;

    public Therapist() {} // Firestore-hoz kell

    public Therapist(String id, String name, String bio, String specialty) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.specialty = specialty;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public String getSpecialty() { return specialty; }

    @Override
    public String toString() { return name + " (" + specialty + ")"; }
}
