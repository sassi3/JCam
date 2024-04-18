package org.example.cameraapi;

import java.util.Objects;
import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Affine;
import org.bytedeco.javacv.FrameGrabber;
import javafx.fxml.FXML;
import org.bytedeco.javacv.JavaFXFrameConverter;

public class Controller  {
    private AnimationTimer timer;
    private final Camera camera;
    @FXML private Canvas cameraCanvas;
    @FXML private Image rawPicture;
    @FXML private Image currentPicture;
    @FXML private ImageView outputPicture;
    @FXML private WritableImage pictureToWrite;

    // --------- BUTTONS & CHECKBOXES ---------
    @FXML private Button captureButton;
    @FXML private CheckBox flipCheckBox;
    @FXML private CheckBox freezeCheckBox;
    private boolean flip;
    private boolean outputChecker;

    // By default, the camera preview is shown on program startup
    public Controller() {
        camera = new Camera();
        outputChecker = true;   // assures that the transform gets applied on output_picture only once
        flip = false;
        initializeTimer();
    }

    // -------------- DISARMER --------------
    public void disableInterface(){
        captureButton.disarm();
        flipCheckBox.disarm();
        freezeCheckBox.disarm();
    }

    // --------------- FXMLs ---------------
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
        outputPicture.setImage(rawPicture);
        outputPicture.setPreserveRatio(true);
    }

    @FXML
    private void takePicture() throws FrameGrabber.Exception {
        // ? Are you sure ?
        if (outputChecker) {
            outputChecker = false;
            outputPicture.getTransforms().add(new Affine(-1,0,outputPicture.getFitWidth(),0,1,0));
            // flips what's displayed by the image view around the y-axis
            // and then translates it right (through the x-axis) by the width of the image view itself
        }
        rawPicture = camera.getConverter().convert(camera.getGrabber().grab());
        picturePreview();
    }

    // --------------- EFFECTS ---------------
    @FXML
    private void flipCamera() {
        flip = !flip;
        System.out.println("flip: " + flip);
    }

    @FXML
    private void freezeCamera() {
        Effects.freeze(timer);
    }

    // --------- UNIVERSAL CANVAS PRINTERS ---------
    private void printWebcamFrame(Canvas canvas, FrameGrabber grabber, JavaFXFrameConverter converter) throws Exception {
        canvas.getGraphicsContext2D().drawImage(converter.convert(grabber.grab()), 0, 0, canvas.getWidth(), canvas.getHeight());
        if (!flip) Effects.imgFlipper(cameraCanvas.getGraphicsContext2D());
    }

    private void printImg(Canvas canvas, Image img)  {
        canvas.getGraphicsContext2D().drawImage(img, 0, 0);
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
                        printWebcamFrame(cameraCanvas, camera.getGrabber(), camera.getConverter());
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