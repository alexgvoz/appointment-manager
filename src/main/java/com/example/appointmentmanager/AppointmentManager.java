package com.example.appointmentmanager;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class AppointmentManager extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppointmentManager.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Appointment Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        JDBC.openConnection();
        //Locale.setDefault(new Locale("fr")); //Sets login page language to French
        launch();
    }
}