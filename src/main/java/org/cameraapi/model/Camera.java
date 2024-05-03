package org.cameraapi.model;

import javafx.animation.AnimationTimer;
import org.bytedeco.javacv.*;
import org.cameraapi.common.AlertWindows;

public class Camera {
    private FrameGrabber grabber;
    private final JavaFXFrameConverter converter;

    public Camera() {
        try {
            System.out.println("Default webcam detected.");
            grabber = FrameGrabber.createDefault(0);
            grabber.start();
        } catch (FrameGrabber.Exception e) {
            System.out.println("No webcam detected.");
            grabber = null;
        } finally {
            converter = new JavaFXFrameConverter();
            System.out.println("Starting background routine for webcam detection...");
            // Background routine for webcam detection. It runs even in case there is a default webcam
        }
    }

    public JavaFXFrameConverter getConverter() {
        return converter;
    }

    public FrameGrabber getGrabber() {
        return grabber;
    }

    public void setGrabber(FrameGrabber grabber) {
        this.grabber = grabber;
    }

    // -------------- START & STOP --------------
    public void start(AnimationTimer timer) {
        try {
            this.getGrabber().start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
            System.err.println("Camera.start(): Failed to restart camera.");
            System.exit(1);
        }
        timer.start();
        System.out.println("webcamRestart(): Webcam restarted.");
    }
    public void stop(AnimationTimer timer) {
        try {
            this.getGrabber().stop();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
            System.err.println("webcamStop(): Failed to stop camera.");
            timer.stop();
            AlertWindows.showFatalError();
            System.exit(1);
        }
        System.out.println("Camera.stop(): Webcam stopped.");
    }
}