package com.example.appointmentmanager;

import helper.JDBC;
import helper.Utility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class HomepageController {

    @FXML
    private TabPane tabPaneHomepage;
    @FXML
    private TableView tableCustomers;
    @FXML
    private TableView tableAppointments;
    @FXML
    private ChoiceBox choiceFilter;
    @FXML
    private ToggleGroup groupFilters;

    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
    @FXML
    private void initialize() throws SQLException {
        getCustomers();
        getAppointments();
        System.out.println();


        setCustomerColumns();
        setAppointmentColumns();
        setChoiceBoxItems();

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
                    appointmentSet.getTimestamp("Start").toLocalDateTime(),
                    appointmentSet.getTimestamp("End").toLocalDateTime()

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
        Utility.setColumnValue(tableAppointments, 6, "startDate");
        Utility.setColumnValue(tableAppointments, 7, "startTime");
        Utility.setColumnValue(tableAppointments, 8, "endDate");
        Utility.setColumnValue(tableAppointments, 9, "endTime");
        Utility.setColumnValue(tableAppointments, 10, "customerID");
        Utility.setColumnValue(tableAppointments, 11, "userID");

        tableAppointments.setItems(allAppointments);
        tableAppointments.getSortOrder().add(tableAppointments.getColumns().get(0));
    }

    private void setChoiceBoxItems() {
        ObservableList<String> months = FXCollections.observableArrayList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        choiceFilter.setItems(months);
        choiceFilter.getSelectionModel().selectFirst();
    }

    @FXML
    private void setChoiceFilter() {
        filteredAppointments = FXCollections.observableArrayList(allAppointments.stream()
                .filter(appointment -> appointment.getStartDateTime()
                        .getMonth().toString()
                        .equalsIgnoreCase(choiceFilter.getValue().toString())).collect(Collectors.toList()));

        tableAppointments.setItems(filteredAppointments);
    }
}
