package org.example.studentgradingsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.studentgradingsystem.util.SceneSwitcher;

import java.io.IOException;

public class TeacherDashboardController {

    @FXML
    private BorderPane mainBoundary; // This matches the fx:id in FXML

    @FXML
    public void handleEnterMarks() {
        // This will load marks_entry.fxml into the center of the dashboard
        loadView("/org/example/studentgradingsystem/fxml/marks_entry.fxml");
    }


    @FXML
    public void handleViewReports() {
        loadView("/org/example/studentgradingsystem/fxml/reports.fxml");
    }
    @FXML
    public void handleLogout(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/org/example/studentgradingsystem/fxml/login.fxml", "Login - Student Grading System");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();
            mainBoundary.setCenter(node); // Sets the center of the BorderPane
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load FXML: " + fxmlPath);
        }
    }
}