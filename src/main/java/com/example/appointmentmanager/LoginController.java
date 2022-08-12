package com.example.appointmentmanager;

import helper.JDBC;
import helper.Utility;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController {
    @FXML
    private TextField fieldUserName;
    @FXML
    private PasswordField fieldPassword;
    @FXML
    private Button buttonLogin;
    @FXML
    private Label labelTimezone;

    private ResourceBundle localetext = ResourceBundle.getBundle("Login", Locale.getDefault());

    @FXML
    private void initialize() {
        fieldUserName.setPromptText(localetext.getString("username"));
        fieldPassword.setPromptText(localetext.getString("password"));
        buttonLogin.setText(localetext.getString("login"));
        labelTimezone.setText(localetext.getString("timezone") + ": " + ZoneId.systemDefault());

    }
    @FXML
    private void loginAttempt(ActionEvent event) throws SQLException, IOException {
        String enteredUsername = fieldUserName.getText();
        String enteredPassword = fieldPassword.getText();
        ResultSet user = getUser(enteredUsername);

        if (user.next()) {
            String userPassword = user.getString("Password");
            if (userPassword.equals(enteredPassword)) {
                logLoginAttempt(enteredUsername, true);
                new HomepageController().setFromLogin(true);
                Utility.switchScene(event, "homepage.fxml");
            } else {
                Utility.showError(localetext.getString("loginFailedTitle"),localetext.getString("loginFail"));
                logLoginAttempt(enteredUsername, false);
            }
        } else {
            Utility.showError(localetext.getString("loginFailedTitle"),localetext.getString("loginFail"));
            logLoginAttempt(enteredUsername, false);
        }

    }

    private ResultSet getUser(String username) throws SQLException {
        String sql = "SELECT User_ID, User_Name, Password FROM users WHERE User_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        return rs;
    }

    private void logLoginAttempt(String username, boolean attempt_result)  {
        try {
            FileWriter logFile = new FileWriter("login_activity", true);
            PrintWriter log = new PrintWriter(logFile);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ssa zzzz");
            String date = dateFormat.format(new Date());
            String time = timeFormat.format(new Date());
            String logMessage;

            if (attempt_result) {
                logMessage = "Successful login attempt\n";
            } else {
                logMessage = "Unsuccessful login attempt\n";
            }

            if (username.isBlank()) {
                username = "Not entered";
            }

            logMessage += String.format("User: %s\n", username);
            logMessage += String.format("Date: %s\n", date);
            logMessage += String.format("Time: %s\n\n", time);
            log.append(logMessage);
            log.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}