package com.example.appointmentmanager;

import helper.JDBC;
import helper.Utility;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Homepage controller
 */
public class HomepageController {

    @FXML
    public static TabPane tabPaneHomepage;
    @FXML
    private TableView<Customer> tableCustomers;
    @FXML
    private TableView<Appointment> tableAppointments;
    @FXML
    private TableView<Appointment> tableReports;
    @FXML
    private RadioButton radioMonthly;
    @FXML
    private RadioButton radioWeekly;
    @FXML
    private Label labelCustomers;
    @FXML
    private Label labelAppointments;
    @FXML
    private Label labelContact;
    @FXML
    private Label labelType;
    @FXML
    private Label labelMonth;
    @FXML
    private Label labelCountry;
    @FXML
    private Label labelCustomer;
    @FXML
    private DatePicker pickerFilter;
    @FXML
    private ComboBox<String> comboBoxContact;
    @FXML
    private ComboBox<String> comboBoxType;
    @FXML
    private ComboBox<String> comboBoxMonth;
    @FXML
    private ComboBox<String> comboBoxCountry;
    @FXML
    private ComboBox<String> comboBoxCustomer;

    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
    private ObservableList<Appointment> upcomingAppointments = FXCollections.observableArrayList();
    private ObservableList<Appointment> contactAppointments = FXCollections.observableArrayList();
    private ObservableList<String> typeList = FXCollections.observableArrayList();
    private ObservableList<String> monthList = FXCollections.observableArrayList("January","February", "March","April", "May", "June", "July", "August", "September", "October", "November", "December");
    private ObservableList<String> countryList = FXCollections.observableArrayList();
    private ObservableList<String> customerNameList = FXCollections.observableArrayList();
    private ObservableList<String> contactList = FXCollections.observableArrayList();
    private HashMap<String, Integer> countryAmount = new HashMap<>();
    private HashMap<String, Integer> customerAmount = new HashMap<>();
    private static boolean fromLogin = false;

    /**
     * Initializes the homepage controller
     * @throws SQLException
     * @throws InterruptedException
     */
    @FXML
    private void initialize() throws SQLException, InterruptedException {
        getCustomers();
        getAppointments();
        getContacts();
        getCountries();
        getReportAppointments();
        getTypeAmount();
        getMonthAmount();
        getCountryAmount();
        getCustomerAmount();

        setCustomerColumns();
        setAppointmentColumns();
        setReportColumns();
        setCountryAmount();
        setCustomerAmount();

        comboBoxMonth.setItems(monthList);
        comboBoxMonth.getSelectionModel().selectFirst();

        if (fromLogin) {
            Platform.runLater(() -> showUpcomingAppointments());
            fromLogin = false;
        }
    }

    /**
     * Sets fromLogin toggle
     * @param fromLogin is fromLogin to set
     */
    public void setFromLogin(boolean fromLogin) {this.fromLogin = fromLogin;}

    /**
     * Shows upcoming appointments
     */
    public void showUpcomingAppointments() {
        if (!upcomingAppointments.isEmpty()) {
            for (Appointment appointment : upcomingAppointments) {
                String title = "Upcoming Appointment";
                String body = "Appointment " +
                        appointment.getTitle() +
                        " is at " +
                        appointment.getStartTime() +
                        " on " + appointment.getStartDate() + "!";

                Utility.showError(title, body);
            }
        } else {
            labelAppointments.setText("No upcoming appointments.");
        }
    }

    /**
     * Gets customers from database
     * @throws SQLException
     */
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
            customerNameList.add(customerSet.getString("Customer_Name"));
        }

        comboBoxCustomer.setItems(customerNameList);
        comboBoxCustomer.getSelectionModel().selectFirst();
    }

    /**
     * Sets customer table column values
     */
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

    /**
     * Gets appointments from database
     * @throws SQLException
     */
    private void getAppointments() throws SQLException {
        allAppointments.clear();

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

            if (appointmentSet.getTimestamp("Start").toLocalDateTime().isAfter(LocalDateTime.now()) && appointmentSet.getTimestamp("Start").toLocalDateTime().isBefore(LocalDateTime.now().plusMinutes(15))) {
                upcomingAppointments.add(curAppointment);
            }

            if (!typeList.contains(appointmentSet.getString("Type"))) {
                typeList.add(appointmentSet.getString("Type"));
            }

            allAppointments.add(curAppointment);
        }

        comboBoxType.setItems(typeList.sorted());
        comboBoxType.getSelectionModel().selectFirst();
    }

    /**
     * Sets appointment table column values
     */
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

    /**
     * Gets contact's appointments
     * @throws SQLException
     */
     @FXML
    private void getReportAppointments() throws SQLException {
        contactAppointments.clear();

        String sql = "SELECT a.Appointment_ID, a.Title, a.Description, a.Location, a.Type, a.Start, a.End, a.Customer_ID, a.User_ID, c.Contact_Name " +
                "FROM appointments AS a " +
                "JOIN contacts AS c ON a.Contact_ID=c.Contact_ID " +
                "WHERE Contact_Name=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, comboBoxContact.getValue());
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

            contactAppointments.add(curAppointment);
        }

        setContactAmount();
    }

    /**
     * Sets report table column volues
     */
    private void setReportColumns() {
        Utility.setColumnValue(tableReports, 0, "id");
        Utility.setColumnValue(tableReports, 1, "title");
        Utility.setColumnValue(tableReports, 2, "description");
        Utility.setColumnValue(tableReports, 3, "simpleStartDateTime");
        Utility.setColumnValue(tableReports, 4, "SimpleEndDateTime");
        Utility.setColumnValue(tableReports, 5, "customerID");

        tableReports.setItems(contactAppointments);
        tableReports.getSortOrder().add(tableReports.getColumns().get(0));
    }

    /**
     * Gets contacts from database
     * @throws SQLException
     */
    private void getContacts() throws SQLException {
        String sql = "SELECT Contact_Name FROM contacts";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet contactSet = ps.executeQuery();

        while (contactSet.next()) {
            contactList.add(contactSet.getString(1));
        }

        comboBoxContact.setItems(contactList.sorted());
        comboBoxContact.getSelectionModel().selectFirst();
    }

    /**
     * Gets countries from database
     * @throws SQLException
     */
    private void getCountries() throws SQLException {
        String sql = "SELECT Country FROM countries";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet countrySet = ps.executeQuery();

        while (countrySet.next()) {
            countryList.add(countrySet.getString(1));
        }

        comboBoxCountry.setItems(countryList.sorted());
        comboBoxCountry.getSelectionModel().selectFirst();
    }

    /**
     * Sets contact label to appointment count
     */
    private void setContactAmount() {
        labelContact.setText("Amount:  " + String.valueOf(contactAppointments.toArray().length));
    }

    /**
     * Gets appointments from database by type
     * @throws SQLException
     */
    @FXML
    private void getTypeAmount() throws SQLException {
        Integer amount = 0;

        String sql = "SELECT Appointment_ID " +
                "FROM appointments " +
                "WHERE Type=?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, comboBoxType.getValue());
        ResultSet appSet = ps.executeQuery();

        while (appSet.next()) {
            amount++;
        }

        labelType.setText("Amount:  " + String.valueOf(amount));
    }

    /**
     * Gets amount of appointments by month
     */
    @FXML
    private void getMonthAmount() {
        Integer amount = 0;

        for (Appointment app : allAppointments) {
            if (app.getStartDateTime().getMonthValue() == monthList.indexOf(comboBoxMonth.getValue()) + 1) {
                amount++;
            }
        }

        labelMonth.setText("Amount:  " + String.valueOf(amount));
    }


    /**
     * Gets appointment count by country
     * @throws SQLException
     */
    private void getCountryAmount() throws SQLException {
        String sql = "SELECT co.Country, count(*) AS CoCount " +
                "FROM appointments a " +
                "JOIN customers c ON a.Customer_ID=c.Customer_ID " +
                "JOIN first_level_divisions fld ON c.Division_ID=fld.Division_ID " +
                "JOIN countries co ON fld.Country_ID=co.Country_ID " +
                "GROUP BY co.Country";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet countrySet = ps.executeQuery();

        while (countrySet.next()) {
            countryAmount.put(countrySet.getString(1), countrySet.getInt(2));
        }
    }

    /**
     * Sets country label to appointment count
     */
    @FXML
    private void setCountryAmount() {
        if (countryAmount.containsKey(comboBoxCountry.getValue())) {
            labelCountry.setText("Amount:  " + String.valueOf(countryAmount.get(comboBoxCountry.getValue())));
        } else {
            labelCountry.setText("Amount:  0");
        }
    }

    /**
     * Gets appointment count by customer
     * @throws SQLException
     */
    private void getCustomerAmount() throws SQLException {
        String sql = "SELECT Customer_Name, count(*) AS CuCount " +
                "FROM appointments a " +
                "JOIN customers c ON a.Customer_ID=c.Customer_ID " +
                "GROUP BY Customer_Name";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet customerSet = ps.executeQuery();

        while (customerSet.next()) {
            customerAmount.put(customerSet.getString(1), customerSet.getInt(2));
        }
    }

    /**
     * Sets customer label to customer appointment count
     */
    @FXML
    private void setCustomerAmount() {
        if (customerAmount.containsKey(comboBoxCustomer.getValue())) {
            labelCustomer.setText("Amount:  " + String.valueOf(customerAmount.get(comboBoxCustomer.getValue())));
        } else {
            labelCustomer.setText("Amount:  0");
        }
    }

    /**
     * Filters appointments based on datepicker
     */
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

    /**
     * Clears filter datepicker
     */
    @FXML
    private void clearDate() {
        pickerFilter.setValue(null);
        tableAppointments.setItems(allAppointments);
    }

    /**
     * Switches to add customer form
     * @param event
     * @throws IOException
     */
    @FXML
    private void switchToAddCustomer(ActionEvent event) throws IOException {
        Utility.switchScene(event, "customerform.fxml");
    }

    /**
     * Switches to edit customer form
     * @param event
     * @throws IOException
     */
    @FXML
    private void switchToEditCustomer(ActionEvent event) throws IOException {
        if (tableCustomers.getSelectionModel().getSelectedItem() != null) {
            new CustomerFormController().setCustomer(tableCustomers.getSelectionModel().getSelectedItem());
            Utility.switchScene(event, "customerform.fxml");
        } else {
            Utility.showError("No customer selected", "Pleaese select a customer to edit.");
        }
    }

    /**
     * Deletes customer from database
     * @param event
     * @throws IOException
     * @throws SQLException
     */
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

                getAppointments();
            }
        } else {
            Utility.showError("No customer selected!", "Pleaese select a customer to delete.");
        }
    }

    /**
     * Switches to add appointment form
     * @param event
     * @throws IOException
     */
    @FXML
    private void switchToAddAppointment(ActionEvent event) throws IOException {
        Utility.switchScene(event, "appointmentform.fxml");
    }

    /**
     * Switches to edit appointment form
     * @param event
     * @throws IOException
     */
    @FXML
    private void switchToEditAppointment(ActionEvent event) throws IOException {
        if (tableAppointments.getSelectionModel().getSelectedItem() != null) {
            new AppointmentFormController().setAppointment(tableAppointments.getSelectionModel().getSelectedItem());
            Utility.switchScene(event, "appointmentform.fxml");
        } else {
            Utility.showError("No appointment selected", "Pleaese select an appointment to edit.");
        }
    }

    /**
     * Deletes appointment from database
     * @param event
     * @throws IOException
     * @throws SQLException
     */
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
