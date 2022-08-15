package com.example.appointmentmanager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Appointment class
 */
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
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("k:mm");

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

    /**
     * @return the id
     */
    public int getId(){ return id; }

    /**
     * @return the title
     */
    public String getTitle(){ return title; }

    /**
     * @return the description
     */
    public String getDescription(){ return description; }

    /**
     * @return the location
     */
    public String getLocation(){ return location; }

    /**
     * @return the contact
     */
    public String getContact(){ return contact; }

    /**
     * @return the type
     */
    public String getType(){ return type; }

    /**
     * @return the customerID
     */
    public int getCustomerID(){ return customerID; }

    /**
     * @return the userID
     */
    public int getUserID(){ return userID; }

    /**
     * @return the startDate
     */
    public String getStartDate(){ return startDate; }

    /**
     * @return the startTime
     */
    public String getStartTime(){ return startTime; }

    /**
     * @return the endDate
     */
    public String getEndDate(){ return endDate; }

    /**
     * @return the endTime
     */
    public String getEndTime(){ return endTime; }

    /**
     * @return the startDateTime
     */
    public LocalDateTime getStartDateTime() { return startDateTime; }

    /**
     * @return the endDateTime
     */
    public LocalDateTime getEndDateTime() { return endDateTime; }

    /**
     * @return the formatted startDateTime
     */
    public String getSimpleStartDateTime() {
        return startDateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm"));
    }

    /**
     * @return the formatted endDateTime
     */
    public String getSimpleEndDateTime() {
        return endDateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm"));
    }

}
