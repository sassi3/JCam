package org.example.cameraapi.controller;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Affine;
import javafx.stage.Modality;
import org.bytedeco.javacv.FrameGrabber;
import javafx.fxml.FXML;
import org.bytedeco.javacv.JavaFXFrameConverter;
import org.example.cameraapi.model.Camera;
import org.example.cameraapi.Effects;

public class CameraController {
    private AnimationTimer timer;
    private Camera camera;
    @FXML private Canvas cameraCanvas;

    // --------- IMAGES' CONTAINERS ---------
    @FXML private Image rawPicture;
    @FXML private Image currentPicture;
    @FXML private ImageView printablePicture;

    // --------- BUTTONS & CHECKBOXES ---------
    @FXML private ToggleButton freezeToggleButton;
    @FXML private ToggleButton flipToggleButton;
    @FXML private Button captureButton;
    private boolean outputChecker;

    @FXML
    public void initialize() {
        camera = new Camera();
        printablePicture = new ImageView();
        outputChecker = true;       // assures that the transform gets applied on output_picture only once
        initializeTimer();
    }

    // -------------- DISARMER --------------
    public void disableInterface() {
        captureButton.disarm();
        freezeToggleButton.disarm();
        flipToggleButton.disarm();
    }

    // ------------ WEBCAM HANDLERS ------------
    @FXML
    private void webcamStop() throws FrameGrabber.Exception {
        camera.stop();
        timer.stop();
        System.out.println("Webcam stopped.");
    }

    @FXML
    private void webcamRestart() throws FrameGrabber.Exception {
        camera.start();
        timer.start();
        System.out.println("Webcam restarted.");
    }

    // ------ TAKING, SHOWING & SAVING PICTURES ------
    @FXML
    private void previewPicture(Image picture) {
        printablePicture.setImage(picture);
        printablePicture.setPreserveRatio(true);
    }

    @FXML
    private void takePicture() throws FrameGrabber.Exception {
        // ? Are you sure ?
        if (outputChecker) {
            outputChecker = false;
            printablePicture.getTransforms().add(new Affine(-1, 0, printablePicture.getFitWidth(), 0, 1, 0));
            // flips what's displayed by the image view around the y-axis
            // and then translates it right (through the x-axis) by the width of the image view itself
        }
        try {
            rawPicture = camera.getConverter().convert(camera.getGrabber().grab());
            previewPicture(rawPicture);
            currentPicture = rawPicture;
        } catch (FrameGrabber.Exception fex) {
            webcamStop();
            showFailedToTakePictureAlert();
            webcamRestart();
            return;
        }
        webcamStop();
        handleEditor();
        webcamRestart();
    }

    @FXML
    private void savePicture(Image picture) {
        // something...
    }

    // ------------ EFFECTS HANDLERS ------------
    @FXML
    private void flipCamera() {
        Effects.flip();
        if (Effects.isFreezed()) {
            Effects.imgFlipper(cameraCanvas.getGraphicsContext2D());
        }
        flipToggleButton.setText(flipToggleButton.isSelected() ? "Unflip" : "Flip");
    }

    @FXML
    private void freezeCamera() {
        Effects.freeze(timer);
        freezeToggleButton.setText(freezeToggleButton.isSelected() ? "Unfreeze" : "Freeze");
    }

    // --------- UNIVERSAL CANVAS PRINTERS ---------
    private void printWebcamFrame(Canvas canvas, FrameGrabber grabber, JavaFXFrameConverter converter) throws Exception {
        canvas.getGraphicsContext2D().drawImage(converter.convert(grabber.grab()), 0, 0, canvas.getWidth(), canvas.getHeight());
        if (!Effects.isFlipped()) Effects.imgFlipper(cameraCanvas.getGraphicsContext2D());
    }

    private void printImg(Canvas canvas, Image img)  {
        canvas.getGraphicsContext2D().drawImage(img, 0, 0);
    }

    // -------------- TIMER --------------
    private void initializeTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    if (Objects.isNull(camera.getGrabber())) {
                        disableInterface();
                        printImg(cameraCanvas, new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/ErrImg.png"))));
                    } else {
                        printWebcamFrame(cameraCanvas, camera.getGrabber(), camera.getConverter());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        timer.start();
    }

    // --------------- DIALOGS ---------------
    @FXML
    public void handleEditor() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("editor-controller-view.fxml"));
            DialogPane editor = loader.load();
            EditorController editorController = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Editor");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(editor);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                // something...
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --------------- ALERTS ---------------
    void showFailedToTakePictureAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.getDialogPane().setMinWidth(675);
        alert.getDialogPane().setMaxWidth(675);
        alert.setTitle("Warning!");
        alert.setHeaderText("Unable to take picture");
        alert.setContentText("""
                The application is unable to take the picture.
                Quick fixes:
                 ~ Retry to take the photo;
                 ~ Check if your webcam works properly. Maybe try to switch to another device using the "Device List" dropdown menu;
                 ~ Try to restart the application;
                 ~ Try to restart the computer;
                 ~ Pray (trust me, it doesn't work).""");
        alert.showAndWait();
    }

    void showFatalError() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.getDialogPane().setMinWidth(675);
        alert.getDialogPane().setMaxWidth(675);
        alert.setTitle("Fatal Error");
        alert.setHeaderText("An error has occurred.");
        alert.setContentText("The application ran into a fatal error.\n" +
                "Try to restart it or the computer.");
        alert.showAndWait();
    }
}