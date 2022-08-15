package com.example.appointmentmanager;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

/**
 * Application entry point
 */
public class AppointmentManager extends Application {

    /**
     * Sets up application for launch
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        final int FORM_NUM = 0;

        FXMLLoader fxmlLoader;

        switch (FORM_NUM) {
            case 1:
                fxmlLoader = new FXMLLoader(AppointmentManager.class.getResource("homepage.fxml"));
                break;
            default:
                fxmlLoader = new FXMLLoader(AppointmentManager.class.getResource("login.fxml"));
        }

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Appointment Manager");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Launches application
     * @param args
     */
    public static void main(String[] args) {
        JDBC.openConnection();
        //Locale.setDefault(new Locale("fr")); //Sets login page language to French
        launch();
    }
}