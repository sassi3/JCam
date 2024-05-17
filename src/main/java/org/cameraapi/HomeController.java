package org.cameraapi;

import java.io.IOException;
import java.util.HashMap;
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
import org.cameraapi.effects.LiveEffect;

import static java.lang.Thread.interrupted;

public class HomeController {
    private boolean frozenFlipStatus;
    @FXML private ImageView webcamDisplay;
    @FXML private ImageView resultImage;
    @FXML private ChoiceBox<Webcam> webcamList;
    private static ObservableList<Webcam> webcams;
    private Webcam activeWebcam;
    private final HashMap<Integer, LiveEffect> liveEffects = new HashMap<>();
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


        // Allocations
        webcams = FXCollections.observableArrayList();

        // Fetching webcams
        new WebcamListener();
        webcamList.setItems(webcams);
        webcamList.getSelectionModel().selectFirst();

        // Starting webcam
        try {
            activeWebcam = webcamList.getSelectionModel().getSelectedItem();
            webcamList.setValue(activeWebcam);
            openWebcam(activeWebcam);
            startShowingFrame();
        } catch (IllegalStateException | NoSuchElementException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }

        // Initializing live effects
        initializeLiveEffects();
        Flip.setRotationValue(180);

    }

    // ---------------- OPEN & CLOSE ----------------
    private void openWebcam(Webcam webcam) {
        if (webcam.isOpen()) {
            throw new RuntimeException("Webcam is already open.");
        }
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
    // ---- HANDLE WEBCAM DISPLAY THREAD ----
    private void startShowingFrame() {
        frameShowThread = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                webcamList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldWebcam, newWebcam) -> {
                    activeWebcam = newWebcam;
                    if(!activeWebcam.isOpen()) {
                        openWebcam(activeWebcam);
                    }
                    handleWebcamChangeDialog();
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

        frameShowThread.setDaemon(true);
        frameShowThread.setName("Camera Frame Showing");

        if (!frameShowThread.isAlive()) {
            frameShowThread.start();
            Thread.yield();
        }
        if (!frameShowThread.isAlive()) {
            throw new IllegalThreadStateException("Failed to start showing frames.");
        }
    }
    private void stopShowingFrame() throws InterruptedException {
        if (frameShowThread.isAlive()) {
            frameShowThread.interrupt();
            frameShowThread.join();
            if (frameShowThread.isAlive()) {
                throw new IllegalThreadStateException("Failed to stop frameShowThread.");
            }
        } else {
            System.out.println("frameShowThread is not running. There is nothing to stop.");
        }
    }

    // --------- WEBCAMS LIST GETTER ----------
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
        if (!liveEffects.get(LiveEffect.FLIP).isApplied()) {
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
    private void savePicture(Image picture) {
        // something...
    }

    // ------------ EFFECTS HANDLERS ------------

    public void initializeLiveEffects(){
        liveEffects.put(LiveEffect.FLIP,new Flip(true,false));
        liveEffects.put(LiveEffect.FREEZE,new Freeze(true ,false));
        // you could add more...

        // Enabling effects
        liveEffects.get(LiveEffect.FLIP).enable();
        liveEffects.get(LiveEffect.FREEZE).enable();
    }


    @FXML
    private void flipCamera() {
        if (liveEffects.get(LiveEffect.FLIP).isDisabled()) {
            throw new RuntimeException("Flip is currently disabled.");
        }
        liveEffects.get(LiveEffect.FLIP).applyEffect(webcamDisplay);
        flipToggleButton.setText(flipToggleButton.isSelected() ? "Unflip" : "Flip");
    }

    @FXML
    private void freezeCamera() {
        if (liveEffects.get(LiveEffect.FREEZE).isDisabled()) {
            throw new RuntimeException("Freeze is currently disabled.");
        }
        liveEffects.get(LiveEffect.FREEZE).applyEffect(webcamDisplay);
        if(liveEffects.get(LiveEffect.FREEZE).isApplied()) {
            frozenPicture = webcamDisplay.getImage();   // saves the displayed frame when the freeze button is pressed
            frozenFlipStatus = liveEffects.get(LiveEffect.FLIP).isApplied();    // saves the status of the flip button when the freeze button is pressed
            try {
                stopShowingFrame();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Freeze.freeze(webcamDisplay,frozenPicture);
        }
        else {
            startShowingFrame();
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
            if(liveEffects.get(LiveEffect.FREEZE).isApplied()) {
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
                if (!liveEffects.get(LiveEffect.FLIP).isApplied()) { // Check if cam is currently flipped
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
            System.err.println("Error: " + e.getMessage());
            AlertWindows.showFatalError();
            System.exit(3);
        }
    }

    @FXML
    public void handleWebcamChangeDialog() {
        //---------- SCENE LOADING --------
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("webcam-change-dialog.fxml"));
        try {
            DialogPane changePane = loader.load();

            WebcamChangeDialogController ChangeDialogController = loader.getController();

            //-------- DIALOG SET-UP AND EXIT ----------
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