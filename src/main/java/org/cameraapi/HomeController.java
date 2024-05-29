package org.cameraapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.cameraapi.common.AlertWindows;
import org.cameraapi.common.FrameShowThread;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.fxml.FXML;

import org.cameraapi.common.WebcamListener;
import org.cameraapi.effects.Flip;
import org.cameraapi.effects.Freeze;
import org.cameraapi.effects.LiveEffect;
import org.cameraapi.common.WebcamUtils;

import static java.lang.Thread.interrupted;

public class HomeController {
    private static ObservableList<Webcam> webcams;
    private FrameShowThread frameShowThread;

    @FXML private ImageView webcamDisplay;
    @FXML private ImageView printablePicture;
    private Image rawPicture;
    private Image currentPicture;

    private HashMap<Class<? extends LiveEffect>, LiveEffect> liveEffects;
    private Image frozenPicture;
    private boolean frozenFlipStatus;

    @FXML private StackPane stackPane;
    @FXML private ToggleButton themeButton;
    @FXML private ToggleButton freezeToggleButton;
    @FXML private ToggleButton flipToggleButton;
    @FXML private Button captureButton;
    @FXML private ChoiceBox<Webcam> webcamList;

    private WebcamMotionDetector motionDetector;
    @FXML private RadioButton stabilityTray;
    private Thread stabilityTrayThread;

    public void initialize() {
        initTheme();
        initWebcamChoiceBox();
        initWebcam();
        initLiveEffects();
        initMotionMonitor();
    }

    private void initTheme() {
        themeButton.setSelected(false);
        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
    }

    private void initWebcamChoiceBox() {
        webcams = FXCollections.observableArrayList();
        new WebcamListener(webcams);
        webcamList.setItems(webcams);
        webcamList.getSelectionModel().selectFirst();
        webcams.addListener((ListChangeListener<Webcam>) change -> webcamList.setItems(webcams));
    }

    private void initWebcam() {
        Webcam activeWebcam = webcamList.getSelectionModel().getSelectedItem();
        webcamList.setValue(activeWebcam);
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
        initStabilityTrayThread();
        stabilityTrayThread.start();
    }
    private void initStabilityTrayThread() {
        stabilityTrayThread = new Thread(() -> {
            System.out.println("StabilityTray thread started.");
            while (!interrupted() || Objects.isNull(motionDetector)) {
                boolean previousStabilityStatus = stabilityTray.isSelected();
                boolean currentStabilityStatus = !motionDetector.isMotion();
                if (currentStabilityStatus != previousStabilityStatus) {
                    stabilityTray.setSelected(currentStabilityStatus);
                }
            }
            System.out.println("StabilityTray thread stopped.");
        });
        stabilityTrayThread.setDaemon(true);
    }
    private void stopStabilityTrayThread() throws InterruptedException {
        if (!stabilityTrayThread.isAlive()) {
            throw new IllegalStateException("StabilityTray thread is not alive.");
        }
        stabilityTrayThread.interrupt();
        stabilityTrayThread.join();
        if (stabilityTrayThread.isAlive()) {
            throw new IllegalStateException("StabilityTray thread is still alive.");
        }
        stabilityTray.setSelected(false);
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

    @FXML
    private void changeTheme() {
        if (themeButton.isSelected()) {
            Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        } else {
            Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
        }
        themeButton.setText(themeButton.isSelected() ? "Dark" : "Light");
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
        if (liveEffects.get(Freeze.class).isApplied()) {
            Freeze.freeze(frameShowThread);
            stabilityTrayThread.interrupt();
            stabilityTray.setSelected(true);
        } else {
            stabilityTray.setSelected(false);
            initStabilityTrayThread();
            stabilityTrayThread.start();
            frameShowThread.startShowingFrame();
        }
        freezeToggleButton.setText(freezeToggleButton.isSelected() ? "Unfreeze" : "Freeze");
    }

    @FXML
    private void takePicture() {
        try {
            rawPicture = webcamDisplay.getImage();
            currentPicture = rawPicture;
            openEditor(currentPicture);
        } catch (Exception e) {
            AlertWindows.showFailedToTakePictureAlert();
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void openEditor(Image capture) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("editor.fxml"));
        Parent newPane = loader.load();
        ScreenController.addScreen("editor", newPane);

        EditorController controller = loader.getController();
        controller.initCanvas(capture);
        controller.initLiveEffects(liveEffects.get(Flip.class).isApplied());

        ScreenController.slideFromRight(stackPane, "editor");
        controller.resize();
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