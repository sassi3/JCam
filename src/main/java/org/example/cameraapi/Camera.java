package org.example.cameraapi;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Camera {

    public static void clickWebcamCapture() throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.open();

        BufferedImage image = webcam.getImage();

        ImageIO.write(image, ImageUtils.FORMAT_JPG, new File("selfie.jpg"));
    }

    public static void showWebcam(Canvas canvas) throws Exception {

        FrameGrabber grabber = new OpenCVFrameGrabber(0);
        JavaFXFrameConverter converter = new JavaFXFrameConverter();
        grabber.start();
        printFrame(canvas,grabber,converter);


    }

    private static void printFrame(Canvas canvas, FrameGrabber grabber, JavaFXFrameConverter converter) {
        GraphicsContext g2d = canvas.getGraphicsContext2D();

        Frame frame = null;
        try {
            frame = grabber.grab();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Image img = converter.convert(frame);
        g2d.drawImage(img, 0, 0);
    }

    public static void clickWebcamShow() {
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setImageSizeDisplayed(true);

        JFrame window = new JFrame("Webcam");
        window.add(panel);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);

    }
}
