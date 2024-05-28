package org.cameraapi;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class EditorController {
    @FXML
    Canvas imagePreview;
    @FXML
    Button saveButton;
    @FXML
    Button returnButton;
    @FXML
    AnchorPane editorPage;


    private AnimationTimer timer;



    @FXML
    public void initialize() {
    }

    public void initCanvas(Image capture) {
        timer = new AnimationTimer() {

            @Override
            public void handle(long l) {
                imagePreview.setHeight(capture.getHeight());
                imagePreview.setWidth(capture.getWidth());
                imagePreview.getGraphicsContext2D().drawImage(capture, 0, 0);
            }
        };
        timer.start();

        //imagePreview.getGraphicsContext2D().drawImage(capture, 0, 0);
    }

    public void initLiveEffects(boolean flipped) {
        imagePreview.setRotationAxis(new Point3D(0, 1, 0));
        if (flipped) {
            imagePreview.setRotate(0);
        } else {
            imagePreview.setRotate(180);
        }
    }


    @FXML
    public void onReturnButtonClicked() {
        timer.stop();
        try {
            handleHomePage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onSaveButtonClicked() {

    }

    public void handleHomePage() throws IOException {
        ScreenController.activate("home");
    }
}
