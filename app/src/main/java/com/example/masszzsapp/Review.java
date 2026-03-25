package com.example.masszzsapp;

public class Review {
    private String id;
    private String userId;
    private String therapistId;
    private int rating;
    private String comment;

    public Review() {}

    public Review(String userId, String therapistId, int rating, String comment) {
        this.userId = userId;
        this.therapistId = therapistId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public String getTherapistId() { return therapistId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
}