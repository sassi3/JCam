package org.example.cameraapi;

import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import javafx.fxml.FXML;

public class Controller  {
    private final Camera camera;
    private AnimationTimer timer;
    @FXML private Canvas camera_canvas;
    @FXML private Image raw_picture;        // I think that it is a good practice to keep a copy of original data
    @FXML private Image current_picture;
    @FXML private ImageView output_picture;
    @FXML private WritableImage picture_to_write;

    // By default, the camera preview is shown on program startup
    public Controller() throws FrameGrabber.Exception {
        camera = new Camera();
        camera.start();
        initializeTimer();
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

    @FXML
    public void takePicture() throws FrameGrabber.Exception {
        Frame snap = camera.getGrabber().grab();
        // TEMPORARY: only to debug
        raw_picture = camera.getConverter().convert(snap);
        // continue...
    }

    public void initializeTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    Camera.webcamStreaming(camera_canvas, camera.getGrabber(), camera.getConverter());
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