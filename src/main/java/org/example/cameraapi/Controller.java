package org.example.cameraapi;

import java.util.Objects;
import com.github.sarxos.webcam.Webcam;
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
        Webcam webcam = Webcam.getDefault();
        camera = new Camera();
        if (webcam != null) {
            System.out.println("webcam found: " + webcam.getName());
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
    private void webcamStop() throws FrameGrabber.Exception {
        camera.stop();
        timer.stop();
    }

    @FXML
    private void webcamRestart() throws FrameGrabber.Exception {
        camera.start();
        timer.start();
    }

    @FXML
    private void picturePreview() {
        output_picture = new ImageView(raw_picture);
        output_picture.setPreserveRatio(true);
    }

    @FXML
    private void takePicture() throws FrameGrabber.Exception {
        Frame snap = camera.getGrabber().grab();
        raw_picture = camera.getConverter().convert(snap);
        this.picturePreview();
    }

    private void initializeTimer() {
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
    /* private static Image mat2Image(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    } */
}