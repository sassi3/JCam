package org.example.cameraapi;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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




    public void start() throws FrameGrabber.Exception {
        grabber.start();
    }

    public void stop() throws FrameGrabber.Exception {
        grabber.stop();
    }

    public void showWebcam(Canvas canvas, FrameGrabber grabber, JavaFXFrameConverter converter) throws Exception {
        printFrame(canvas, grabber, converter);
    }


    private void printFrame(Canvas canvas, FrameGrabber grabber, JavaFXFrameConverter converter) throws Exception {
        GraphicsContext g2d = canvas.getGraphicsContext2D();
        Image img = converter.convert(grabber.grab());
        g2d.drawImage(img, 0, 0);
    }

    public void printImg(Canvas canvas, Image img)  {
        GraphicsContext g2d = canvas.getGraphicsContext2D();
        g2d.drawImage(img, 0, 0);
    }
}

