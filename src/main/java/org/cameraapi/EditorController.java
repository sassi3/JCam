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

    private Image capture;
    private AnimationTimer timer;
    private boolean flipped;


    @FXML
    public void initialize() {
        initCanvas(imagePreview);
        initLiveEffects(flipped);
    }

    private void initCanvas(Canvas canvas) {
        timer = new AnimationTimer() {

            @Override
            public void handle(long l) {
                canvas.setHeight(capture.getHeight());
                canvas.setWidth(capture.getWidth());
                canvas.getGraphicsContext2D().drawImage(capture, 0, 0);
            }
        };
        timer.start();

        canvas.getGraphicsContext2D().drawImage(capture, 0, 0);
    }

    private void initLiveEffects(boolean flipped) {
        imagePreview.setRotationAxis(new Point3D(0, 1, 0));
        if (flipped) {
            imagePreview.setRotate(0);
        } else {
            imagePreview.setRotate(180);
        }
    }

    public void setCapture(Image capture) {
        this.capture = capture;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
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
