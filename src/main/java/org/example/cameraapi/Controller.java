package org.example.cameraapi;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.JavaFXFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import javafx.fxml.FXML;

public class Controller {
    @FXML private Canvas camera;
    AnimationTimer timer;
    FrameGrabber grabber = new OpenCVFrameGrabber(0);
    JavaFXFrameConverter converter = new JavaFXFrameConverter();


    public void initializeTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    Camera.showWebcam(camera,grabber,converter);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        timer.start();
    }

    @FXML
    public void handleStart() throws Exception {
        grabber.start();
        initializeTimer();
    }

    public static Image mat2Image(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

}