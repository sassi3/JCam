package org.cameraapi;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Affine;
import javafx.stage.Modality;
import javafx.fxml.FXML;
import org.cameraapi.common.AlertWindows;
import org.cameraapi.common.Effects;

import static java.lang.Thread.interrupted;

public class CameraController {
    private boolean frozenFlipStatus;
    @FXML private ImageView webcamDisplay;
    @FXML private ImageView resultImage;
    @FXML private ChoiceBox<Webcam> webcamList;
    private static ObservableList<Webcam> webcams;
    private Webcam activeWebcam;
    private Thread frameShowThread;

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
        webcams = FXCollections.observableArrayList();
        new WebcamListener();
        webcamList.setItems(webcams);
        webcamList.getSelectionModel().selectFirst();
        /*webcamList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            activeWebcam = newValue;
        });*/
        try {
            activeWebcam = webcamList.getSelectionModel().getSelectedItem();
            // by default decided to select the first available webcam
            webcamList.setValue(activeWebcam);
        } catch (NoSuchElementException e) {
            System.err.println("Error: no webcams found.");
            System.exit(1);
        }
        try {
            openWebcam(activeWebcam);
            startShowingFrame();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private void openWebcam(Webcam webcam) {
        webcam.open();
        if (!webcam.isOpen()) {
            throw new IllegalStateException("Failed to open webcam.");
        }
    }
    private void closeWebcam(Webcam webcam) {
        if (Objects.isNull(activeWebcam)) {
            throw new IllegalStateException("Webcam is null.");
        }
        activeWebcam.close();
        if (webcam.isOpen()) {
            throw new IllegalStateException("Failed to close webcam.");
        }
    }

    private void startShowingFrame() {
        if (Objects.isNull(frameShowThread)) {
            frameShowThread = new Thread(new Runnable() {
                @Override
                public synchronized void run() {
                    while (!interrupted()) {
                        try {
                            activeWebcam = webcamList.getSelectionModel().getSelectedItem();
                            if(!activeWebcam.isOpen()) {
                                openWebcam(activeWebcam);
                            }
                            Image image = SwingFXUtils.toFXImage(activeWebcam.getImage(), null);
                            webcamDisplay.setImage(image);
                        } catch (Exception e) {
                            System.out.println("Skipped frame");
                        }
                    }
                }
            });
            frameShowThread.setDaemon(true);
            frameShowThread.setName("Camera Frame Showing");
        }
        frameShowThread.start();
        if (!frameShowThread.isAlive()) {
            throw new IllegalThreadStateException("Failed to start showing frame.");
        }
    }
    private void stopShowingFrame() {
        if (Objects.nonNull(frameShowThread)) {
            frameShowThread.interrupt();
            if (frameShowThread.isAlive()) {
                throw new IllegalThreadStateException("Failed to stop frameShowThread.");
            }
        } else {
            System.out.println("frameShowThread is null. There is nothing to stop.");
        }
    }

    public static ObservableList<Webcam> getWebcams() {
        return webcams;
    }



    // ------------- GUI DISARMER -------------
    public void disableInterface() {
        captureButton.disarm();
        freezeToggleButton.disarm();
        flipToggleButton.disarm();
    }

    // ------------ WEBCAM HANDLERS ------------


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
        printablePicture.setImage(webcamDisplay.getImage());

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
            frozenPicture = webcamDisplay.getImage();
                                                        // saves the displayed frame when the freeze button
                                                        // is pressed

            frozenFlipStatus = Effects.isFlipped(); // saves the status of the flip
                                                        // button when the freeze button is pressed
        }
        freezeToggleButton.setText(freezeToggleButton.isSelected() ? "Unfreeze" : "Freeze");
    }

    // --------- UNIVERSAL CANVAS PRINTERS ---------


    private void printImg(Canvas canvas, Image img)  {
        canvas.getGraphicsContext2D().drawImage(img, 0, 0);
    }

    // -------------- TIMER --------------


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