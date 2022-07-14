module com.example.appointmentmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.appointmentmanager to javafx.fxml;
    exports com.example.appointmentmanager;
}