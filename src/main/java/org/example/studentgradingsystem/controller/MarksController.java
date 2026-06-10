package org.example.studentgradingsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.studentgradingsystem.database.DBConnection;
import org.example.studentgradingsystem.model.Result;
import org.example.studentgradingsystem.service.GradingService;
import org.example.studentgradingsystem.util.UserSession;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MarksController {
    @FXML private ComboBox<String> subjectComboBox, studentComboBox;
    @FXML private TextField marksField;
    @FXML private TableView<Result> resultTable;
    @FXML private TableColumn<Result, String> colStudent, colSubject, colGrade, colStatus;
    @FXML private TableColumn<Result, Double> colMarks;

    private Map<String, Integer> subjectMap = new HashMap<>();
    private Map<String, String> studentMap = new HashMap<>();
    private ObservableList<Result> resultList = FXCollections.observableArrayList();
    private int selectedResultId = -1;

    @FXML
    public void initialize() {
        setupTable();
        loadSubjects();
        loadStudents();
        loadResults();
    }

    private void setupTable() {
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        colMarks.setCellValueFactory(new PropertyValueFactory<>("marks"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadSubjects() {
        subjectComboBox.getItems().clear();
        subjectMap.clear();

        // Filter subjects by the logged-in Teacher ID
        String query = "SELECT s.subject_id, s.subject_name FROM subjects s " +
                "JOIN teachers t ON s.teacher_id = t.teacher_id " +
                "WHERE t.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, UserSession.getUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                subjectMap.put(rs.getString("subject_name"), rs.getInt("subject_id"));
                subjectComboBox.getItems().add(rs.getString("subject_name"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadStudents() {
        studentComboBox.getItems().clear();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {
            while (rs.next()) {
                String name = rs.getString("first_name") + " " + rs.getString("last_name");
                studentMap.put(name, rs.getString("student_id"));
                studentComboBox.getItems().add(name);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadResults() {
        resultList.clear();
        // Show only results for subjects taught by this teacher
        String query = "SELECT r.result_id, s.first_name, s.last_name, sub.subject_name, r.marks, r.grade, r.status, s.student_id " +
                "FROM results r JOIN students s ON r.student_id = s.student_id " +
                "JOIN subjects sub ON r.subject_id = sub.subject_id " +
                "JOIN teachers t ON sub.teacher_id = t.teacher_id " +
                "WHERE t.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, UserSession.getUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                resultList.add(new Result(rs.getInt("result_id"), rs.getString("student_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"), rs.getString("subject_name"),
                        rs.getDouble("marks"), rs.getString("grade"), rs.getString("status")));
            }
            resultTable.setItems(resultList);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleSubmitMarks() {
        if (!validateInputs()) return;
        try {
            double marks = Double.parseDouble(marksField.getText());
            String sql = "INSERT INTO results (student_id, subject_id, marks, grade, status) VALUES (?, ?, ?, ?, ?)";
            saveToDatabase(sql, marks, false);
        } catch (NumberFormatException e) { showAlert("Invalid Input", "Please enter a valid number."); }
    }

    @FXML
    private void handleUpdateMarks() {
        if (selectedResultId == -1) { showAlert("No Selection", "Select a record to update."); return; }
        if (!validateInputs()) return;
        try {
            double marks = Double.parseDouble(marksField.getText());
            String sql = "UPDATE results SET student_id=?, subject_id=?, marks=?, grade=?, status=? WHERE result_id=?";
            saveToDatabase(sql, marks, true);
        } catch (NumberFormatException e) { showAlert("Invalid Input", "Please enter a valid number."); }
    }

    private void saveToDatabase(String sql, double marks, boolean isUpdate) {
        String grade = GradingService.calculateGrade(marks);
        String status = GradingService.calculateStatus(marks);
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentMap.get(studentComboBox.getValue()));
            pstmt.setInt(2, subjectMap.get(subjectComboBox.getValue()));
            pstmt.setDouble(3, marks);
            pstmt.setString(4, grade);
            pstmt.setString(5, status);
            if (isUpdate) pstmt.setInt(6, selectedResultId);
            pstmt.executeUpdate();
            loadResults();
            clearFields();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleDeleteMarks() {
        if (selectedResultId == -1) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM results WHERE result_id=?")) {
            pstmt.setInt(1, selectedResultId);
            pstmt.executeUpdate();
            loadResults();
            clearFields();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleTableClick() {
        Result selected = resultTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedResultId = selected.getResultId();
            studentComboBox.setValue(selected.getStudentName());
            subjectComboBox.setValue(selected.getSubjectName());
            marksField.setText(String.valueOf(selected.getMarks()));
        }
    }

    private boolean validateInputs() {
        if (subjectComboBox.getValue() == null || studentComboBox.getValue() == null || marksField.getText().isEmpty()) {
            showAlert("Validation Error", "All fields are required!");
            return false;
        }
        return true;
    }

    @FXML private void clearFields() {
        marksField.clear();
        subjectComboBox.getSelectionModel().clearSelection();
        studentComboBox.getSelectionModel().clearSelection();
        selectedResultId = -1;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}