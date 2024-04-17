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
        /* Affine represents a 2x3 matrix where the 3 first numbers represent the first row and the
         * other represent the second one.
         * The first two columns form the matrix relative to the linear transformation applied, while the last
         * indicates the translation through the x,y-axis
         *
         * In this case what's displayed in the canvas will be flipped around the y-axis and then moved right
         * (or through the x-axis) by the canvas' width.
         *
         * This last step is necessary as the result of the image being flipped will be that the image
         * has been moved at the left of the y-axis, which corresponds to the left border of the canvas,
         * displaying nothing at the end.
         *
         * Example for even more clarification:
         * visualize the image as a series of points in a plane, if I apply the transformation represented by
         * the 2x2 matrix {-1,0,0,1} to the point (1,0) I will get as result the point (-1,0). The latter's x
         * being negative implies it is placed at the left of the y-axis, thus not in the range of what's showed
         * by the canvas. We can fix that by moving the whole image to the right by its width.
         */
    }


    public static void imgFlipper(GraphicsContext g2d) {
        g2d.setTransform(flipperMaker(g2d.getCanvas()));
    }


}

