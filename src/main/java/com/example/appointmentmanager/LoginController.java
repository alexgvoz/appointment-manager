package com.example.appointmentmanager;

import helper.JDBC;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        try {
            PreparedStatement prepstatement = JDBC.connection.prepareStatement("SELECT * FROM users");
            ResultSet resset = prepstatement.executeQuery();
            System.out.println(resset.getString("User_Name"));

        } catch(Exception e) {
            System.out.println("Ooops didnt work");
        }
    }

    @FXML
    public void initialize() {

    }
}