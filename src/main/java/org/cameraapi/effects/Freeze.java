package org.cameraapi.effects;

import com.github.sarxos.webcam.Webcam;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.cameraapi.common.FrameShowThread;

import java.util.Objects;

public class Freeze extends LiveEffect {
    public Freeze() {}

    @Override
    public void toggle(ImageView imageAffected) {
        Objects.requireNonNull(imageAffected);
        setApplied(!isApplied());
        System.out.println("freeze: " + isApplied());
    }

    public static void freeze(FrameShowThread thread) {
        Objects.requireNonNull(thread);
        try {
            thread.stopShowingFrame();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static FrameShowThread unfreeze(FrameShowThread thread) {
        Objects.requireNonNull(thread);
        ChoiceBox<Webcam> choiceBox = thread.getWebcamChoiceBox();
        Webcam webcam = thread.getActiveWebcam();
        ImageView imageView = thread.getWebcamDisplay();
        Text text = thread.getFPSTray();
        RadioButton stabilityTray = thread.getStabilityTray();
        thread = new FrameShowThread(choiceBox, webcam, imageView, text, stabilityTray);
        thread.startShowingFrame();
        return thread;
    }

    // Unused mat2Image converter, but maybe useful for
    /* private static Image mat2Image(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    } */
}
