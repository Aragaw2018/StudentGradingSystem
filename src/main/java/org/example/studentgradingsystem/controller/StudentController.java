package org.example.studentgradingsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.studentgradingsystem.database.DBConnection;
import org.example.studentgradingsystem.model.Student;
import org.example.studentgradingsystem.util.ValidationUtil;

import java.sql.*;

public class StudentController {
    @FXML private TextField idField, firstNameField, lastNameField, emailField, usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colId, colFirstName, colLastName, colEmail, colGender;

    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female"));
        setupTable();
        loadStudents();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
    }

    @FXML
    private void handleAddStudent() {
        if (!validateInputs()) {
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String userSql = "INSERT INTO users (username, password, role, full_name) VALUES (?, ?, 'STUDENT', ?)";
            PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, usernameField.getText());
            userStmt.setString(2, passwordField.getText());
            userStmt.setString(3, firstNameField.getText() + " " + lastNameField.getText());
            userStmt.executeUpdate();

            ResultSet rs = userStmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                String studentSql = "INSERT INTO students (student_id, user_id, first_name, last_name, email, gender) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement studentStmt = conn.prepareStatement(studentSql);
                studentStmt.setString(1, idField.getText());
                studentStmt.setInt(2, userId);
                studentStmt.setString(3, firstNameField.getText());
                studentStmt.setString(4, lastNameField.getText());
                studentStmt.setString(5, emailField.getText());
                studentStmt.setString(6, genderComboBox.getValue());
                studentStmt.executeUpdate();
            }

            conn.commit();
            loadStudents();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Student registered successfully!");
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            showAlert(Alert.AlertType.ERROR, "Database Error", "Student ID or Username already exists!");
        }
    }

    private boolean validateInputs() {
        String errorMessage = "";
        if (!ValidationUtil.isNotEmpty(idField.getText(), firstNameField.getText(), lastNameField.getText(),
                emailField.getText(), usernameField.getText(), passwordField.getText())) {
            errorMessage += "All text fields must be filled!\n";
        }
        if (genderComboBox.getValue() == null || genderComboBox.getValue().isEmpty()) {
            errorMessage += "Please select a gender (Male/Female)!\n";
        }
        if (!ValidationUtil.isValidEmail(emailField.getText())) {
            errorMessage += "Invalid Email Format!\n";
        }
        if (!ValidationUtil.isAlphabetOnly(firstNameField.getText()) || !ValidationUtil.isAlphabetOnly(lastNameField.getText())) {
            errorMessage += "Names must contain only alphabets!\n";
        }
        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errorMessage);
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void clearFields() {
        idField.clear(); firstNameField.clear(); lastNameField.clear();
        emailField.clear(); usernameField.clear(); passwordField.clear();
        idField.setEditable(true);
        usernameField.setDisable(false);
        passwordField.setDisable(false);
        genderComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleUpdateStudent() {
        if (idField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a student from the table first.");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Update students table
            String studentUpdateSql = "UPDATE students SET first_name = ?, last_name = ?, email = ?, gender = ? WHERE student_id = ?";
            PreparedStatement studentStmt = conn.prepareStatement(studentUpdateSql);
            studentStmt.setString(1, firstNameField.getText());
            studentStmt.setString(2, lastNameField.getText());
            studentStmt.setString(3, emailField.getText());
            studentStmt.setString(4, genderComboBox.getValue());
            studentStmt.setString(5, idField.getText());
            studentStmt.executeUpdate();

            // 2. Update users table (Full Name for consistency)
            String userUpdateSql = "UPDATE users SET full_name = ? WHERE user_id = (SELECT user_id FROM students WHERE student_id = ?)";
            PreparedStatement userStmt = conn.prepareStatement(userUpdateSql);
            userStmt.setString(1, firstNameField.getText() + " " + lastNameField.getText());
            userStmt.setString(2, idField.getText());
            userStmt.executeUpdate();

            conn.commit();
            loadStudents();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Update Success", "Student information has been updated!");

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Update failed.");
        }
    }

    @FXML
    private void handleDeleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String sql = "DELETE FROM users WHERE user_id = (SELECT user_id FROM students WHERE student_id = ?)";
            try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, selected.getStudentId());
                pstmt.executeUpdate();
                loadStudents();
                clearFields();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void handleTableClick() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            idField.setText(selected.getStudentId());
            firstNameField.setText(selected.getFirstName());
            lastNameField.setText(selected.getLastName());
            emailField.setText(selected.getEmail());
            genderComboBox.setValue(selected.getGender());
            idField.setEditable(false);
            usernameField.setDisable(true);
            passwordField.setDisable(true);
        }
    }

    private void loadStudents() {
        studentList.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {
            while (rs.next()) {
                studentList.add(new Student(rs.getString("student_id"), rs.getString("first_name"),
                        rs.getString("last_name"), rs.getString("email"), rs.getString("gender")));
            }
            studentTable.setItems(studentList);
        } catch (SQLException e) { e.printStackTrace(); }
    }
}