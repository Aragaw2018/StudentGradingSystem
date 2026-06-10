package org.example.studentgradingsystem;
import org.example.studentgradingsystem.database.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Absolute Path
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/org/example/studentgradingsystem/fxml/login.fxml"));

        if (fxmlLoader.getLocation() == null) {
            System.out.println("Error: FXML file not found! Check the path.");
            return;
        }

        Scene scene = new Scene(fxmlLoader.load(), 500, 600);
        stage.setTitle("Login - Student Grading System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        testConnection();
        launch();
    }

    private static void testConnection() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                System.out.println("---------------------------------------");
                System.out.println("DATABASE CONNECTION: SUCCESSFUL!");
                System.out.println("---------------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("---------------------------------------");
            System.out.println("DATABASE CONNECTION: FAILED!");
            System.out.println("Error: " + e.getMessage());
            System.out.println("---------------------------------------");
        }
    }
}