package org.example.studentgradingsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.studentgradingsystem.database.DBConnection;
import org.example.studentgradingsystem.util.SceneSwitcher;

import java.sql.*;

public class ForgotPasswordController {
    @FXML private TextField userVerifyField, emailVerifyField;
    @FXML private PasswordField newPasswordField, confirmPasswordField;
    @FXML private VBox verificationBox, resetBox;
    @FXML private Label statusLabel;

    private int foundUserId = -1;

    @FXML
    private void handleVerify() {
        String username = userVerifyField.getText();
        String email = emailVerifyField.getText();

        // Query to check if username and email match in users + (students or teachers)
        String query = "SELECT u.user_id FROM users u " +
                "LEFT JOIN students s ON u.user_id = s.user_id " +
                "LEFT JOIN teachers t ON u.user_id = t.user_id " +
                "WHERE u.username = ? AND (s.email = ? OR t.email = ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, email);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                foundUserId = rs.getInt("user_id");
                verificationBox.setVisible(false);
                verificationBox.setManaged(false);
                resetBox.setVisible(true);
                resetBox.setManaged(true);
                statusLabel.setText("Identity Verified! Enter new password.");
                statusLabel.setStyle("-fx-text-fill: green;");
            } else {
                statusLabel.setText("No matching record found!");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleUpdatePassword() {
        String pass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (pass.isEmpty() || !pass.equals(confirm)) {
            statusLabel.setText("Passwords do not match!");
            return;
        }

        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pass);
            pstmt.setInt(2, foundUserId);
            pstmt.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Password updated successfully! You can now login.");
            alert.showAndWait();
            handleBackToLogin(null);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        Stage stage;
        if (event != null) {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        } else {
            stage = (Stage) statusLabel.getScene().getWindow();
        }
        SceneSwitcher.switchScene(stage, "/org/example/studentgradingsystem/fxml/login.fxml", "Login");
    }
}