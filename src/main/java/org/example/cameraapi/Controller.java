package org.example.cameraapi;

import java.util.Objects;
import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import javafx.fxml.FXML;

public class Controller  {
    private final Camera camera;
    @FXML private Canvas camera_canvas;
    @FXML private Image raw_picture;        // I think that it is a good practice to keep a copy of original data
    @FXML private Image current_picture;
    @FXML private ImageView output_picture;
    @FXML private WritableImage picture_to_write;
    @FXML private Button captureButton;

    // By default, the camera preview is shown on program startup
    public Controller() throws FrameGrabber.Exception {
        camera = new Camera(camera_canvas);
        if (Objects.isNull(camera.getGrabber())) {
            disableInterface();
        }
    }

    public void disableInterface(){
        captureButton.disarm();
    }

    // Useful method to stop the camera when the user changes page (for example, opening settings)
    @FXML
    private void webcamStop() throws FrameGrabber.Exception {
        camera.stop();
        camera.getTimer().stop();
    }

    @FXML
    private void webcamRestart() throws FrameGrabber.Exception {
        camera.start();
        camera.getTimer().start();
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

    // Unused mat2Image converter, but maybe useful
    /* private static Image mat2Image(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    } */
}