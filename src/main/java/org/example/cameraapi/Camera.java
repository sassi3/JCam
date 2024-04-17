package org.example.cameraapi;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Affine;
import javafx.scene.transform.MatrixType;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Arrays;


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
        imgFlipper(g2d);
        g2d.drawImage(img, 0, 0, canvas.getWidth(), canvas.getHeight());
    }
    public static void printImg(Canvas canvas, Image img)  {
        GraphicsContext g2d = canvas.getGraphicsContext2D();
        g2d.drawImage(img, 0, 0, canvas.getWidth(), canvas.getHeight());
    }


    public static Affine flipperMaker(Canvas canvas){
        return new Affine(-1,0,canvas.getWidth(),0,1,0);
    }


    public static void imgFlipper(GraphicsContext g2d) {
        g2d.setTransform(flipperMaker(g2d.getCanvas()));
    }


}

