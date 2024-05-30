package org.cameraapi.common;

import com.github.sarxos.webcam.Webcam;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.time.Instant;
import java.util.Objects;

public class FrameShowThread extends Thread {
    private final ChoiceBox<Webcam> webcamList;
    private final Text FPSTray;

    private Webcam activeWebcam;
    private final ImageView webcamDisplay;
    private Thread FPSTrayThread;
    private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();

    public FrameShowThread(ChoiceBox<Webcam> webcamList, Webcam activeWebcam, ImageView webcamDisplay, Text FPSTray) {
        Objects.requireNonNull(webcamList);
        Objects.requireNonNull(activeWebcam);
        Objects.requireNonNull(webcamDisplay);
        this.webcamList = webcamList;
        this.activeWebcam = activeWebcam;
        this.webcamDisplay = webcamDisplay;
        this.FPSTray = FPSTray;
    }

    public void startShowingFrame() {
        if (this.isAlive()) {
            throw new IllegalStateException(this.getName() + " already started");
        }
        webcamDisplay.imageProperty().bind(imageProperty);
        this.initFrameShowThread();
        if (!this.isAlive()) {
            throw new IllegalThreadStateException("Failed to start " + this.getName() + ".");
        }
    }

    private void initFrameShowThread() {
        this.setName("Webcam Frame-Showing Thread");
        this.setDaemon(true);
        this.setPriority(MAX_PRIORITY);
        this.start();
    }

    @Override
    public void run() {
        System.out.println(this.getName() + " started.");
        webcamList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldWebcam, newWebcam) -> {
            activeWebcam = newWebcam;
            if (!activeWebcam.isOpen()) {
                WebcamUtils.startUpWebcam(activeWebcam, null);
            }
        });
        this.initFPSTrayThread();
        while (!interrupted()) {
            try {
                imageProperty.set(SwingFXUtils.toFXImage(activeWebcam.getImage(), null));
            } catch (Exception e) {
                System.out.println("Skipped frame: " + e.getMessage());
                break;
            }
        }
        System.out.println(this.getName() + " terminated.");
    }

    private void initFPSTrayThread() {
        this.runFPSTrayThread();
        FPSTrayThread.setName("FPSTray Thread");
        FPSTrayThread.setDaemon(true);
        FPSTrayThread.start();
    }

    private void runFPSTrayThread() {
        FPSTrayThread = new Thread(() -> {
            System.out.println("FPSTray started.");
            long start = Instant.now().toEpochMilli();
            while (!interrupted()) {
                if (Instant.now().toEpochMilli() - start >= 1000) {
                    FPSTray.setText("FPS: " + (int) activeWebcam.getFPS());
                    start = Instant.now().toEpochMilli();
                }
            }
            System.out.println("FPSTray terminated.");
        });
    }

    public void stopShowingFrame() throws InterruptedException {
        if (FPSTrayThread.isAlive()) {
            FPSTrayThread.interrupt();
            FPSTrayThread.join();
            if (FPSTrayThread.isAlive()) {
                throw new IllegalThreadStateException("Failed to stop " + FPSTrayThread.getName() + ".");
            }
        } else {
            throw new IllegalThreadStateException(FPSTrayThread.getName() + " already stopped.");
        }
        if (this.isAlive()) {
            this.interrupt();
            this.join();
            if (this.isAlive()) {
                throw new IllegalThreadStateException("Failed to stop " + this.getName() + ".");
            }
        } else {
            throw new IllegalThreadStateException(this.getName() + " already stopped.");
        }
    }

    public ChoiceBox<Webcam> getWebcamList() {
        return webcamList;
    }

    public Text getFPSTray() {
        return FPSTray;
    }

    public Webcam getActiveWebcam() {
        return activeWebcam;
    }

    public ImageView getWebcamDisplay() {
        return webcamDisplay;
    }
}
