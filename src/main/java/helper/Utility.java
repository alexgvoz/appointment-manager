package helper;

import com.example.appointmentmanager.Appointment;
import com.example.appointmentmanager.AppointmentManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Utility {

    private static Stage stage;
    private static Scene scene;

    public static void showError(String header, String text) {
        Alert.AlertType type = Alert.AlertType.WARNING;
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(text);

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(stage);
        alert.showAndWait();
    }

    public static void switchScene(ActionEvent event, String location) throws IOException {
        try {
            Parent root = FXMLLoader.load(AppointmentManager.class.getResource(location));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setColumnValue(TableView<?> table, int column, String id) {
        TableColumn col = table.getColumns().get(column);
        col.setCellValueFactory(new PropertyValueFactory<>(id));
    }
}
