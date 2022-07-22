package com.example.appointmentmanager;

import helper.JDBC;
import helper.Utility;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.FileNotFoundException;
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
    private void initialize() throws FileNotFoundException {
        fieldUserName.setPromptText(localetext.getString("username"));
        fieldPassword.setPromptText(localetext.getString("password"));
        buttonLogin.setText(localetext.getString("login"));
        labelTimezone.setText(localetext.getString("timezone") + ": " + ZoneId.systemDefault());

    }
    @FXML
    protected void loginAttempt(ActionEvent event) throws SQLException, IOException {
        String entered_username = fieldUserName.getText();
        String entered_password = fieldPassword.getText();
        ResultSet user = getUser(entered_username);

        if (user.next()) {
            String user_password = user.getString("Password");
            if (user_password.equals(entered_password)) {
                logLoginAttempt(entered_username, true);
                System.out.println("We in this!");
                Utility.switchScene(event, "homepage.fxml");
            } else {
                Utility.showError(localetext.getString("loginFailedTitle"),localetext.getString("loginFail"));
                logLoginAttempt(entered_username, false);
            }
        } else {
            Utility.showError(localetext.getString("loginFailedTitle"),localetext.getString("loginFail"));
            logLoginAttempt(entered_username, false);
        }

    }

    protected ResultSet getUser(String username) throws SQLException {
        String sql = "SELECT User_ID, User_Name, Password FROM users WHERE User_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        return rs;
    }

    protected void logLoginAttempt(String username, boolean attempt_result)  {
        try {
            FileWriter log_file = new FileWriter("login_activity", true);
            PrintWriter log = new PrintWriter(log_file);
            SimpleDateFormat date_format = new SimpleDateFormat("MM-dd-yyyy");
            SimpleDateFormat time_format = new SimpleDateFormat("h:mm:ssa zzzz");
            String date = date_format.format(new Date());
            String time = time_format.format(new Date());
            String log_message = new String();

            if (attempt_result) {
                log_message = "Successful login attempt\n";
            } else {
                log_message = "Unsuccessful login attempt\n";
            }

            if (username.isBlank()) {
                username = "Not entered";
            }

            log_message += String.format("User: %s\n", username);
            log_message += String.format("Date: %s\n", date);
            log_message += String.format("Time: %s\n\n", time);
            log.append(log_message);
            log.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}