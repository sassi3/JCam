package org.cameraapi;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SaveDialogController {

    @FXML
    ImageView preview;

    public void save(){

    }

    public void initPreview(Image image){
        preview.setImage(image);
    }

}
