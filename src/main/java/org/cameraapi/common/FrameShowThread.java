package org.cameraapi.common;

import com.github.sarxos.webcam.Webcam;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.Objects;
import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

public class FrameShowThread implements Runnable {
    private final ChoiceBox<Webcam> webcamList;
    private final Text FPSTray;

    private Webcam activeWebcam;
    private final ImageView webcamDisplay;
    private Thread frameShowThread;
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
        if (Objects.nonNull(frameShowThread)) {
            if (frameShowThread.isAlive()) {
                throw new IllegalStateException("Frame showing thread already started");
            }
        }
        if (!activeWebcam.isOpen()) {
            activeWebcam.open();
        }
        webcamDisplay.imageProperty().bind(imageProperty);
        initFrameShowThread();
        initFPSTrayThread();
        if (!frameShowThread.isAlive() || !FPSTrayThread.isAlive()) {
            throw new IllegalThreadStateException("Failed to start showing frames.");
        }
    }

    private void initFrameShowThread() {
        frameShowThread = new Thread(this);
        frameShowThread.setDaemon(true);
        frameShowThread.setName("Webcam Frame Showing-Thread");
        frameShowThread.start();
    }

    private void initFPSTrayThread() {
        runFPSTrayThread();
        FPSTrayThread.setDaemon(true);
        FPSTrayThread.setName("FPSTray Thread");
        FPSTrayThread.start();
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

    private void runFPSTrayThread() {
        FPSTrayThread = new Thread(() -> {
            if (Objects.nonNull(FPSTray)) {
                System.out.println("FPSTray is running.");
                while (!interrupted()) {
                    FPSTray.setText("FPS: " + (int) activeWebcam.getFPS());
                    try {
                        sleep(1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                System.out.println("FPSTray not running. Text area is null");
            }
        });
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
