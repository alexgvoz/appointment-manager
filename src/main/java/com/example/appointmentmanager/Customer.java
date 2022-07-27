package com.example.appointmentmanager;

public class Customer {
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private int divisionId;

    public Customer(int id, String name, String address,String postalCode, String phone, int divisionId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionId = divisionId;
    }

    public int getId() { return id; }

    public String getName(){
        return name;
    }

    public String getAddress(){
        return address;
    }

    public String getPostalCode(){
        return postalCode;
    }

    public String getPhone(){
        return phone;
    }

    public int getDivisionID(){
        return divisionId;
    }
}
