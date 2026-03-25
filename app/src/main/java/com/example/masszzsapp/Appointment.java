package com.example.masszzsapp;

public class Appointment {
    private String id;
    private String userId;
    private String massageId;
    private String massageName;
    private String therapistId;
    private String therapistName;
    private String date;
    private String time;
    private String status;
    private int durationMinutes;

    public Appointment() {}

    public Appointment(String userId, String massageId, String massageName, String therapistId, String therapistName, String date, String time, int durationMinutes) {
        this.userId = userId;
        this.massageId = massageId;
        this.massageName = massageName;
        this.therapistId = therapistId;
        this.therapistName = therapistName;
        this.date = date;
        this.time = time;
        this.durationMinutes = durationMinutes;
        this.status = "CONFIRMED";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public String getMassageId() { return massageId; }
    public String getMassageName() { return massageName; }
    public String getTherapistId() { return therapistId; }
    public String getTherapistName() { return therapistName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getDurationMinutes() { return durationMinutes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}