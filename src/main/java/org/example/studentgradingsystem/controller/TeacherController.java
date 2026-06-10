package org.example.studentgradingsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.studentgradingsystem.database.DBConnection;
import org.example.studentgradingsystem.model.Teacher;
import org.example.studentgradingsystem.util.ValidationUtil;

import java.sql.*;

public class TeacherController {
    @FXML private TextField fullNameField, emailField, deptField, usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TableView<Teacher> teacherTable;
    @FXML private TableColumn<Teacher, Integer> colId;
    @FXML private TableColumn<Teacher, String> colName, colEmail, colDept;

    private ObservableList<Teacher> teacherList = FXCollections.observableArrayList();
    private int selectedUserId = -1; // To track user record for update

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("teacherId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        loadTeachers();
    }

    private void loadTeachers() {
        teacherList.clear();
        String query = "SELECT t.teacher_id, t.user_id, u.full_name, t.email, t.department FROM teachers t JOIN users u ON t.user_id = u.user_id";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                teacherList.add(new Teacher(rs.getInt("teacher_id"), rs.getInt("user_id"), rs.getString("full_name"), rs.getString("email"), rs.getString("department")));
            }
            teacherTable.setItems(teacherList);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleAddTeacher() {
        if (!validateInputs(true)) return;

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String userSql = "INSERT INTO users (username, password, role, full_name) VALUES (?, ?, 'TEACHER', ?)";
            PreparedStatement pstmtUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            pstmtUser.setString(1, usernameField.getText());
            pstmtUser.setString(2, passwordField.getText());
            pstmtUser.setString(3, fullNameField.getText());
            pstmtUser.executeUpdate();

            ResultSet rs = pstmtUser.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                String teacherSql = "INSERT INTO teachers (user_id, department, email) VALUES (?, ?, ?)";
                PreparedStatement pstmtTeacher = conn.prepareStatement(teacherSql);
                pstmtTeacher.setInt(1, userId);
                pstmtTeacher.setString(2, deptField.getText());
                pstmtTeacher.setString(3, emailField.getText());
                pstmtTeacher.executeUpdate();
            }

            conn.commit();
            loadTeachers();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Teacher added successfully!");
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
        }
    }

    @FXML
    private void handleUpdateTeacher() {
        if (selectedUserId == -1) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Select a teacher from the table.");
            return;
        }
        if (!validateInputs(false)) return;

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Update users table
            String userUpdate = "UPDATE users SET full_name = ? WHERE user_id = ?";
            PreparedStatement pstmtUser = conn.prepareStatement(userUpdate);
            pstmtUser.setString(1, fullNameField.getText());
            pstmtUser.setInt(2, selectedUserId);
            pstmtUser.executeUpdate();

            // Update teachers table
            String teacherUpdate = "UPDATE teachers SET department = ?, email = ? WHERE user_id = ?";
            PreparedStatement pstmtTeacher = conn.prepareStatement(teacherUpdate);
            pstmtTeacher.setString(1, deptField.getText());
            pstmtTeacher.setString(2, emailField.getText());
            pstmtTeacher.setInt(3, selectedUserId);
            pstmtTeacher.executeUpdate();

            conn.commit();
            loadTeachers();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Teacher updated successfully!");
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTableClick() {
        Teacher selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            fullNameField.setText(selected.getFullName());
            emailField.setText(selected.getEmail());
            deptField.setText(selected.getDepartment());
            selectedUserId = selected.getUserId();
            usernameField.setDisable(true);
            passwordField.setDisable(true);
        }
    }

    private boolean validateInputs(boolean checkPassword) {
        String msg = "";
        if (!ValidationUtil.isNotEmpty(fullNameField.getText(), emailField.getText(), deptField.getText())) msg += "Info fields are required!\n";
        if (checkPassword && !ValidationUtil.isNotEmpty(usernameField.getText(), passwordField.getText())) msg += "Credentials are required!\n";
        if (!ValidationUtil.isValidEmail(emailField.getText())) msg += "Invalid email format!\n";

        if (!msg.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", msg);
            return false;
        }
        return true;
    }

    @FXML
    private void handleDeleteTeacher() {
        Teacher selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
                pstmt.setInt(1, selected.getUserId());
                pstmt.executeUpdate();
                loadTeachers();
                clearFields();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void clearFields() {
        fullNameField.clear(); emailField.clear(); deptField.clear();
        usernameField.clear(); passwordField.clear();
        usernameField.setDisable(false); passwordField.setDisable(false);
        selectedUserId = -1;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}