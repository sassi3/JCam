package org.cameraapi;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;

public class EditorController {
    @FXML Canvas imagePreview;
    @FXML Button saveButton;

    private Image capture;
    private AnimationTimer timer;
    private boolean flipped;


    @FXML
    public void initialize(){
        initializeCanvas(imagePreview);

    }

    @FXML
    public void onSaveButtonClicked(){

    }

    private void initializeCanvas(Canvas canvas){
        timer = new AnimationTimer() {

            @Override
            public void handle(long l) {
                canvas.setHeight(capture.getHeight());
                canvas.setWidth(capture.getWidth());
                canvas.getGraphicsContext2D().drawImage(capture, 0, 0);
            }
        };
        timer.start();

        canvas.getGraphicsContext2D().drawImage(capture, 0, 0);
    }

    public void setCapture(Image capture) {
        this.capture = capture;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    private void initializeLiveEffects(boolean flipped) {
        if (flipped) {
            //Do stuff...
        }
    }
