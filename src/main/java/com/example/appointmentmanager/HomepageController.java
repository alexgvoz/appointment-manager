package com.example.appointmentmanager;

import helper.JDBC;
import helper.Utility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

public class HomepageController {

    @FXML
    public static TabPane tabPaneHomepage;
    @FXML
    private TableView<Customer> tableCustomers;
    @FXML
    private TableView<Appointment> tableAppointments;
    @FXML
    private ComboBox comboBoxFilter;
    @FXML
    private RadioButton radioMonthly;
    @FXML
    private RadioButton radioWeekly;
    @FXML
    private Label labelCustomers;
    @FXML
    private Label labelAppointments;
    @FXML
    private DatePicker pickerFilter;

    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();

    @FXML
    private void initialize() throws SQLException {
        getCustomers();
        getAppointments();

        setCustomerColumns();
        setAppointmentColumns();
    }

    private void getCustomers() throws SQLException {
        String sql = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM customers";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet customerSet = ps.executeQuery();

        while (customerSet.next()) {
            Customer curCustomer = new Customer(
                    customerSet.getInt("Customer_ID"),
                    customerSet.getString("Customer_Name"),
                    customerSet.getString("Address"),
                    customerSet.getString("Postal_Code"),
                    customerSet.getString("Phone"),
                    customerSet.getInt("Division_ID")
            );

            allCustomers.add(curCustomer);
        }
    }

    private void setCustomerColumns() {
        Utility.setColumnValue(tableCustomers, 0, "id");
        Utility.setColumnValue(tableCustomers, 1, "name");
        Utility.setColumnValue(tableCustomers, 2, "phone");
        Utility.setColumnValue(tableCustomers, 3, "divisionID");
        Utility.setColumnValue(tableCustomers, 4, "address");
        Utility.setColumnValue(tableCustomers, 5, "postalCode");

        tableCustomers.setItems(allCustomers);
        tableCustomers.getSortOrder().add(tableCustomers.getColumns().get(0));
    }

    private void getAppointments() throws SQLException {
        String sql = "SELECT a.Appointment_ID, a.Title, a.Description, a.Location, a.Type, a.Start, a.End, a.Customer_ID, a.User_ID, c.Contact_Name " +
                "FROM appointments AS a " +
                "JOIN contacts AS c ON a.Contact_ID=c.Contact_ID";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet appointmentSet = ps.executeQuery();

        while (appointmentSet.next()) {
            Appointment curAppointment = new Appointment(
                    appointmentSet.getInt("Appointment_ID"),
                    appointmentSet.getString("Title"),
                    appointmentSet.getString("Description"),
                    appointmentSet.getString("Location"),
                    appointmentSet.getString("Contact_Name"),
                    appointmentSet.getString("Type"),
                    appointmentSet.getInt("Customer_ID"),
                    appointmentSet.getInt("User_ID"),
                    appointmentSet.getTimestamp("Start").toLocalDateTime(),
                    appointmentSet.getTimestamp("End").toLocalDateTime()

            );

            allAppointments.add(curAppointment);
        }
    }

    private void setAppointmentColumns() {
        Utility.setColumnValue(tableAppointments, 0, "id");
        Utility.setColumnValue(tableAppointments, 1, "title");
        Utility.setColumnValue(tableAppointments, 2, "description");
        Utility.setColumnValue(tableAppointments, 3, "location");
        Utility.setColumnValue(tableAppointments, 4, "contact");
        Utility.setColumnValue(tableAppointments, 5, "type");
        Utility.setColumnValue(tableAppointments, 6, "startDate");
        Utility.setColumnValue(tableAppointments, 7, "startTime");
        Utility.setColumnValue(tableAppointments, 8, "endDate");
        Utility.setColumnValue(tableAppointments, 9, "endTime");
        Utility.setColumnValue(tableAppointments, 10, "customerID");
        Utility.setColumnValue(tableAppointments, 11, "userID");

        tableAppointments.setItems(allAppointments);
        tableAppointments.getSortOrder().add(tableAppointments.getColumns().get(0));
    }

    @FXML
    private void filterAppointments() {
        if (pickerFilter.getValue() == null) {
            tableAppointments.setItems(allAppointments);
            tableAppointments.getSortOrder().add(tableAppointments.getColumns().get(0));
        } else if (radioMonthly.isSelected()) {
            filteredAppointments = FXCollections.observableArrayList(allAppointments.stream()
                    .filter(appointment -> appointment.getStartDateTime()
                    .getMonthValue()
                     == pickerFilter.getValue().getMonthValue() && appointment.getStartDateTime().getYear() == pickerFilter.getValue().getYear()).collect(Collectors.toList()));


            tableAppointments.setItems(filteredAppointments);
            tableAppointments.getSortOrder().add(tableAppointments.getColumns().get(0));

        } else if (radioWeekly.isSelected()) {
            WeekFields localWeekFields = WeekFields.of(Locale.getDefault());

            filteredAppointments = FXCollections.observableArrayList(allAppointments.stream()
                    .filter(appointment -> appointment.getStartDateTime()
                            .get(localWeekFields.weekOfYear()) == pickerFilter.getValue().get(localWeekFields.weekOfYear()) && appointment.getStartDateTime().getYear() == pickerFilter.getValue().getYear()).collect(Collectors.toList()));

            tableAppointments.setItems(filteredAppointments);
            tableAppointments.getSortOrder().add(tableAppointments.getColumns().get(0));
        }
    }

    @FXML
    private void clearDate() {
        pickerFilter.getEditor().clear();
        tableAppointments.setItems(allAppointments);
    }

    @FXML
    private void switchToAddCustomer(ActionEvent event) throws IOException {
        Utility.switchScene(event, "customerform.fxml");
    }

    @FXML
    private void switchToEditCustomer(ActionEvent event) throws IOException {
        if (tableCustomers.getSelectionModel().getSelectedItem() != null) {
            new CustomerFormController().setCustomer(tableCustomers.getSelectionModel().getSelectedItem());
            Utility.switchScene(event, "customerform.fxml");
        } else {
            Utility.showError("No customer selected", "Pleaese select a customer to edit.");
        }
    }

    @FXML
    private void deleteCustomer(ActionEvent event) throws IOException, SQLException {
        if (tableCustomers.getSelectionModel().getSelectedItem() != null) {
            if (Utility.showConfirm("Are you sure you want to delete this customer?", "Deleting a customer will also delete all appointments associated with them.")) {
                String sql = "DELETE FROM customers " +
                        "WHERE Customer_ID=?";
                PreparedStatement ps = JDBC.connection.prepareStatement(sql);
                ps.setInt(1, tableCustomers.getSelectionModel().getSelectedItem().getId());
                ps.executeUpdate();

                labelCustomers.setText(tableCustomers.getSelectionModel().getSelectedItem().getName() + " has been deleted.");
                allCustomers.remove(tableCustomers.getSelectionModel().getSelectedItem());
            }
        } else {
            Utility.showError("No customer selected!", "Pleaese select a customer to delete.");
        }
    }

    @FXML
    private void switchToAddAppointment(ActionEvent event) throws IOException {
        Utility.switchScene(event, "appointmentform.fxml");
    }

    @FXML
    private void switchToEditAppointment(ActionEvent event) throws IOException {
        if (tableAppointments.getSelectionModel().getSelectedItem() != null) {
            new AppointmentFormController().setAppointment(tableAppointments.getSelectionModel().getSelectedItem());
            Utility.switchScene(event, "appointmentform.fxml");
        } else {
            Utility.showError("No appointment selected", "Pleaese select an appointment to edit.");
        }
    }

    @FXML
    private void deleteAppointment(ActionEvent event) throws IOException, SQLException {
        if (tableAppointments.getSelectionModel().getSelectedItem() != null) {
            if (Utility.showConfirm("Are you sure you want to delete this appointment?", "")) {
                String sql = "DELETE FROM appointments " +
                        "WHERE Appointment_ID=?";
                PreparedStatement ps = JDBC.connection.prepareStatement(sql);
                ps.setInt(1,tableAppointments.getSelectionModel().getSelectedItem().getId());
                ps.executeUpdate();

                labelAppointments.setText(tableAppointments.getSelectionModel().getSelectedItem().getTitle() + " appointment has been deleted.");
                allAppointments.remove(tableAppointments.getSelectionModel().getSelectedItem());
            }
        } else {
            Utility.showError("No appointment selected!", "Pleaese select an appointment to delete.");
        }
    }
}
