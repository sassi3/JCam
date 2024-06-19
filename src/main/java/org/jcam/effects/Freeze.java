package org.jcam.effects;

import com.github.sarxos.webcam.Webcam;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.NonNull;
import lombok.Setter;
import org.jcam.common.FrameShowThread;

import java.beans.ConstructorProperties;
import java.util.Objects;

public class Freeze extends LiveEffect {
    protected Freeze() {
        super();
    }

    @Override
    public void apply(@NonNull ImageView imageAffected) {
        Objects.requireNonNull(imageAffected);
        setApplied(!isApplied());
        System.out.println("freeze: " + isApplied());
    }

    public static void freeze(@NonNull FrameShowThread thread) {
        try {
            thread.stopShowingFrame();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static FrameShowThread unfreeze(@NonNull FrameShowThread thread) {
        ChoiceBox<Webcam> choiceBox = thread.getWebcamChoiceBox();
        Webcam webcam = thread.getActiveWebcam();
        ImageView imageView = thread.getWebcamDisplay();
        Text text = thread.getFPSTray();
        RadioButton stabilityTray = thread.getStabilityTray();
        thread = new FrameShowThread(choiceBox, webcam, imageView, text, stabilityTray);
        thread.startShowingFrame();
        return thread;
    }
}
