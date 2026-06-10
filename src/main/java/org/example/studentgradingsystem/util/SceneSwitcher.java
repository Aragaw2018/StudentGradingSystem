package org.example.studentgradingsystem.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.studentgradingsystem.MainApp;

import java.io.IOException;

public class SceneSwitcher {
    public static void switchScene(Stage currentStage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            currentStage.setTitle(title);
            currentStage.setScene(scene);
            currentStage.centerOnScreen();
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}