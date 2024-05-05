package org.cameraapi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("camera-home-view.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/CameraIconNew.png"))));
            stage.setTitle("Camera");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load fxml.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}