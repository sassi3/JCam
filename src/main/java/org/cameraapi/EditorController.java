package org.cameraapi;

import com.github.sarxos.webcam.Webcam;
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
    private Webcam activeWebcam;



    @FXML
    public void initialize() {
    }

    public void initCanvas(Canvas canvas, Image capture) {
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

    public void initLiveEffects(boolean flipped) {
        imagePreview.setRotationAxis(new Point3D(0, 1, 0));
        if (flipped) {
            imagePreview.setRotate(0);
        } else {
            imagePreview.setRotate(180);
        }
    }

    public Canvas getImagePreview() {
        return imagePreview;
    }

    public void setActiveWebcam(Webcam activeWebcam) {
        this.activeWebcam = activeWebcam;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("camera-home-view.fxml"));
        Parent root = loader.load();

        HomeController controller = loader.getController();
        controller.restoreWebcam(activeWebcam);


        Stage stage = (Stage) editorPage.getScene().getWindow();
        double minHeight = stage.getMinHeight();
        double minWidth = stage.getMinWidth();
        double Height = stage.getHeight();
        double Width = stage.getWidth();

        Scene scene = new Scene(root);
        stage.setTitle("Camera");
        stage.setScene(scene);
        stage.setMinHeight(minHeight);
        stage.setMinWidth(minWidth);
        stage.setHeight(Height);
        stage.setWidth(Width);
    }
}
