package org.cameraapi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        int screenWidth = (int) Screen.getPrimary().getBounds().getWidth();
        int screenHeight = (int) Screen.getPrimary().getBounds().getHeight();

        int sceneMinWidth = 400, sceneMinHeight = 300, sceneStartWidth = 0, sceneStartHeight = 0;
        if (screenWidth <= 800 && screenHeight <= 600) {
            sceneStartWidth = 400;
            sceneStartHeight = 300;
        } else if (screenWidth <= 1280 && screenHeight <= 720) {
            sceneMinWidth = 500;
            sceneMinHeight = 400;
            sceneStartWidth = 600;
            sceneStartHeight = 500;
        } else if (screenWidth <= 1920 && screenHeight <= 1080) {
            sceneMinWidth = 600;
            sceneMinHeight = 500;
            sceneStartWidth = 700;
            sceneStartHeight = 600;
        } else {
            sceneMinWidth = 700;
            sceneMinHeight = 600;
            sceneStartWidth = 900;
            sceneStartHeight = 800;
        }
        System.out.println("Screen resolution: " + screenWidth + "x" + screenHeight);
        System.out.println("Scene minimum dimension: " + sceneMinWidth + "x" + sceneMinHeight);
        System.out.println("Scene start dimension: " + sceneStartWidth + "x" + sceneStartHeight);

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("camera-home-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setMinHeight(sceneMinHeight);
        stage.setMinWidth(sceneMinWidth);

        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/CameraIconNew.png"))));
        stage.setTitle("Camera");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}