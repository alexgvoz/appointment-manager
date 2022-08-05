package com.example.appointmentmanager;

import helper.JDBC;
import helper.Utility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomerFormController {

    @FXML
    private TextField fieldID;
    @FXML
    private TextField fieldName;
    @FXML
    private TextField fieldPhone;
    @FXML
    private TextField fieldAddress;
    @FXML
    private TextField fieldPostal;
    @FXML
    private ComboBox comboBoxCountry;
    @FXML
    private ComboBox comboBoxDivision;

    private static Customer customer= null;
    private Map<String, Integer> countryList = new HashMap<>();
    private Map<String, Integer> divisionList = new HashMap<>();
    private int selectedCountry = 0;

    @FXML
    private void initialize() throws SQLException {
        getCountries();

        if (customer != null) {
            fieldID.setText(String.valueOf(customer.getId()));
            fieldName.setText(customer.getName());
            fieldPhone.setText(customer.getPhone());
            fieldAddress.setText(customer.getAddress());
            fieldPostal.setText(customer.getPostalCode());
        } else {
            fieldID.setText(String.valueOf(getNextID()));
        }
    }

    public void setCustomer(Customer customer) { this.customer = customer; }

    private int getNextID() throws SQLException {
        String sql = "SELECT `AUTO_INCREMENT` " +
                "FROM  INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_SCHEMA = 'client_schedule' " +
                "AND   TABLE_NAME   = 'customers';";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet autoInc = ps.executeQuery();

        autoInc.next();

        return autoInc.getInt(1);
    }

    private void getCountries() throws SQLException {
        String sql = "SELECT Country_ID, Country " +
                "FROM  countries";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet countryData = ps.executeQuery();

        while (countryData.next()) {
            countryList.put(countryData.getString(2), countryData.getInt(1));
        }

        comboBoxCountry.setItems(FXCollections.observableArrayList(countryList.keySet()));


        if (customer != null) {
            comboBoxCountry.getSelectionModel().getSelectedItem().toString();
//            for (String country : countryList) {
//                if (customer.getDivisionID() == 0 ) {
//                    // TODO: 8/4/2022
//                }
//            }
        } else {
            comboBoxCountry.getSelectionModel().selectFirst();
            getDivisions();
        }
    }

   private void getDivisions() throws SQLException {
       String sql = "SELECT Division_ID, Division, Country_ID " +
               "FROM  first_level_divisions";
       PreparedStatement ps = JDBC.connection.prepareStatement(sql);
       ResultSet divisionData = ps.executeQuery();

       while (divisionData.next()) {
           if (divisionData.getInt("Country_ID") == countryList.get(comboBoxCountry.getValue())) {
               // TODO: 8/4/2022  
           }
       }
   }

    @FXML
    private void switchToHomepage(ActionEvent event) throws IOException {
        customer = null;
        Utility.switchScene(event, "homepage.fxml");
    }
}
