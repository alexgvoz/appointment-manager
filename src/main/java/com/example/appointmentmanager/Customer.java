package com.example.appointmentmanager;

/**
 * Customer class
 */
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

    /**
     * @return the id
     */
    public int getId() { return id; }

    /**
     * @return the name
     */
    public String getName(){
        return name;
    }

    /**
     * @return the address
     */
    public String getAddress(){
        return address;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode(){
        return postalCode;
    }

    /**
     * @return the phone
     */
    public String getPhone(){
        return phone;
    }

    /**
     * @return the divisionId
     */
    public int getDivisionID(){
        return divisionId;
    }
}
