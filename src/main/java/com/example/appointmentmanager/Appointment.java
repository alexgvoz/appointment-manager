package com.example.appointmentmanager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private int id;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private int customerID;
    private int userID;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public Appointment(int id, String title, String description, String location, String contact, String type, int customerID, int userID, LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("LLL dd u");
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("h:mm a");

        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.customerID = customerID;
        this.userID = userID;
        this.startDate = formatterDate.format(start.toLocalDate());
        this.startTime = formatterTime.format(start.toLocalTime());
        this.endDate = formatterDate.format(end.toLocalDate());
        this.endTime = formatterTime.format(end.toLocalTime());
        this.startDateTime = start;
        this.endDateTime = end;
    }

    public int getId(){ return id; }

    public String getTitle(){ return title; }

    public String getDescription(){ return description; }

    public String getLocation(){ return location; }

    public String getContact(){ return contact; }

    public String getType(){ return type; }

    public int getCustomerID(){ return customerID; }

    public int getUserID(){ return userID; }

    public String getStartDate(){ return startDate; }

    public String getStartTime(){ return startTime; }

    public String getEndDate(){ return endDate; }

    public String getEndTime(){ return endTime; }

    public LocalDateTime getStartDateTime() { return startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }

}
