package org.example.studentgradingsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.studentgradingsystem.database.DBConnection;
import org.example.studentgradingsystem.model.Subject;
import org.example.studentgradingsystem.util.ValidationUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SubjectController {
    @FXML private TextField codeField, nameField;
    @FXML private ComboBox<String> teacherComboBox;
    @FXML private TableView<Subject> subjectTable;
    @FXML private TableColumn<Subject, String> colCode, colName, colTeacher;

    private ObservableList<Subject> subjectList = FXCollections.observableArrayList();
    private Map<String, Integer> teacherMap = new HashMap<>(); // Store Name -> ID mapping
    private int selectedSubjectId = -1;

    @FXML
    public void initialize() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("subjectCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        colTeacher.setCellValueFactory(new PropertyValueFactory<>("teacherName"));

        loadTeachers();
        loadSubjects();
    }

    private void loadTeachers() {
        teacherComboBox.getItems().clear();
        teacherMap.clear();
        String query = "SELECT t.teacher_id, u.full_name FROM teachers t JOIN users u ON t.user_id = u.user_id";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String name = rs.getString("full_name");
                int id = rs.getInt("teacher_id");
                teacherMap.put(name, id);
                teacherComboBox.getItems().add(name);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadSubjects() {
        subjectList.clear();
        String query = "SELECT s.subject_id, s.subject_code, s.subject_name, u.full_name FROM subjects s " +
                "LEFT JOIN teachers t ON s.teacher_id = t.teacher_id " +
                "LEFT JOIN users u ON t.user_id = u.user_id";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                subjectList.add(new Subject(rs.getInt("subject_id"), rs.getString("subject_code"),
                        rs.getString("subject_name"), rs.getString("full_name")));
            }
            subjectTable.setItems(subjectList);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleAddSubject() {
        if (!validateInputs()) return;

        String sql = "INSERT INTO subjects (subject_code, subject_name, teacher_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codeField.getText().trim());
            pstmt.setString(2, nameField.getText().trim());
            pstmt.setInt(3, teacherMap.get(teacherComboBox.getValue()));
            pstmt.executeUpdate();
            loadSubjects();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Subject added successfully!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Subject code must be unique.");
        }
    }

    @FXML
    private void handleUpdateSubject() {
        if (selectedSubjectId == -1) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Select a subject from the table.");
            return;
        }
        if (!validateInputs()) return;

        String sql = "UPDATE subjects SET subject_code=?, subject_name=?, teacher_id=? WHERE subject_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codeField.getText().trim());
            pstmt.setString(2, nameField.getText().trim());
            pstmt.setInt(3, teacherMap.get(teacherComboBox.getValue()));
            pstmt.setInt(4, selectedSubjectId);
            pstmt.executeUpdate();
            loadSubjects();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Subject updated successfully!");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleTableClick() {
        Subject selected = subjectTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedSubjectId = selected.getSubjectId();
            codeField.setText(selected.getSubjectCode());
            nameField.setText(selected.getSubjectName());
            teacherComboBox.setValue(selected.getTeacherName());
        }
    }

    private boolean validateInputs() {
        String msg = "";
        if (!ValidationUtil.isNotEmpty(codeField.getText(), nameField.getText())) msg += "Code and Name are required!\n";
        if (teacherComboBox.getValue() == null) msg += "Please assign a teacher!\n";

        if (!msg.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", msg);
            return false;
        }
        return true;
    }

    @FXML
    private void handleDeleteSubject() {
        Subject selected = subjectTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM subjects WHERE subject_id = ?")) {
                pstmt.setInt(1, selected.getSubjectId());
                pstmt.executeUpdate();
                loadSubjects();
                clearFields();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void clearFields() {
        codeField.clear(); nameField.clear();
        teacherComboBox.getSelectionModel().clearSelection();
        selectedSubjectId = -1;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}