package com.example.appointmentmanager;

import helper.JDBC;
import helper.Utility;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
    private Label labelName;
    @FXML
    private Label labelPhone;
    @FXML
    private Label labelAddress;
    @FXML
    private Label labelPostal;
    @FXML
    private ComboBox comboBoxCountry;
    @FXML
    private ComboBox comboBoxDivision;
    @FXML
    private Label labelCustomer;
    @FXML
    private Button buttonCustomer;

    private static Customer customer = null;
    private Map<String, Integer> countryList = new HashMap<>();
    private Map<String, Integer> divisionList = new HashMap<>();
    private int selectedCountry = 0;

    @FXML
    private void initialize() throws SQLException {
        getCountries();

        if (customer != null) {
            labelCustomer.setText("Edit Customer");
            buttonCustomer.setText("Save");
            fieldID.setText(String.valueOf(customer.getId()));
            fieldName.setText(customer.getName());
            fieldPhone.setText(customer.getPhone());
            fieldAddress.setText(customer.getAddress());
            fieldPostal.setText(customer.getPostalCode());

            getCountryFromDivision();
        } else {

            fieldID.setText(String.valueOf(getNextID()));
        }
    }

    public void setCustomer(Customer customer) { this.customer = customer; }

    public int getNextID() throws SQLException {
        String sql = "SELECT `AUTO_INCREMENT` " +
                "FROM  INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_SCHEMA = 'client_schedule' " +
                "AND   TABLE_NAME   = 'customers'";
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
        comboBoxCountry.getSelectionModel().selectFirst();
        getDivisions();
    }

    @FXML
   private void getDivisions() throws SQLException {
       String sql = "SELECT Division_ID, Division, Country_ID " +
               "FROM  first_level_divisions";
       PreparedStatement ps = JDBC.connection.prepareStatement(sql);
       ResultSet divisionData = ps.executeQuery();

       selectedCountry = countryList.get(comboBoxCountry.getValue());
       divisionList.clear();

       while (divisionData.next()) {
           if (divisionData.getInt("Country_ID") == selectedCountry) {
               divisionList.put(divisionData.getString("Division"), divisionData.getInt("Division_ID"));
           }
       }

       comboBoxDivision.setItems(FXCollections.observableArrayList(divisionList.keySet()));
       comboBoxDivision.getSelectionModel().selectFirst();
   }

   private void getCountryFromDivision() throws SQLException {
       String sql = "SELECT d.Division_ID, d.Division, d.Country_ID, c.Country " +
               "FROM  first_level_divisions AS d " +
               "JOIN countries AS c ON d.Country_ID = c.Country_ID";
       PreparedStatement ps = JDBC.connection.prepareStatement(sql);
       ResultSet divisionData = ps.executeQuery();

       while (divisionData.next()) {
           if (divisionData.getInt("Division_ID") == customer.getDivisionID()) {
               comboBoxCountry.getSelectionModel().select(divisionData.getString("Country"));
               getDivisions();
               comboBoxDivision.getSelectionModel().select(divisionData.getString("Division"));
           }
       }
   }

   @FXML
   private void handleCustomer(ActionEvent event) throws SQLException, IOException {
        Integer errors = 0;

        if (Utility.checkField(fieldName)){
            labelName.setText("Name field can't be empty.");
            errors += 1;
        } else {
            labelName.setText("");
        }
        if (Utility.checkField(fieldPhone)) {
            labelPhone.setText("Phone field can't be empty.");
            errors += 1;
        } else {
            labelPhone.setText("");
        }
        if (Utility.checkField(fieldAddress)) {
            labelAddress.setText("Address field can't be empty.");
            errors += 1;
        } else {
            labelAddress.setText("");
        }
        if (Utility.checkField(fieldPostal)) {
            labelPostal.setText("Postal code field can't be empty.");
            errors += 1;
        } else {
            labelPostal.setText("");
        }

        if (errors == 0) {
            if (customer != null) {
                String sql = "UPDATE customers " +
                        String.format("SET Customer_Name='%s', Address='%s', Phone='%s', Postal_Code='%s', Division_ID=%d ", fieldName.getText(), fieldAddress.getText(), fieldPhone.getText(), fieldPostal.getText(), divisionList.get(comboBoxDivision.getValue())) +
                        String.format("WHERE Customer_ID=%d", Integer.parseInt(fieldID.getText()));
                PreparedStatement ps = JDBC.connection.prepareStatement(sql);
                ps.executeUpdate();

                customer = null;
                Utility.switchScene(event, "homepage.fxml");
            } else {
                String sql = "INSERT INTO customers (Customer_Name, Address, Phone, Postal_Code, Division_ID) " +
                        String.format("VALUES ('%s', '%s', '%s', '%s', %d)", fieldName.getText(), fieldAddress.getText(), fieldPhone.getText(), fieldPostal.getText(), divisionList.get(comboBoxDivision.getValue()));
                PreparedStatement ps = JDBC.connection.prepareStatement(sql);
                ps.executeUpdate();

                Utility.switchScene(event, "homepage.fxml");
            }
        }
   }

    @FXML
    private void switchToHomepage(ActionEvent event) throws IOException {
        customer = null;
        Utility.switchScene(event, "homepage.fxml");
    }
}
