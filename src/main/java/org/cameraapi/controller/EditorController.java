package org.cameraapi.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class EditorController {
    final Image APPLICATION_ICON = new Image("src/main/resources/org/cameraapi/controller/icons/editorIcon.png");

    // --------- IMAGES' CONTAINERS ---------
    @FXML private ImageView picturePreview;
    private Image picture;


    @FXML
    public void initialize() {
        setPicturePreview(picture);
    }

    //---------- GETTERS AND SETTERS ----------
    public ImageView getPicturePreview() {
        return picturePreview;
    }

    public Image getPicture() {
        return picture;
    }

    public void setPicture(Image picture) {
        this.picture = picture;
    }

    public void setPicturePreview(Image picture) {
        picturePreview.setImage(picture);
    }


    public <T> void addDialogIconTo(Dialog<T> dialog) {
        // Add custom Image to Dialog's title bar
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(APPLICATION_ICON);
    }
}
