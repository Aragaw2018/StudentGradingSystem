package org.example.studentgradingsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.studentgradingsystem.database.DBConnection;
import org.example.studentgradingsystem.util.SceneSwitcher;
import org.example.studentgradingsystem.util.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField; // Visible password field
    @FXML private CheckBox showPasswordCheckbox;
    @FXML private Label messageLabel;

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();

        // Get password from the active field
        String password = showPasswordCheckbox.isSelected() ? passwordTextField.getText() : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password.");
            return;
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                UserSession.init(rs.getInt("user_id"), rs.getString("username"), rs.getString("full_name"));
                String role = rs.getString("role");
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                navigateToDashboard(role, stage);
            } else {
                messageLabel.setText("Invalid username or password!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Database connection error.");
        }
    }

    // This method toggles between PasswordField and TextField
    @FXML
    private void togglePassword() {
        if (showPasswordCheckbox.isSelected()) {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordField.setVisible(false);
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordTextField.setVisible(false);
        }
    }

    @FXML
    private void handleClear() {
        usernameField.clear();
        passwordField.clear();
        passwordTextField.clear();
        messageLabel.setText("");
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/org/example/studentgradingsystem/fxml/forgot_password.fxml", "Reset Password");
    }

    private void navigateToDashboard(String role, Stage stage) {
        String fxmlPath = switch (role.toUpperCase()) {
            case "ADMIN" -> "/org/example/studentgradingsystem/fxml/admin_dashboard.fxml";
            case "TEACHER" -> "/org/example/studentgradingsystem/fxml/teacher_dashboard.fxml";
            case "STUDENT" -> "/org/example/studentgradingsystem/fxml/student_dashboard.fxml";
            default -> null;
        };

        if (fxmlPath != null) {
            SceneSwitcher.switchScene(stage, fxmlPath, role + " Dashboard");
        } else {
            messageLabel.setText("Unknown user role!");
        }
    }
}