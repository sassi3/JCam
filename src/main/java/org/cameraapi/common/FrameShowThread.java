package org.cameraapi.common;

import com.github.sarxos.webcam.Webcam;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.cameraapi.model.WebcamUtils;
import java.util.Objects;
import static java.lang.Thread.interrupted;

public class FrameShowThread implements Runnable {
    private final ChoiceBox<Webcam> webcamList;
    private Webcam activeWebcam;
    private final ImageView webcamDisplay;
    private Thread frameShowThread;
    private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();

    public FrameShowThread(ChoiceBox<Webcam> webcamList, Webcam activeWebcam, ImageView webcamDisplay) {
        Objects.requireNonNull(webcamList);
        Objects.requireNonNull(activeWebcam);
        Objects.requireNonNull(webcamDisplay);
        this.webcamList = webcamList;
        this.activeWebcam = activeWebcam;
        this.webcamDisplay = webcamDisplay;
    }

    @Override
    public void run() {
        webcamList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldWebcam, newWebcam) -> {
            activeWebcam = newWebcam;
            if(!activeWebcam.isOpen()) {
                WebcamUtils.startUpWebcam(activeWebcam, null);
            }
        });
        while (!interrupted()) {
            try {
                imageProperty.set(SwingFXUtils.toFXImage(activeWebcam.getImage(), null));
            } catch (Exception e) {
                System.out.println("Skipped frame: " + e.getMessage());
                break;
            }

        }
    }

    public void startShowingFrame() {
        if(Objects.nonNull(frameShowThread)) {
            if (frameShowThread.isAlive()) {
                throw new IllegalStateException("Frame showing thread already started");
            }
        }
        if(!activeWebcam.isOpen()) {
            activeWebcam.open();
        }
        frameShowThread = new Thread(this);
        frameShowThread.setDaemon(true);
        frameShowThread.setName("Webcam Frame Showing-Thread");
        frameShowThread.start();
        webcamDisplay.imageProperty().bind(imageProperty);
        if (!frameShowThread.isAlive()) {
            frameShowThread.start();
        }
        if (!frameShowThread.isAlive()) {
            throw new IllegalThreadStateException("Failed to start showing frames.");
        }
    }

    public void stopShowingFrame() throws InterruptedException {
        if (frameShowThread.isAlive()) {
            frameShowThread.interrupt();
            frameShowThread.join();
            if (frameShowThread.isAlive()) {
                throw new IllegalThreadStateException("Failed to stop frameShowThread.");
            }
        } else {
            throw new IllegalThreadStateException("Frame showing thread already stopped.");
        }
    }

    public Thread getFrameShowThread() {
        return frameShowThread;
    }
}
