package org.example.cameraapi;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.bytedeco.javacv.*;
import java.util.Objects;

public class Camera {
    private AnimationTimer timer;
    private FrameGrabber grabber;
    private final JavaFXFrameConverter converter;

    public Camera(Canvas camera_canvas) {
        try {
            grabber = FrameGrabber.createDefault(0);
            System.out.println("Default webcam detected. Starting timer...");
            timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    try {
                        showWebcam(camera_canvas, grabber, converter);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            System.out.println("Timer running.");
        } catch (FrameGrabber.Exception e) {
            grabber = null;
            System.out.println("No webcam detected. Starting timer...");
            timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    try {
                        printImg(camera_canvas,new Image(Objects.requireNonNull(getClass().getResourceAsStream("Icons/ErrImg.png"))));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            System.out.println("Timer running.");
        } finally {
            converter = new JavaFXFrameConverter();
            // Routine in background for webcam research. It runs even in case there is a default webcam
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

    public AnimationTimer getTimer() {
        return timer;
    }

    public void start() throws FrameGrabber.Exception {
        grabber.start();
    }

    public void stop() throws FrameGrabber.Exception {
        grabber.stop();
    }

    public static void showWebcam(Canvas canvas, FrameGrabber grabber, JavaFXFrameConverter converter) throws Exception {
        printFrame(canvas, grabber, converter);
    }


    private static void printFrame(Canvas canvas, FrameGrabber grabber, JavaFXFrameConverter converter) throws Exception {
        GraphicsContext g2d = canvas.getGraphicsContext2D();
        Image img = converter.convert(grabber.grab());
        g2d.drawImage(img, 0, 0);
    }

    public static void printImg(Canvas canvas, Image img)  {
        GraphicsContext g2d = canvas.getGraphicsContext2D();
        g2d.drawImage(img, 0, 0);
    }
}

