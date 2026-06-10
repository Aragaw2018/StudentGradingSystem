package org.example.studentgradingsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.studentgradingsystem.util.SceneSwitcher;

import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private BorderPane mainBoundary;

    @FXML
    public void handleLogout(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/org/example/studentgradingsystem/fxml/login.fxml", "Login - Student Grading System");
    }

    @FXML
    public void handleManageStudents() {
        loadView("/org/example/studentgradingsystem/fxml/students.fxml");
    }

    @FXML
    public void handleManageTeachers() {
        loadView("/org/example/studentgradingsystem/fxml/teachers.fxml");
    }

    @FXML
    public void handleManageSubjects() {
        loadView("/org/example/studentgradingsystem/fxml/subjects.fxml");
    }

    /**
     * Helper method to load different FXML files into the center of the BorderPane
     * This makes the dashboard modular and clean.
     */
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();
            mainBoundary.setCenter(node);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + fxmlPath);
        } catch (NullPointerException e) {
            System.err.println("FXML file not found at: " + fxmlPath);
        }
    }
}