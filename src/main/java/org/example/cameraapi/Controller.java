package org.example.cameraapi;

import java.util.Objects;

import com.github.sarxos.webcam.Webcam;
import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import org.bytedeco.javacv.FrameGrabber;
import javafx.fxml.FXML;

public class Controller  {
    @FXML private Canvas camera_canvas;
    private final Camera camera;
    private AnimationTimer timer;

    // By default, the camera preview is shown on program startup
    public Controller() throws FrameGrabber.Exception {
        Webcam webcam = Webcam.getDefault();
        camera = new Camera();
        if (webcam != null) {
            camera.grabber = FrameGrabber.createDefault(0);
            camera.start();
            initializeTimer();
        }
        else{
            System.out.println("No webcam found");
            showErrorScreen();
        }

    }

    // Useful method to stop the camera when the user changes page (for example, opening settings)
    @FXML
    public void webcamStop() throws FrameGrabber.Exception {
        camera.stop();
        timer.stop();
    }

    @FXML
    public void webcamRestart() throws FrameGrabber.Exception {
        camera.start();
        timer.start();
    }

    public void initializeTimer() {
        timer = new AnimationTimer() {
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

    public void showErrorScreen(){
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Camera.printImg(camera_canvas,new Image(Objects.requireNonNull(getClass().getResourceAsStream("Icons/ErrImg.png"))));
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