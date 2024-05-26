package org.cameraapi.model;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import java.awt.*;
import java.util.Objects;

public class WebcamUtils {
    private static final Dimension[] nonStandardResolutions = new Dimension[] {
            WebcamResolution.HD.getSize(),
            WebcamResolution.FHD.getSize(),
            WebcamResolution.QHD.getSize(),
            WebcamResolution.UHD4K.getSize()
    };
    private static final Dimension defaultResolution = WebcamResolution.HD.getSize();

    private static boolean isValidResolution(Dimension resolution) {
        Objects.requireNonNull(resolution);
        for (Dimension dimension : nonStandardResolutions) {
            if (resolution.equals(dimension)) {
                return true;
            }
        }
        return false;
    }

    public static void openWebcam(Webcam webcam, Dimension resolution) {
        Objects.requireNonNull(webcam);
        if (webcam.isOpen()) {
            throw new RuntimeException("Webcam is already open.");
        }
        webcam.setCustomViewSizes(nonStandardResolutions);
        if (Objects.isNull(resolution)) {
            resolution = defaultResolution;
        }
        changeResolution(webcam, resolution);
        webcam.open();
        if (!webcam.isOpen()) {
            throw new IllegalStateException("Failed to open webcam.");
        }
    }

    public static void closeWebcam(Webcam webcam) {
        Objects.requireNonNull(webcam);
        webcam.close();
        if (webcam.isOpen()) {
            throw new IllegalStateException("Failed to close webcam.");
        }
    }

    public static void changeResolution(Webcam webcam, Dimension resolution) {
        Objects.requireNonNull(webcam);
        Objects.requireNonNull(resolution);
        if (!isValidResolution(resolution)) {
            throw new IllegalArgumentException("Resolution " + resolution + " is not supported.");
        }
        if (webcam.isOpen()) {
            closeWebcam(webcam);
        }
        webcam.setViewSize(resolution);
        webcam.open();
    }
}