package org.cameraapi.model;

import com.github.sarxos.webcam.Webcam;

import java.awt.*;
import java.util.Objects;

public class WebcamUtils {
    public static void openWebcam(Webcam webcam) {
        webcam.setViewSize(new Dimension(640, 480));
        if (webcam.isOpen()) {
            throw new RuntimeException("Webcam is already open.");
        }
        webcam.open();
        if (!webcam.isOpen()) {
            throw new IllegalStateException("Failed to open webcam.");
        }
    }

    public static void closeWebcam(Webcam webcam) {
        if (Objects.isNull(webcam)) {
            throw new IllegalStateException("Webcam is null.");
        }
        webcam.close();
        if (webcam.isOpen()) {
            throw new IllegalStateException("Failed to close webcam.");
        }
    }
}