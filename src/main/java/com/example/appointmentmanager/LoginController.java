package com.example.appointmentmanager;

import helper.JDBC;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginController {
    @FXML
    private TextField fieldUserName;
    @FXML
    private PasswordField fieldPassword;

    private ArrayList<String> usernames = new ArrayList<String>();
    private ArrayList<String> passwords = new ArrayList<String>();

    @FXML
    private void initialize() {
        try {
            String sql = "SELECT * FROM users";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                usernames.add(rs.getString("User_Name"));
                passwords.add(rs.getString("Password"));
            }

//            System.out.println(usernames);
//            System.out.println(passwords);

        } catch(SQLException e) {
            System.out.println("Ooops didnt work");
        }
    }
    @FXML
    protected void loginAttempt() {
        String entered_username = fieldUserName.getText();
        String entered_password = fieldPassword.getText();

        if (usernames.contains(entered_username)) {
            if (passwords.contains(entered_password)) {
                System.out.println("We in this!");
            }
        }

    }
}