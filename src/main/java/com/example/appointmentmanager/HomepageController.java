package com.example.appointmentmanager;

import helper.JDBC;
import helper.Utility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomepageController {

    @FXML
    private TabPane tabPaneHomepage;
    @FXML
    private TableView tableCustomers;
    @FXML
    private TableView tableAppointments;

    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    @FXML
    private void initialize() throws SQLException {
        getCustomers();
        getAppointments();

        System.out

        setCustomerColumns();
        setAppointmentColumns();

    }

    private void getCustomers() throws SQLException {
        String sql = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM customers";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet customerSet = ps.executeQuery();

        while (customerSet.next()) {
            Customer curCustomer = new Customer(
                    customerSet.getInt("Customer_ID"),
                    customerSet.getString("Customer_Name"),
                    customerSet.getString("Address"),
                    customerSet.getString("Postal_Code"),
                    customerSet.getString("Phone"),
                    customerSet.getInt("Division_ID")
            );

            allCustomers.add(curCustomer);
        }
    }

    private void setCustomerColumns() {
        Utility.setColumnValue(tableCustomers, 0, "id");
        Utility.setColumnValue(tableCustomers, 1, "name");
        Utility.setColumnValue(tableCustomers, 2, "phone");
        Utility.setColumnValue(tableCustomers, 3, "divisionID");
        Utility.setColumnValue(tableCustomers, 4, "address");
        Utility.setColumnValue(tableCustomers, 5, "postalCode");

        tableCustomers.setItems(allCustomers);
        tableCustomers.getSortOrder().add(tableCustomers.getColumns().get(0));
    }

    private void getAppointments() throws SQLException {
        String sql = "SELECT a.Appointment_ID, a.Title, a.Description, a.Location, a.Type, a.Start, a.End, a.Customer_ID, a.User_ID, c.Contact_Name " +
                "FROM appointments AS a " +
                "JOIN contacts AS c ON a.Contact_ID=c.Contact_ID";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet appointmentSet = ps.executeQuery();

        while (appointmentSet.next()) {
            Appointment curAppointment = new Appointment(
                    appointmentSet.getInt("Appointment_ID"),
                    appointmentSet.getString("Title"),
                    appointmentSet.getString("Description"),
                    appointmentSet.getString("Location"),
                    appointmentSet.getString("Contact_Name"),
                    appointmentSet.getString("Type"),
                    appointmentSet.getInt("Customer_ID"),
                    appointmentSet.getInt("User_ID"),
                    appointmentSet.getDate("Start").toLocalDate(),
                    appointmentSet.getDate("End").toLocalDate()
            );

            allAppointments.add(curAppointment);
        }
    }

    private void setAppointmentColumns() {
        Utility.setColumnValue(tableAppointments, 0, "id");
        Utility.setColumnValue(tableAppointments, 1, "title");
        Utility.setColumnValue(tableAppointments, 2, "description");
        Utility.setColumnValue(tableAppointments, 3, "location");
        Utility.setColumnValue(tableAppointments, 4, "contact");
        Utility.setColumnValue(tableAppointments, 5, "type");
        Utility.setColumnValue(tableAppointments, 6, "start");
        Utility.setColumnValue(tableAppointments, 7, "end");
        Utility.setColumnValue(tableAppointments, 8, "customerID");
        Utility.setColumnValue(tableAppointments, 9, "userID");

        tableAppointments.setItems(allAppointments);
        tableAppointments.getSortOrder().add(tableAppointments.getColumns().get(0));
    }
}
