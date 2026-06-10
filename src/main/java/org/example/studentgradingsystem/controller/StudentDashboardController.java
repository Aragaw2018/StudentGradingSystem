package org.example.studentgradingsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.studentgradingsystem.database.DBConnection;
import org.example.studentgradingsystem.model.Result;
import org.example.studentgradingsystem.util.SceneSwitcher;
import org.example.studentgradingsystem.util.UserSession;

import java.sql.*;

public class StudentDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private TableView<Result> gradeTable;
    @FXML private TableColumn<Result, String> colSubject, colGrade, colStatus;
    @FXML private TableColumn<Result, Double> colMarks;

    private ObservableList<Result> myResults = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Show student name
        welcomeLabel.setText("Welcome, " + UserSession.getFullName());

        // Set up table columns
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        colMarks.setCellValueFactory(new PropertyValueFactory<>("marks"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadMyGrades();
    }

    private void loadMyGrades() {
        myResults.clear();
        // SQL: Fetch results for the student linked to the logged-in User ID
        String query = "SELECT r.marks, r.grade, r.status, sub.subject_name " +
                "FROM results r " +
                "JOIN subjects sub ON r.subject_id = sub.subject_id " +
                "JOIN students s ON r.student_id = s.student_id " +
                "WHERE s.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, UserSession.getUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // We pass placeholder values for IDs we don't need in this view
                myResults.add(new Result(0, "", "", rs.getString("subject_name"),
                        rs.getDouble("marks"), rs.getString("grade"), rs.getString("status")));
            }
            gradeTable.setItems(myResults);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        UserSession.clean(); // Clear session data
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/org/example/studentgradingsystem/fxml/login.fxml", "Login - Student Grading System");
    }
}