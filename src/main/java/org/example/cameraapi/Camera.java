package org.example.cameraapi;
import org.bytedeco.javacv.*;


public class Camera {
    private FrameGrabber grabber;
    private final JavaFXFrameConverter converter;

    public Camera() {
        try {
            System.out.println("Default webcam detected.");
            grabber = FrameGrabber.createDefault(0);
            start();
        } catch (FrameGrabber.Exception e) {
            System.out.println("No webcam detected.");
            grabber = null;
        } finally {
            converter = new JavaFXFrameConverter();
            // Background routine for webcam detection. It runs even in case there is a default webcam
            System.out.println("Starting background routine for webcam detection...");
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
    public void start() throws FrameGrabber.Exception {
        grabber.start();
    }

    public void stop() throws FrameGrabber.Exception {
        grabber.stop();
    }
}

