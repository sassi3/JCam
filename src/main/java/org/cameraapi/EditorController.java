package org.cameraapi;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

import java.io.IOException;


public class EditorController {
    @FXML
    Canvas imagePreview;
    @FXML
    Button saveButton;
    @FXML
    Button returnButton;
    @FXML
    AnchorPane anchorPane;
    @FXML
    AnchorPane bottomPane;

    private AnimationTimer timer;
    double dx,dy;

    @FXML
    public void initialize() {
    }

    public void initCanvas(Image capture) {
        dx = capture.getWidth() - imagePreview.getWidth();
        dy = capture.getHeight() - imagePreview.getHeight();
        timer = new AnimationTimer() {

            @Override
            public void handle(long l) {
                imagePreview.setHeight(capture.getHeight());
                imagePreview.setWidth(capture.getWidth());
                imagePreview.getGraphicsContext2D().drawImage(capture, 0, 0);
            }
        };
        timer.start();

        //imagePreview.getGraphicsContext2D().drawImage(capture, 0, 0);
    }

    public void initLiveEffects(boolean flipped) {
        imagePreview.setRotationAxis(new Point3D(0, 1, 0));
        if (flipped) {
            imagePreview.setRotate(0);
        } else {
            imagePreview.setRotate(180);
        }
    }


    @FXML
    public void onReturnButtonClicked() {
        timer.stop();
        try {
            handleHomePage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleHomePage() throws IOException {
        ScreenController.slideFromLeft(anchorPane, "home");
    }

    private void handleSave() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("save-dialog.fxml"));

        DialogPane saveDialog = loader.load();
        SaveDialogController controller = loader.getController();
        controller.initPreview(imagePreview.snapshot(null, null));
        controller.initTypeChoiceBox();
        // snapshot method renders the current node into a WritableImage object
        // (Writable Image extends Image).

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Save Image");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setDialogPane(saveDialog);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            controller.save();
            dialog.close();
            event.consume();
        });
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    public void resize(){
        if(saveButton.getLayoutX() > anchorPane.getScene().getWindow().getWidth()){
            anchorPane.getScene().getWindow().setWidth(anchorPane.getPrefWidth() + dx);
        }
        if(anchorPane.getPrefHeight() - bottomPane.getHeight() + saveButton.getLayoutY() > anchorPane.getScene().getWindow().getHeight()){
            anchorPane.getScene().getWindow().setHeight(anchorPane.getPrefHeight() + dy);
        }
    }

    @FXML
    public void onSaveButtonClicked() {
        try {
            handleSave();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
