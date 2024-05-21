package org.cameraapi.common;

import com.github.sarxos.webcam.Webcam;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.cameraapi.effects.Flip;
import org.cameraapi.model.WebcamUtils;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.interrupted;

public class FrameShowThread implements Runnable {
    private final ChoiceBox<Webcam> webcamList;
    private Webcam activeWebcam;
    private final ImageView webcamDisplay;
    private Thread frameShowThread;

    public FrameShowThread(ChoiceBox<Webcam> webcamList, Webcam activeWebcam, ImageView webcamDisplay) {
        this.webcamList = webcamList;
        this.activeWebcam = activeWebcam;
        this.webcamDisplay = webcamDisplay;
    }

    @Override
    public void run() {
        webcamList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldWebcam, newWebcam) -> {
            activeWebcam = newWebcam;
            if(!activeWebcam.isOpen()) {
                WebcamUtils.openWebcam(activeWebcam);
            }
        });

        while (!interrupted()) {
            try {
                Image image = SwingFXUtils.toFXImage(activeWebcam.getImage(), null);
                webcamDisplay.setImage(image);
                Flip.viewportFlipper(webcamDisplay);
            } catch (Exception e) {
                System.out.println("Skipped frame: " + e.getMessage());
                break;
            }
        }
    }

    public void startShowingFrame() {
        frameShowThread = new Thread(this);
        frameShowThread.setDaemon(true);
        frameShowThread.setName("Webcam Frame Showing-Thread");
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
            System.out.println("frameShowThread is not running. There is nothing to stop.");
        }
    }
}
