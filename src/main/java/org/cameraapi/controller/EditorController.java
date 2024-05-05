package org.cameraapi.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EditorController {
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
}
