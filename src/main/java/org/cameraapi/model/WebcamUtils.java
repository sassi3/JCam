package org.cameraapi.model;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import java.awt.*;
import java.util.Objects;

public class WebcamUtils {
    private static final Dimension[] nonStandardResolutions = new Dimension[] {
            WebcamResolution.QQVGA.getSize(),
            WebcamResolution.HQVGA.getSize(),
            WebcamResolution.QVGA.getSize(),
            WebcamResolution.WQVGA.getSize(),
            WebcamResolution.HVGA.getSize(),
            WebcamResolution.VGA.getSize(),
            WebcamResolution.WVGA.getSize(),
            WebcamResolution.FWVGA.getSize(),
            WebcamResolution.SVGA.getSize(),
            WebcamResolution.DVGA.getSize(),
            WebcamResolution.WSVGA1.getSize(),
            WebcamResolution.WSVGA2.getSize(),
            WebcamResolution.XGA.getSize(),
            WebcamResolution.XGAP.getSize(),
            WebcamResolution.WXGA1.getSize(),
            WebcamResolution.WXGA2.getSize(),
            WebcamResolution.WXGAP.getSize(),
            WebcamResolution.SXGA.getSize(),
            WebcamResolution.SXGAP.getSize(),
            WebcamResolution.WSXGAP.getSize(),
            WebcamResolution.HD.getSize(),
            WebcamResolution.FHD.getSize(),
            WebcamResolution.UXGA.getSize(),
            WebcamResolution.WUXGA.getSize(),
            WebcamResolution.QHD.getSize(),
            WebcamResolution.UHD4K.getSize()
    };
    private static final Dimension defaultResolution = nonStandardResolutions[0];

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
        Dimension[] dimensionsSupported = webcam.getDevice().getResolutions();
        Dimension maxDimension = dimensionsSupported[dimensionsSupported.length - 1];
        if (Objects.isNull(resolution)) {
            resolution = webcam.getViewSize();
        }
        changeResolution(webcam, resolution);
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