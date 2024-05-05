package org.cameraapi;

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
import org.cameraapi.common.AlertWindows;
import org.cameraapi.model.Camera;
import org.cameraapi.common.Effects;

public class CameraController {
    private AnimationTimer timer;
    private Camera camera;
    private boolean frozenFlipStatus;
    @FXML private Canvas cameraCanvas;

    // --------- IMAGES' CONTAINERS ---------
    @FXML private Image rawPicture;
    @FXML private Image currentPicture;
    @FXML private ImageView printablePicture;
    private Image frozenPicture;

    // --------- BUTTONS & CHECKBOXES ---------
    @FXML private ToggleButton freezeToggleButton;
    @FXML private ToggleButton flipToggleButton;
    @FXML private Button captureButton;

    public void initialize() {
        camera = new Camera();
        printablePicture = new ImageView();
        initializeTimer();
    }

    // ------------- GUI DISARMER -------------
    public void disableInterface() {
        captureButton.disarm();
        freezeToggleButton.disarm();
        flipToggleButton.disarm();
    }

    // ------------ WEBCAM HANDLERS ------------
    @FXML
    private void webcamStop() {
        camera.stop(timer);
    }
    @FXML
    private void webcamRestart() {
        camera.start(timer);
    }

    // ------ TAKING, SHOWING & SAVING PICTURES ------
    @FXML
    private void previewPicture(Image picture) {
        printablePicture.setImage(picture);
        printablePicture.setPreserveRatio(true);
    }

    @FXML
    private void takePicture() {

        if (!Effects.isFlipped()) {
            printablePicture.getTransforms().add(new Affine(-1, 0, printablePicture.getFitWidth(), 0, 1, 0));
            // flips what's displayed by the image view around the y-axis
            // and then translates it right (through the x-axis) by the width of the image view itself
        }
        else {
            printablePicture.getTransforms().add(new Affine(1, 0, 0, 0, 1, 0));
            //Identity matrix
        }



        try {
            rawPicture = camera.getConverter().convert(camera.getGrabber().grab());
            previewPicture(rawPicture);
            currentPicture = rawPicture;
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
            webcamStop();
            AlertWindows.showFailedToTakePictureAlert();
            webcamRestart();
            return;
        }

        handleEditor();
    }

    @FXML
    private void savePicture(Image picture) {
        // something...
    }

    // ------------ EFFECTS HANDLERS ------------
    @FXML
    private void flipCamera() {
        Effects.flip();
        if (Effects.isFrozen()) {
            Effects.imgFlipper(cameraCanvas.getGraphicsContext2D());
        }
        flipToggleButton.setText(flipToggleButton.isSelected() ? "Unflip" : "Flip");
    }

    @FXML
    private void freezeCamera() {
        Effects.freeze(timer);
        if(Effects.isFrozen()) {
            try {
                frozenPicture = camera.getConverter().convert(camera.getGrabber().grab());
                                                        // saves the displayed frame when the freeze button
                                                        // is pressed

                frozenFlipStatus = Effects.isFlipped(); // saves the status of the flip
                                                        // button when the freeze button is pressed
            } catch (FrameGrabber.Exception e) {
                throw new RuntimeException(e);
            }
        }
        freezeToggleButton.setText(freezeToggleButton.isSelected() ? "Unfreeze" : "Freeze");
    }

    // --------- UNIVERSAL CANVAS PRINTERS ---------
    private void printWebcamFrame(Canvas canvas, FrameGrabber grabber, JavaFXFrameConverter converter) throws Exception {
        try {
            Image frame = converter.convert(grabber.grab());
            canvas.getGraphicsContext2D().drawImage(frame, 0, 0, canvas.getWidth(), canvas.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("CameraController.printWebcamFrame(): Failed to print grabbed frames.");
            AlertWindows.showFatalError();
            System.exit(2);
        }
        if (!Effects.isFlipped()) {
            Effects.imgFlipper(cameraCanvas.getGraphicsContext2D());
        }
        else {
            Effects.imgUnflipper(cameraCanvas.getGraphicsContext2D());
        }
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
                    if (Objects.nonNull(camera.getGrabber())) {
                        printWebcamFrame(cameraCanvas, camera.getGrabber(), camera.getConverter());
                    } else {
                        disableInterface();
                        printImg(cameraCanvas, new Image(Objects.requireNonNull(getClass().getResourceAsStream("controller/errImg/ErrImg.png"))));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
            //---------- SCENE LOADING --------
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("editor-controller-view.fxml"));
            DialogPane editor = loader.load();
            EditorController editorController = loader.getController();

            //---------- CONTROLLER ACCESS METHODS --------

            // Checks if the cam is currently frozen and decides which picture to show and whether to flip it or not
            if(Effects.isFrozen()) {
                editorController.setPicture(frozenPicture); // show picture taken when cam froze
                if (!frozenFlipStatus) { // Checks if the cam was flipped when froze
                    editorController.getPicturePreview().getTransforms().add(new Affine(-1, 0, editorController.getPicturePreview().getFitWidth(), 0, 1, 0));
                    // flips what's displayed by the image view around the y-axis
                    // and then translates it right (through the x-axis) by the width of the image view itself
                } else {
                    editorController.getPicturePreview().getTransforms().add(new Affine(1, 0, 0, 0, 1, 0));
                    //Identity matrix
                }
            }
            else {
                editorController.setPicture(currentPicture); // Else set picture currently displayed
                if (!Effects.isFlipped()) { // Check if cam is currently flipped
                    editorController.getPicturePreview().getTransforms().add(new Affine(-1, 0, editorController.getPicturePreview().getFitWidth(), 0, 1, 0));
                    // flips what's displayed by the image view around the y-axis
                    // and then translates it right (through the x-axis) by the width of the image view itself
                } else {
                    editorController.getPicturePreview().getTransforms().add(new Affine(1, 0, 0, 0, 1, 0));
                    //Identity matrix
                }
            }
            editorController.initialize();


            //-------- DIALOG SET-UP AND EXIT ----------
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Editor");
            editorController.addDialogIconTo(dialog);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(editor);
            dialog.initOwner(cameraCanvas.getScene().getWindow());

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                // if the dialog button pressed is the OK button calls savePicture method
                savePicture(editorController.getPicture());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("CameraController.handleEditor(): Failed to load editor's FXML file.");
            AlertWindows.showFatalError();
            System.exit(3);
        }
    }
}