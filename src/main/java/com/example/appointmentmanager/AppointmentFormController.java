package com.example.appointmentmanager;

import helper.JDBC;
import helper.Utility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private TextField fieldUser;
    @FXML
    private TextField fieldCustomer;
    @FXML
    private Label labelUser;
    @FXML
    private Label labelCustomer;
    @FXML
    private Label labelAppointment;
    @FXML
    private Button buttonAppointment;

    private static Appointment appointment = null;
    private Map<String, Integer> contactList = new HashMap<>();
    private List listUsers = new ArrayList();
    private List listCustomers = new ArrayList();

    @FXML
    private void initialize() throws SQLException {
        getContacts();
        getUsers();
        getCustomers();

        if (appointment != null) {
            labelAppointment.setText("Edit Appointment");
            buttonAppointment.setText("Save");
            fieldID.setText(String.valueOf(appointment.getId()));
            fieldTitle.setText(appointment.getTitle());
            fieldDescription.setText(appointment.getDescription());
            fieldLocation.setText(appointment.getLocation());
            fieldType.setText(appointment.getType());
            comboBoxContact.getSelectionModel().select(appointment.getContact());
            pickerStartDate.setValue(appointment.getStartDateTime().toLocalDate());

        } else {
            fieldID.setText(String.valueOf(getNextID()));
            pickerStartDate.setValue(java.time.LocalDate.now());
            pickerEndDate.setValue(java.time.LocalDate.now());

        }

        System.out.println(pickerStartDate.getValue().toString());
    }

    public void setAppointment(Appointment appointment) { this.appointment = appointment;}

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

    private void getUsers() throws SQLException {
        String sql = "SELECT User_ID " +
                "FROM  users";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet users = ps.executeQuery();

        while (users.next()) {
            listUsers.add(users.getInt(1));
        }
    }

    private void getCustomers() throws SQLException {
        String sql = "SELECT Customer_ID " +
                "FROM  customers";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet customers = ps.executeQuery();

        while (customers.next()) {
            listCustomers.add(customers.getInt(1));
        }
    }

    private void handleAppointment(ActionEvent event) throws SQLException, IOException {
        Integer errors = 0;

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
            labelLocation.setText("Title field can't be empty.");
            errors += 1;
        } else {
            labelLocation.setText("");
        }
        if (Utility.checkField(fieldType)){
            labelType.setText("Title field can't be empty.");
            errors += 1;
        } else {
            labelType.setText("");
        }
        if (Utility.checkField(fieldUser)){
            labelUser.setText("Title field can't be empty.");
            errors += 1;
        } else {
            labelUser.setText("");
        }
        if (Utility.checkField(fieldCustomer)){
            labelCustomer.setText("Title field can't be empty.");
            errors += 1;
        } else {
            labelCustomer.setText("");
        }

        if (errors == 0) {
            if (appointment != null) {
//                String sql = "UPDATE customers " +
//                        String.format("SET Customer_Name='%s', Address='%s', Phone='%s', Postal_Code='%s', Division_ID=%d ", fieldName.getText(), fieldAddress.getText(), fieldPhone.getText(), fieldPostal.getText(), divisionList.get(comboBoxDivision.getValue())) +
//                        String.format("WHERE Customer_ID=%d", Integer.parseInt(fieldID.getText()));
//                PreparedStatement ps = JDBC.connection.prepareStatement(sql);
//                ps.executeUpdate();
//
//                customer = null;
//                Utility.switchScene(event, "homepage.fxml");
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                String startDateTime = pickerStartDate.getValue().toString() + " "; // TODO: 8/9/2022 Convert to localdatetime

                String sql = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = JDBC.connection.prepareStatement(sql);
                ps.setString(1, fieldTitle.getText());
                ps.setString(2, fieldDescription.getText());
                ps.setString(3, fieldLocation.getText());
                ps.setString(4, fieldType.getText());
                //ps.setTimestamp(5, );

                ps.executeUpdate();

                Utility.switchScene(event, "homepage.fxml");
            }
        }
    }

    @FXML
    private void getContacts() throws SQLException {
        String sql = "SELECT Contact_ID, Contact_Name " +
                "FROM  contacts";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet contacts = ps.executeQuery();

        while (contacts.next()) {
            contactList.put(contacts.getString(2), contacts.getInt(1));
        }

        comboBoxContact.setItems(FXCollections.observableArrayList(contactList.keySet()));

        if (appointment != null) {
            // TODO: 8/8/2022 Select appointment's contact
        } else {
            comboBoxContact.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void switchToHomepage(ActionEvent event) throws IOException {
        appointment = null;
        Utility.switchScene(event, "homepage.fxml");
    }
}
