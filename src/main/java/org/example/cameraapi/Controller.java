package org.example.cameraapi;

import java.util.Objects;
import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Affine;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import javafx.fxml.FXML;
import org.bytedeco.javacv.JavaFXFrameConverter;

public class Controller  {
    private AnimationTimer timer;
    private final Camera camera;
    @FXML private Canvas cameraCanvas;
    @FXML private Image rawPicture;        // I think that it is a good practice to keep a copy of original data
    @FXML private Image currentPicture;
    @FXML private ImageView outputPicture;
    @FXML private WritableImage pictureToWrite;
    @FXML private Button captureButton;
    private boolean outputChecker = true; // assures that the transform gets applied on output_picture only once
    // By default, the camera preview is shown on program startup
    public Controller() throws FrameGrabber.Exception {
        camera = new Camera();
        initializeTimer();
    }

    public void disableInterface(){
        captureButton.disarm();
    }

    // --------------- FXML ---------------
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
        output_picture.setImage(raw_picture);
        output_picture.setPreserveRatio(true);
    }

    @FXML
    private void takePicture() throws FrameGrabber.Exception {
        if (outputChecker) {
            outputChecker = false;
            output_picture.getTransforms().add(new Affine(-1,0,output_picture.getFitWidth(),0,1,0));
            // flips what's displayed by the image view around the y axis
            // and then translates it right (through the x axis) by the width of the image view itself
        }
        Frame snap = camera.getGrabber().grab();
        rawPicture = camera.getConverter().convert(snap);
        this.picturePreview();
    }

    // ----------- CANVAS PRINTERS -----------
    private void printFrame(Canvas canvas, FrameGrabber grabber, JavaFXFrameConverter converter) throws Exception {
        GraphicsContext graphics2D = canvas.getGraphicsContext2D();
        Image img = converter.convert(grabber.grab());
        graphics2D.drawImage(img, 0, 0);
    }

    private void printImg(Canvas canvas, Image img)  {
        GraphicsContext graphics2D = canvas.getGraphicsContext2D();
        graphics2D.drawImage(img, 0, 0);
    }

    // -------------- TIMER --------------
    private void initializeTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    if (Objects.isNull(camera.getGrabber())) {
                        disableInterface();
                        printImg(cameraCanvas, new Image(Objects.requireNonNull(getClass().getResourceAsStream("Icons/ErrImg.png"))));
                    } else {
                        printFrame(cameraCanvas, camera.getGrabber(), camera.getConverter());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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