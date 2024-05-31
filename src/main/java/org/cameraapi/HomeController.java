package org.cameraapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import com.github.sarxos.webcam.Webcam;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.cameraapi.common.AlertWindows;
import org.cameraapi.common.FrameShowThread;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;

import org.cameraapi.common.WebcamListener;
import org.cameraapi.effects.Flip;
import org.cameraapi.effects.Freeze;
import org.cameraapi.effects.LiveEffect;
import org.cameraapi.common.WebcamUtils;

public class HomeController {
    private static ObservableList<Webcam> webcams;
    private FrameShowThread frameShowThread;

    @FXML private ImageView webcamImageView;
    private Image rawPicture;
    private Image currentPicture;

    private HashMap<Class<? extends LiveEffect>, LiveEffect> liveEffects;

    @FXML private StackPane stackPane;
    @FXML private ToggleButton themeButton;
    @FXML private ToggleButton freezeToggleButton;
    @FXML private ToggleButton flipToggleButton;
    @FXML private Button captureButton;
    @FXML private ChoiceBox<Webcam> webcamChoiceBox;
    @FXML private Text FPSTray;

    @FXML private RadioButton stabilityTray;

    public void initialize() {
        initTheme();
        initWebcamChoiceBox();
        initWebcam();
        initLiveEffects();
    }

    private void initTheme() {
        themeButton.setSelected(false);
        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
    }

    private void initWebcamChoiceBox() {
        webcams = FXCollections.observableArrayList();
        new WebcamListener(webcams);
        webcamChoiceBox.setItems(webcams);
        webcamChoiceBox.getSelectionModel().selectFirst();
        webcams.addListener((ListChangeListener<Webcam>) change -> webcamChoiceBox.setItems(webcams));
    }

    private void initWebcam() {
        Webcam activeWebcam = webcamChoiceBox.getSelectionModel().getSelectedItem();
        webcamChoiceBox.setValue(activeWebcam);
        WebcamUtils.startUpWebcam(activeWebcam, null);
        frameShowThread = new FrameShowThread(webcamChoiceBox, activeWebcam, webcamImageView, FPSTray, stabilityTray);
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
        liveEffects.get(Flip.class).toggle(webcamImageView);
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
        liveEffects.get(Flip.class).toggle(webcamImageView);
        flipToggleButton.setText(flipToggleButton.isSelected() ? "Unflip" : "Flip");
    }

    @FXML
    private void freezeCamera() {
        if (liveEffects.get(Freeze.class).isDisabled()) {
            throw new RuntimeException("Freeze is currently disabled.");
        }
        liveEffects.get(Freeze.class).toggle(webcamImageView);
        if (liveEffects.get(Freeze.class).isApplied()) {
            Freeze.freeze(frameShowThread);
        } else {
            frameShowThread = Freeze.unfreeze(frameShowThread);
        }
        freezeToggleButton.setText(freezeToggleButton.isSelected() ? "Unfreeze" : "Freeze");
    }

    @FXML
    private void takePicture() {
        try {
            rawPicture = webcamImageView.getImage();
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
        Parent nextPane = loader.load();
        try {
            if (!RootController.getRootMap().containsValue(nextPane)) {
                RootController.addRoot("editor", nextPane);
            }
        } catch (Exception e) {
            AlertWindows.showFailedToTakePictureAlert();
            throw new RuntimeException(e);
        }

        EditorController controller = loader.getController();
        controller.initCanvas(capture);
        controller.initLiveEffects(liveEffects.get(Flip.class).isApplied());

        RootController.changeRoot("editor");
    }
}