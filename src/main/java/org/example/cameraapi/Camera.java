package org.example.cameraapi;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Camera {

    public static void clickOpenCV() throws Exception {
        FrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();
        Frame frame = grabber.grab();

        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        IplImage img = converter.convert(frame);
        opencv_imgcodecs.cvSaveImage("pic.jpg", img);

        CanvasFrame canvas = new CanvasFrame("Camera");
        canvas.showImage(frame);
    }

    public static void clickWebcamCapture() throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.open();

        BufferedImage image = webcam.getImage();

        ImageIO.write(image, ImageUtils.FORMAT_JPG, new File("selfie.jpg"));
    }

    public static void clickWebcamUtils() throws Exception {
        Webcam webcam = Webcam.getDefault();
        webcam.open();

        WebcamUtils.capture(webcam, "selfie.jpg");
    }

    public static void clickWebcamShow() throws Exception {
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

    public static void main(String[] args) throws IOException {
        // Now this is the only working
        //Camera.clickOpenCV();
        // These are bruh and slow
         //Camera.clickWebcamCapture();
         //Camera.clickWebcamUtils();
         Camera.clickWebcamShow();
    }
}
