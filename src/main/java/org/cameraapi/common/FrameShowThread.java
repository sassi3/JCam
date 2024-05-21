package org.cameraapi.common;

import com.github.sarxos.webcam.Webcam;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.cameraapi.effects.Flip;
import org.cameraapi.model.WebcamUtils;

import java.awt.image.BufferedImage;
import java.util.Objects;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.interrupted;

public class FrameShowThread implements Runnable {
    private final ChoiceBox<Webcam> webcamList;
    private Webcam activeWebcam;
    private final ImageView webcamDisplay;
    private Thread frameShowThread = new Thread(this);

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
        BufferedImage image = null;
        while (!interrupted()) {
            try {
                image = activeWebcam.getImage();
                webcamDisplay.setImage(SwingFXUtils.toFXImage(image, null));
                image.flush(); // This prevents memory leakage, it was actually a big deal, but we didn't realize.
            } catch (Exception e) {
                System.out.println("Skipped frame: " + e.getMessage());
                break;
            }
        }
        if(Objects.nonNull(image)) {
            image.flush();  // This is to be sure the image is properly flushed,
                            // even if the loop above is interrupted because of an exception threw in the toFXImage method
                            // (which would stop the image from be flushed).
        }
    }

    public void startShowingFrame() {
        if(frameShowThread.isAlive()) {
            throw new IllegalStateException("Frame showing thread already started");
        }
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
            throw new IllegalThreadStateException("Frame showing thread already stopped.");
        }
    }
}
