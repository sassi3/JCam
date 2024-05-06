package org.cameraapi.model;

import javafx.animation.AnimationTimer;
import org.bytedeco.javacv.*;
import org.cameraapi.common.AlertWindows;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Thread.*;

public class Camera implements AutoCloseable {
    private final List<FrameGrabber> grabber;
    private final JavaFXFrameConverter converter;
    Thread cameraDetector;

    public Camera() {
        grabber = new ArrayList<>();
        try {
            System.out.println("Default webcam detected.");
            grabber.addFirst(FrameGrabber.createDefault(0));
            grabber.getFirst().start();
        } catch (Exception e) {
            System.out.println("No webcam detected.");
        } finally {
            converter = new JavaFXFrameConverter();
            System.out.println("Starting background routine for webcam detection...");

            // Background routine for webcam detection. It runs even in case there is a default webcam
            Thread cameraDetector = getDetector();
            cameraDetector.setPriority(Thread.MIN_PRIORITY);
            cameraDetector.start();
        }
    }

    @Override
    public void close() {
        cameraDetector.interrupt();
    }

    private Thread getDetector() {
        cameraDetector = new Thread(() -> {
            for (int i = grabber.size(); !interrupted(); i++) {
                try {
                    grabber.addFirst(FrameGrabber.createDefault(i));
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!grabber.isEmpty()) {
                        System.out.println("Number of webcams detected: " + grabber.size());
                        i = grabber.size();
                    }
                }
            }
        });
        return cameraDetector;
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