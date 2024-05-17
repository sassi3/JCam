package org.cameraapi;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import com.github.sarxos.webcam.Webcam;
import org.cameraapi.common.WebcamListener;
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
import org.cameraapi.effects.Flip;
import org.cameraapi.effects.Freeze;

import static java.lang.Thread.interrupted;

public class HomeController {
    private boolean frozenFlipStatus;
    @FXML private ImageView webcamDisplay;
    @FXML private ImageView resultImage;
    @FXML private ChoiceBox<Webcam> webcamList;
    private static ObservableList<Webcam> webcams;
    private Webcam activeWebcam;

    // --------- IMAGES' CONTAINERS ---------
    @FXML private Image rawPicture;
    @FXML private Image currentPicture;
    @FXML private ImageView printablePicture;
    private Image frozenPicture;

    // --------- BUTTONS & CHECKBOXES ---------
    @FXML private ToggleButton freezeToggleButton;
    @FXML private ToggleButton flipToggleButton;
    @FXML private Button captureButton;

    private final Thread frameShowThread = new Thread(new Runnable() {
        @Override
        public synchronized void run() {
            webcamList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldWebcam, newWebcam) -> {
                activeWebcam = newWebcam;
                if(!activeWebcam.isOpen()) {
                    openWebcam(activeWebcam);
                }
            });

            while (!interrupted()) {
                try {
                    Image image = SwingFXUtils.toFXImage(activeWebcam.getImage(), null);
                    webcamDisplay.setImage(image);
                    Flip.viewportFlipper(webcamDisplay);
                } catch (Exception e) {
                    System.out.println("Skipped frame" + e.getMessage());
                    break;
                }
            }
        }
    });

    public void initialize() {
        // Starting webcam
        frameShowThread.setDaemon(true);
        frameShowThread.setName("Camera Frame Showing");
        webcams = FXCollections.observableArrayList();
        new WebcamListener();
        webcamList.setItems(webcams);
        webcamList.getSelectionModel().selectFirst();

        try {
            activeWebcam = webcamList.getSelectionModel().getSelectedItem();
            webcamList.setValue(activeWebcam);
            openWebcam(activeWebcam);
            startShowingFrame();
        } catch (IllegalStateException | NoSuchElementException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }

        // Enabling live effects
        Flip.enable();
        Flip.setRotationValue(180);
        Freeze.enable();
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
        if (!frameShowThread.isAlive()) {
            frameShowThread.start();
        }
        if (!frameShowThread.isAlive()) {
            throw new IllegalThreadStateException("Failed to start showing frame.");
        }
    }
    private void stopShowingFrame() throws InterruptedException {
        if (Objects.nonNull(frameShowThread)) {
            frameShowThread.interrupt();
            frameShowThread.join();
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

    // ------ TAKING, SHOWING & SAVING PICTURES ------
    @FXML
    private void previewPicture(Image picture) {
        printablePicture.setImage(picture);
        printablePicture.setPreserveRatio(true);
    }

    @FXML
    private void takePicture() {
        if (!Flip.isApplied()) {
            printablePicture.getTransforms().add(new Affine(-1, 0, printablePicture.getFitWidth(), 0, 1, 0));
            // flips what's displayed by the image view around the y-axis
            // and then translates it right (through the x-axis) by the width of the image view itself
        }
        else {
            printablePicture.getTransforms().add(new Affine(1, 0, 0, 0, 1, 0));
            //Identity matrix
        }
        Image takenImage = webcamDisplay.getImage();
        printablePicture.setImage(takenImage);

        handleEditor();
    }

    @FXML
    private void savePicture(Image picture) {
        // something...
    }

    // ------------ EFFECTS HANDLERS ------------
    @FXML
    private void flipCamera() {
        if (!Flip.isEnabled()) {
            throw new RuntimeException("Flip is currently disabled.");
        }
        Flip.flip(webcamDisplay);
        flipToggleButton.setText(flipToggleButton.isSelected() ? "Unflip" : "Flip");
    }

    @FXML
    private void freezeCamera() {
        if (!Freeze.isEnabled()) {
            throw new RuntimeException("Freeze is currently disabled.");
        }
        Freeze.freeze();
        if(Freeze.isApplied()) {
            frozenPicture = webcamDisplay.getImage();   // saves the displayed frame when the freeze button is pressed
            frozenFlipStatus = Flip.isApplied();    // saves the status of the flip button when the freeze button is pressed
        }
        freezeToggleButton.setText(freezeToggleButton.isSelected() ? "Unfreeze" : "Freeze");
    }

    // --------- UNIVERSAL CANVAS PRINTERS ---------
    private void printImg(Canvas canvas, Image img)  {
        canvas.getGraphicsContext2D().drawImage(img, 0, 0);
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
            if(Freeze.isApplied()) {
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
                if (!Freeze.isApplied()) { // Check if cam is currently flipped
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
            System.err.println("HomeController.handleEditor(): Failed to load editor's FXML file.");
            AlertWindows.showFatalError();
            System.exit(3);
        }
    }
}