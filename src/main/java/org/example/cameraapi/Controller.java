package org.example.cameraapi;

import java.io.ByteArrayInputStream;

import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.JavaFXFrameConverter;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import javafx.fxml.FXML;

public class Controller  {
    @FXML private Canvas camera_canvas;
    private final Camera camera;
    
    public Controller() throws Exception {
        camera = new Camera();
        camera.start();
        initializeTimer();
    }

    public void initializeTimer() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    Camera.showWebcam(camera_canvas, camera.getGrabber(), camera.getConverter());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        timer.start();
    }

    // Unused mat2Image converter, but maybe useful
    /* public static Image mat2Image(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    } */
}