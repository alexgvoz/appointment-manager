package com.example.appointmentmanager;

import java.time.LocalDate;

public class Appointment {
    private int id;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private int customerID;
    private int userID;
    private LocalDate start;
    private LocalDate end;

    public Appointment(int id, String title, String description, String location, String contact, String type, int customerID, int userID, LocalDate start, LocalDate end) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.customerID = customerID;
        this.userID = userID;
        this.start = start;
        this.end = end;
    }

    public int getID(){ return id; }

    public String getTitle(){ return title; }

    public String getDesc(){ return description; }

    public String getLoc(){ return location; }

    public String getContact(){ return contact; }

    public String getType(){ return type; }

    public int getCustomerID(){ return customerID; }

    public int getUserID(){ return userID; }

    public LocalDate getStartTime(){ return start; }

    public LocalDate getEndTime(){ return end; }

}
