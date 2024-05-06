package org.cameraapi.model;

import javafx.animation.AnimationTimer;
import org.bytedeco.javacv.*;
import org.cameraapi.common.AlertWindows;
import org.cameraapi.common.CameraDetector;

import java.util.ArrayList;
import java.util.List;

public class Camera implements AutoCloseable {
    private final List<FrameGrabber> grabber;
    private final JavaFXFrameConverter converter;
    Thread cameraDetector;

    public Camera() {
        grabber = new ArrayList<>();
        startWebcamDetection();
        if (grabber.isEmpty()) {
            System.out.println("No webcam detected. Waiting for webcam detection...");
            cameraDetector.setPriority(Thread.MAX_PRIORITY);
            try {
                cameraDetector.join();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                System.err.println("Camera detection interrupted.");
                System.exit(6);
            }
            System.out.println("Webcam detected. Proceeding...");
        }
        converter = new JavaFXFrameConverter();
    }

    @Override
    public void close() {
        if (cameraDetector != null) {
            cameraDetector.interrupt();
        }
    }

    public JavaFXFrameConverter getConverter() {
        return converter;
    }

    public FrameGrabber getGrabber() {
        return grabber.getLast();
    }

    public void setGrabber(FrameGrabber grabber) {
        this.grabber.addFirst(grabber);
    }

    public void startWebcamDetection() {
        System.out.println("Starting background routine for webcam detection...");
        if (cameraDetector == null) {
            cameraDetector = new CameraDetector("WebcamDetector", grabber);
            cameraDetector.setPriority(Thread.MIN_PRIORITY);
        }
        cameraDetector.start();
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