package com.example.appointmentmanager;

import helper.JDBC;
import helper.Utility;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AppointmentFormController {
    @FXML
    private TextField fieldID;

    @FXML
    private ComboBox comboBoxContact;

    private static Appointment appointment = null;
    private Map<String, Integer> contactList = new HashMap<>();

    @FXML
    private void initialize() throws SQLException {
        getContacts();
        
        if (appointment != null) {
            // TODO: 8/8/2022 Set text fields to appointment values 
        } else {
            fieldID.setText(String.valueOf(getNextID()));
        }
    }

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
