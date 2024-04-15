package org.example.cameraapi;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.bytedeco.javacv.*;

public class Camera {
    FrameGrabber grabber;
    private final JavaFXFrameConverter converter;

    public Camera() {

        converter = new JavaFXFrameConverter();
    }

    public JavaFXFrameConverter getConverter() {
        return converter;
    }

    public FrameGrabber getGrabber() {
        return grabber;
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
    public static void printFrame(Canvas canvas, Image img)  {
        GraphicsContext g2d = canvas.getGraphicsContext2D();
        g2d.drawImage(img, 0, 0);
    }
}

