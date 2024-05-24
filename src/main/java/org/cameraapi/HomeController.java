package org.cameraapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import javafx.collections.ListChangeListener;
import org.cameraapi.common.FrameShowThread;
import org.cameraapi.common.WebcamListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Affine;
import javafx.stage.Modality;
import javafx.fxml.FXML;

import org.cameraapi.common.AlertWindows;
import org.cameraapi.effects.Flip;
import org.cameraapi.effects.Freeze;
import org.cameraapi.effects.LiveEffect;
import org.cameraapi.model.WebcamUtils;

import static java.lang.Thread.interrupted;

public class HomeController {
    private static ObservableList<Webcam> webcams;
    private HashMap<Class<? extends LiveEffect>, LiveEffect> liveEffects;

    @FXML private ImageView webcamDisplay;
    @FXML private Image currentPicture;
    @FXML private ImageView printablePicture;
    private Image frozenPicture;

    @FXML private ToggleButton freezeToggleButton;
    @FXML private ToggleButton flipToggleButton;
    @FXML private Button captureButton;
    @FXML private ChoiceBox<Webcam> webcamList;

    private final int INTERVAL = 100;
    private int threshold = 25;
    private int inertia = 100;
    private WebcamMotionDetector motionDetector;
    private boolean isStable;
    @FXML private RadioButton stabilizedTray;

    private boolean frozenFlipStatus;

    private FrameShowThread frameShowThread;

    public void initialize() {
        try {
            initializeWebcamList();
            initializeLiveEffects();
            initializeMotionMonitor();
        } catch (Exception e) {
            System.err.println("Error initializing controller " + this.getClass() + ": " + e.getMessage());
            System.exit(1);
        }
    }

    private void initializeWebcamList() {
        webcams = FXCollections.observableArrayList();
        new WebcamListener();
        webcamList.setItems(webcams);
        webcamList.getSelectionModel().selectFirst();
        webcams.addListener((ListChangeListener<Webcam>) change -> webcamList.setItems(webcams));

        Webcam activeWebcam = webcamList.getSelectionModel().getSelectedItem();
        webcamList.setValue(activeWebcam);
        WebcamUtils.openWebcam(activeWebcam);

        frameShowThread = new FrameShowThread(webcamList, activeWebcam, webcamDisplay);
        initializeFrameShowThread(frameShowThread);
    }

    private void initializeFrameShowThread(FrameShowThread thread) {
        Objects.requireNonNull(thread, "Thread cannot be null");
        thread.startShowingFrame();
    }

    private void initializeLiveEffects() {
        liveEffects = new HashMap<>();
        liveEffects.put(Flip.class, new Flip());
        liveEffects.put(Freeze.class, new Freeze());

        for (LiveEffect effect : liveEffects.values()) {
            effect.enable();
        }
    }

    private void initializeMotionMonitor() {
        stabilizedTray.setSelected(true);
        stabilizedTray.disarm();
        motionDetector = new WebcamMotionDetector(webcamList.getSelectionModel().getSelectedItem(), threshold, inertia);
        motionDetector.setInterval(INTERVAL);
        motionDetector.start();
        Thread stabilizedThread = getStabilizedThread();
        stabilizedThread.start();
    }

    public void disableInterface() {
        captureButton.disarm();
        freezeToggleButton.disarm();
        flipToggleButton.disarm();
        System.out.println("Interface disabled.");
    }

    public void enableInterface() {
        captureButton.arm();
        freezeToggleButton.arm();
        flipToggleButton.arm();
        System.out.println("Interface enabled.");
    }

    public static ObservableList<Webcam> getWebcams() {
        return webcams;
    }

    private Thread getStabilizedThread() {
        Thread stabilizedThread = new Thread(() -> {
            System.out.println("StabilizedThread started.");
            while (!interrupted() || Objects.isNull(motionDetector)) {
                if (motionDetector.isMotion() != isStable) {
                    isStable = motionDetector.isMotion();
                    stabilizedTray.setSelected(isStable);
                }
            }
            System.out.println("StabilizedThread stopped.");
        });
        stabilizedThread.setDaemon(true);
        return stabilizedThread;
    }

    @FXML
    private void previewPicture(Image picture) {
        printablePicture.setImage(picture);
        printablePicture.setPreserveRatio(true);
    }

    @FXML
    private void takePicture() {
        if (!liveEffects.get(Flip.class).isApplied()) {
            printablePicture.getTransforms().add(new Affine(-1, 0, printablePicture.getFitWidth(), 0, 1, 0));
            // flips what's displayed by the image view around the y-axis
            // and then translates it right (through the x-axis) by the width of the image view itself
        } else {
            printablePicture.getTransforms().add(new Affine(1, 0, 0, 0, 1, 0));
            //Identity matrix
        }
        Image takenImage = webcamDisplay.getImage();
        printablePicture.setImage(takenImage);

        handleEditor();
    }

    @FXML
    private void flipCamera() {
        if (liveEffects.get(Flip.class).isDisabled()) {
            throw new RuntimeException("Flip is currently disabled.");
        }
        liveEffects.get(Flip.class).toggle(webcamDisplay);
        flipToggleButton.setText(flipToggleButton.isSelected() ? "Unflip" : "Flip");
    }

    @FXML
    private void freezeCamera() {
        if (liveEffects.get(Freeze.class).isDisabled()) {
            throw new RuntimeException("Freeze is currently disabled.");
        }
        liveEffects.get(Freeze.class).toggle(webcamDisplay);
        if(liveEffects.get(Freeze.class).isApplied()) {
            frozenPicture = webcamDisplay.getImage();   // saves the displayed frame when the freeze button is pressed
            frozenFlipStatus = liveEffects.get(Flip.class).isApplied();    // saves the status of the flip button when the freeze button is pressed
            try {
                frameShowThread.stopShowingFrame();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Freeze.freeze(webcamDisplay, frozenPicture);
        } else {
            frameShowThread.startShowingFrame();
        }
        freezeToggleButton.setText(freezeToggleButton.isSelected() ? "Unfreeze" : "Freeze");
    }

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
            if(liveEffects.get(Freeze.class).isApplied()) {
                editorController.setPicture(frozenPicture); // show picture taken when cam froze
                if (!frozenFlipStatus) { // Checks if the cam was flipped when froze
                    editorController.getPicturePreview().getTransforms().add(new Affine(-1, 0, editorController.getPicturePreview().getFitWidth(), 0, 1, 0));
                    // flips what's displayed by the image view around the y-axis
                    // and then translates it right (through the x-axis) by the width of the image view itself
                } else {
                    editorController.getPicturePreview().getTransforms().add(new Affine(1, 0, 0, 0, 1, 0));
                    //Identity matrix
                }
            } else {
                editorController.setPicture(currentPicture); // Else set picture currently displayed
                if (!liveEffects.get(Flip.class).isApplied()) { // Check if cam is currently flipped
                    editorController.getPicturePreview().getTransforms().add(new Affine(-1, 0, editorController.getPicturePreview().getFitWidth(), 0, 1, 0));
                    // flips what's displayed by the image view around the y-axis
                    // and then translates it right (through the x-axis) by the width of the image view itself
                } else {
                    editorController.getPicturePreview().getTransforms().add(new Affine(1, 0, 0, 0, 1, 0));
                    //Identity matrix
                }
            }
            editorController.initialize();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Editor");
            editorController.addDialogIconTo(dialog);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(editor);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                // if the dialog button pressed is the OK button calls savePicture method
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            AlertWindows.showFatalError();
            System.exit(3);
        }
    }

    @FXML
    public void handleWebcamChangeDialog() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("webcam-change-dialog.fxml"));
        try {
            DialogPane changePane = loader.load();

            WebcamChangeDialogController ChangeDialogController = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(changePane);
            dialog.setTitle("Warning");
            dialog.initModality(Modality.WINDOW_MODAL);

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.NO) == ButtonType.YES) {
                // if the dialog button pressed is the YES button it will reset the effect
                ChangeDialogController.reset(liveEffects);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}