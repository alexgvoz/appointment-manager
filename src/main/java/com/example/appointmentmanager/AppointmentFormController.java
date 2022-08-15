package com.example.appointmentmanager;

import helper.JDBC;
import helper.Utility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Appointment form controller
 */
public class AppointmentFormController {
    @FXML
    private TextField fieldID;
    @FXML
    private TextField fieldTitle;
    @FXML
    private TextField fieldDescription;
    @FXML
    private TextField fieldLocation;
    @FXML
    private TextField fieldType;
    @FXML
    private ComboBox comboBoxContact;
    @FXML
    private DatePicker pickerStartDate;
    @FXML
    private DatePicker pickerEndDate;
    @FXML
    private Label labelTitle;
    @FXML
    private Label labelDescription;
    @FXML
    private Label labelLocation;
    @FXML
    private Label labelType;
    @FXML
    private Label labelStartTime;
    @FXML
    private Label labelEndTime;
    @FXML
    private ComboBox<Integer> comboBoxUsers;
    @FXML
    private ComboBox<Integer> comboBoxCustomers;
    @FXML
    private Label labelAppointment;
    @FXML
    private Button buttonAppointment;
    @FXML
    private Spinner<Integer> spinnerStartHour;
    @FXML
    private Spinner<Integer> spinnerStartMinute;
    @FXML
    private Spinner<Integer> spinnerEndHour;
    @FXML
    private Spinner<Integer> spinnerEndMinute;

    private static Appointment appointment = null;
    private Map<String, Integer> contactList = new HashMap<>();
    private ObservableList<Integer> listUsers = FXCollections.observableArrayList();
    private ObservableList<Integer> listCustomers = FXCollections.observableArrayList();

    /**
     * Initializes the appointment form controller
     * @throws SQLException
     */
    @FXML
    private void initialize() throws SQLException {
        getContacts();
        getUsers();
        getCustomers();

        spinnerHandler();

        if (appointment != null) {
            System.out.println(appointment.getUserID());
            System.out.println(appointment.getCustomerID());
            labelAppointment.setText("Edit Appointment");
            buttonAppointment.setText("Save");
            fieldID.setText(String.valueOf(appointment.getId()));
            fieldTitle.setText(appointment.getTitle());
            fieldDescription.setText(appointment.getDescription());
            fieldLocation.setText(appointment.getLocation());
            fieldType.setText(appointment.getType());
            comboBoxContact.getSelectionModel().select(appointment.getContact());
            comboBoxUsers.getSelectionModel().select((Integer) appointment.getUserID());
            comboBoxCustomers.getSelectionModel().select((Integer) appointment.getCustomerID());
            pickerStartDate.setValue(appointment.getStartDateTime().toLocalDate());
            spinnerStartHour.getValueFactory().setValue(appointment.getStartDateTime().toLocalTime().getHour());
            spinnerStartMinute.getValueFactory().setValue(appointment.getStartDateTime().toLocalTime().getMinute());
            pickerEndDate.setValue(appointment.getStartDateTime().toLocalDate());
            spinnerEndHour.getValueFactory().setValue(appointment.getEndDateTime().toLocalTime().getHour());
            spinnerEndMinute.getValueFactory().setValue(appointment.getEndDateTime().toLocalTime().getMinute());

        } else {
            fieldID.setText(String.valueOf(getNextID()));
            pickerStartDate.setValue(java.time.LocalDate.now());
            pickerEndDate.setValue(java.time.LocalDate.now());

        }
    }

    /**
     * Sets time spinner values
     */
    private void spinnerHandler() {
        var hourStartFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
        hourStartFactory.setWrapAround(true);
        var hourEndFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
        hourEndFactory.setWrapAround(true);
        var minuteStartFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        minuteStartFactory.setWrapAround(true);
        var minuteEndFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        minuteEndFactory.setWrapAround(true);

        spinnerStartHour.setValueFactory(hourStartFactory);
        spinnerEndHour.setValueFactory(hourEndFactory);
        spinnerStartMinute.setValueFactory(minuteStartFactory);
        spinnerEndMinute.setValueFactory(minuteEndFactory);
    }

    /**
     * Sets current appointment
     * @param appointment is appointment to set
     */
    public void setAppointment(Appointment appointment) { this.appointment = appointment;}

    /**
     * @return next appointment ID
     * @throws SQLException
     */
    public int getNextID() throws SQLException {
        String sql = "SELECT `AUTO_INCREMENT` " +
                "FROM  INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_SCHEMA = 'client_schedule' " +
                "AND   TABLE_NAME   = 'appointments'";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet autoInc = ps.executeQuery();

        autoInc.next();

        return autoInc.getInt(1);
    }

    /**
     * Gets users from database
     * @throws SQLException
     */
    private void getUsers() throws SQLException {
        String sql = "SELECT User_ID " +
                "FROM  users";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet users = ps.executeQuery();

        while (users.next()) {
            listUsers.add(users.getInt(1));
        }

        comboBoxUsers.setItems(listUsers.sorted());
        comboBoxUsers.getSelectionModel().selectFirst();
    }

    /**
     * Gets customers from database
     * @throws SQLException
     */
    private void getCustomers() throws SQLException {
        String sql = "SELECT Customer_ID " +
                "FROM  customers";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet customers = ps.executeQuery();

        while (customers.next()) {
            listCustomers.add(customers.getInt(1));
        }

        comboBoxCustomers.setItems(listCustomers.sorted());
        comboBoxCustomers.getSelectionModel().selectFirst();
    }

    /**
     * Handles adding and editing of appointments
     * @param event
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    private void handleAppointment(ActionEvent event) throws SQLException, IOException {
        Integer errors = 0;
        Timestamp startTS = Timestamp.valueOf(pickerStartDate.getValue().atTime(spinnerStartHour.getValue(),spinnerStartMinute.getValue()));
        Timestamp endTS = Timestamp.valueOf(pickerEndDate.getValue().atTime(spinnerEndHour.getValue(), spinnerEndMinute.getValue()));
        LocalTime startTime = LocalTime.of(spinnerStartHour.getValue(), spinnerStartMinute.getValue());
        LocalTime endTime = LocalTime.of(spinnerEndHour.getValue(), spinnerEndMinute.getValue());
        LocalTime dayStart = ZonedDateTime.of(pickerStartDate.getValue().atTime(8,0), ZoneId.of("America/New_York")).withZoneSameInstant(ZoneId.systemDefault()).toLocalTime();
        LocalTime dayEnd = ZonedDateTime.of(pickerStartDate.getValue().atTime(22,0), ZoneId.of("America/New_York")).withZoneSameInstant(ZoneId.systemDefault()).toLocalTime();

        if (Utility.checkField(fieldTitle)){
            labelTitle.setText("Title field can't be empty.");
            errors += 1;
        } else {
            labelTitle.setText("");
        }
        if (Utility.checkField(fieldDescription)){
            labelDescription.setText("Description field can't be empty.");
            errors += 1;
        } else {
            labelDescription.setText("");
        }
        if (Utility.checkField(fieldLocation)){
            labelLocation.setText("Location field can't be empty.");
            errors += 1;
        } else {
            labelLocation.setText("");
        }
        if (Utility.checkField(fieldType)){
            labelType.setText("Type field can't be empty.");
            errors += 1;
        } else {
            labelType.setText("");
        }
        if (pickerStartDate.getValue().isBefore(LocalDate.now())) {
            labelStartTime.setText("Start date can't be before today's date.");
            errors += 1;
        } else if (pickerStartDate.getValue().isAfter(pickerEndDate.getValue())) {
            labelStartTime.setText("Start date can't be after end date.");
            errors += 1;
        } else if (pickerStartDate.getValue().isEqual(LocalDate.now()) && startTime.isBefore(LocalTime.now())) {
            labelStartTime.setText("Start time can't be in the past.");
            System.out.println(startTime);
            System.out.println(LocalTime.now());
            errors += 1;
        } else if (startTime.isBefore(dayStart) || startTime.isAfter(dayEnd)) {
            labelStartTime.setText("Start time is outside of business hours (8am - 10pm EST).");
            errors += 1;
        } else {
            labelStartTime.setText("");
        }
        if (pickerEndDate.getValue().isBefore(pickerStartDate.getValue())) {
            labelEndTime.setText("End date can't be before start date.");
            errors += 1;
        } else if (pickerStartDate.getValue().equals(pickerEndDate.getValue()) && startTime.isAfter(endTime)) {
            labelEndTime.setText("End time can't be before start time.");
            errors += 1;
        } else if (pickerStartDate.getValue().equals(pickerEndDate.getValue()) && startTime.equals(endTime)) {
            labelEndTime.setText("End and start time can't be the same.");
            errors += 1;
        } else if (endTime.isBefore(dayStart) || startTime.isAfter(dayEnd)) {
            labelEndTime.setText("End time is outside of business hours (8am - 10pm EST).");
            errors += 1;
        } else {
            labelEndTime.setText("");
        }

        if (errors == 0) {
            String sql = "SELECT Appointment_ID, Title, Start, End " +
                    "FROM appointments " +
                    "WHERE Customer_ID=?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, comboBoxCustomers.getValue());
            ResultSet customerAppointments = ps.executeQuery();

            String title = "Customer Appointment Overlap";
            String body = "Current appointment overlaps this appointment:\n";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            while (customerAppointments.next()) {
                if ((startTS.before(customerAppointments.getTimestamp(4)) || startTS.equals(customerAppointments.getTimestamp(4))) && (endTS.after(customerAppointments.getTimestamp(3)) || endTS.equals(customerAppointments.getTimestamp(3)))) {
                    if (customerAppointments.getInt(1) != Integer.parseInt(fieldID.getText())) {
                        errors += 1;

                        body += "ID: " + customerAppointments.getInt(1) + "\n" +
                                "Title: " + customerAppointments.getString(2) + "\n" +
                                "Starts: " + customerAppointments.getTimestamp(3).toLocalDateTime().format(formatter) + "\n" +
                                "Ends: " + customerAppointments.getTimestamp(4).toLocalDateTime().format(formatter);

                        Utility.showError(title, body);
                    }
                }
            }
        }

        if (errors == 0) {
            if (appointment != null) {
                String sql = "UPDATE appointments " +
                        "SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Customer_ID=?, User_ID=?, Contact_ID=? " +
                        "WHERE Appointment_ID=?";
                PreparedStatement ps = JDBC.connection.prepareStatement(sql);
                ps.setString(1, fieldTitle.getText());
                ps.setString(2, fieldDescription.getText());
                ps.setString(3, fieldLocation.getText());
                ps.setString(4, fieldType.getText());
                ps.setTimestamp(5, startTS);
                ps.setTimestamp(6, endTS);
                ps.setInt(7, comboBoxCustomers.getValue());
                ps.setInt(8, comboBoxUsers.getValue());
                ps.setInt(9, contactList.get(comboBoxContact.getValue()));
                ps.setInt(10, Integer.parseInt(fieldID.getText()));

                ps.executeUpdate();

                Utility.switchScene(event, "homepage.fxml");
            } else {
                String sql = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = JDBC.connection.prepareStatement(sql);
                ps.setString(1, fieldTitle.getText());
                ps.setString(2, fieldDescription.getText());
                ps.setString(3, fieldLocation.getText());
                ps.setString(4, fieldType.getText());
                ps.setTimestamp(5, startTS);
                ps.setTimestamp(6, endTS);
                ps.setInt(7, comboBoxCustomers.getValue());
                ps.setInt(8, comboBoxUsers.getValue());
                ps.setInt(9, contactList.get(comboBoxContact.getValue()));

                ps.executeUpdate();

                Utility.switchScene(event, "homepage.fxml");
            }
        }
    }

    /**
     * Gets contacts from database
     * @throws SQLException
     */
    @FXML
    private void getContacts() throws SQLException {
        String sql = "SELECT Contact_ID, Contact_Name " +
                "FROM  contacts";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet contacts = ps.executeQuery();

        while (contacts.next()) {
            contactList.put(contacts.getString(2), contacts.getInt(1));
        }

        comboBoxContact.setItems(FXCollections.observableArrayList(contactList.keySet()).sorted());
        comboBoxContact.getSelectionModel().selectFirst();
    }

    /**
     * Switches to homepage
     * @param event
     * @throws IOException
     */
    @FXML
    private void switchToHomepage(ActionEvent event) throws IOException {
        appointment = null;
        Utility.switchScene(event, "homepage.fxml");
    }
}
