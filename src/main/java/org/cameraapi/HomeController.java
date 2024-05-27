package org.cameraapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
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
    @FXML private ImageView printablePicture;
    private Image rawPicture;
    private Image currentPicture;

    @FXML private AnchorPane mainPane;
    @FXML private ToggleButton freezeToggleButton;
    @FXML private ToggleButton flipToggleButton;
    @FXML private Button captureButton;
    @FXML private ChoiceBox<Webcam> webcamList;

    private WebcamMotionDetector motionDetector;
    @FXML private RadioButton stabilityTray;
    private Thread stabilizedThread;

    private FrameShowThread frameShowThread;

    public void initialize() {
        initWebcamChoiceBox();
        initWebcam();
        initLiveEffects();
        initMotionMonitor();
    }

    private void initWebcamChoiceBox() {
        webcams = FXCollections.observableArrayList();
        new WebcamListener();
        webcamList.setItems(webcams);
        webcamList.getSelectionModel().selectFirst();
        webcams.addListener((ListChangeListener<Webcam>) change -> webcamList.setItems(webcams));
    }

    private void initWebcam() {
        Webcam activeWebcam = webcamList.getSelectionModel().getSelectedItem();
        webcamList.setValue(activeWebcam);
        // Detection of webcam's resolution (find a way)
        WebcamUtils.startUpWebcam(activeWebcam, null);
        frameShowThread = new FrameShowThread(webcamList, activeWebcam, webcamDisplay);
        initFrameShowThread(frameShowThread);
    }

    private void initFrameShowThread(FrameShowThread thread) {
        Objects.requireNonNull(thread, "Thread cannot be null");
        thread.startShowingFrame();
    }

    private void initLiveEffects() {
        liveEffects = new HashMap<>();
        liveEffects.put(Flip.class, new Flip());
        liveEffects.put(Freeze.class, new Freeze());

        for (LiveEffect effect : liveEffects.values()) {
            effect.enable();
        }
        liveEffects.get(Flip.class).toggle(webcamDisplay);
    }

    private void initMotionMonitor() {
        stabilityTray.setSelected(true);
        stabilityTray.disarm();

        int interval = 210;
        int threshold = 10;
        int inertia = 10;
        motionDetector = new WebcamMotionDetector(webcamList.getSelectionModel().getSelectedItem(), threshold, inertia);
        motionDetector.setInterval(interval);
        motionDetector.start();
        stabilizedThread = getStabilizedThread();
        stabilizedThread.start();
    }
    private Thread getStabilizedThread() {
        Thread stabilizedThread = new Thread(() -> {
            System.out.println("StabilizedThread started.");
            while (!interrupted() || Objects.isNull(motionDetector)) {
                boolean previousStabilityStatus = stabilityTray.isSelected();
                boolean currentStabilityStatus = !motionDetector.isMotion();
                if (currentStabilityStatus != previousStabilityStatus) {
                    stabilityTray.setSelected(currentStabilityStatus);
                }
            }
            System.out.println("StabilizedThread stopped.");
        });
        stabilizedThread.setDaemon(true);
        return stabilizedThread;
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

    @FXML
    private void previewPicture(Image picture) {
        printablePicture.setImage(picture);
        printablePicture.setPreserveRatio(true);
    }

    @FXML
    private void takePicture() {
        closeSceneI();
        Image capture = webcamDisplay.getImage();
        try {
            handleEditor(capture);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeSceneI(){
        if(frameShowThread.getFrameShowThread().isAlive()) {
            try {
                frameShowThread.stopShowingFrame();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        stabilizedThread.interrupt();
        WebcamUtils.shutDownWebcams(webcams);
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
            Freeze.freeze(frameShowThread);
        } else {
            frameShowThread.startShowingFrame();
        }
        freezeToggleButton.setText(freezeToggleButton.isSelected() ? "Unfreeze" : "Freeze");
    }

    @FXML
    public void handleEditor(Image capture) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("editor-controller-view.fxml"));
        Parent root = loader.load();


        // Loading the controller
        EditorController controller = loader.getController();

        // Here we do operations with the controller before showing the scene
        controller.setCapture(capture);
        controller.setFlipped(liveEffects.get(Flip.class).isApplied());
        controller.initialize(); // Called after setters to ensure that the variable are all updated before loading the scene

        Stage stage = (Stage) mainPane.getScene().getWindow();    // In this case we have a VBox as wrapper instead of AnchorPane
        double minHeight = stage.getMinHeight();
        double minWidth = stage.getMinWidth();
        double Height = stage.getHeight();
        double Width = stage.getWidth();
        Scene scene = new Scene(root);
        stage.setTitle("Editor");
        stage.setScene(scene);
        stage.setMinHeight(minHeight);
        stage.setMinWidth(minWidth);
        stage.setHeight(Height);
        stage.setWidth(Width);
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