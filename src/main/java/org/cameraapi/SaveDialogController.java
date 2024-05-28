package org.cameraapi;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Target;
import java.nio.file.Path;

public class SaveDialogController {

    @FXML
    TextField selectDirTxtField;
    @FXML
    TextField fileNameTxtField;
    @FXML
    ImageView preview;

    private String fileName;
    private String dir;
    private Image imageToSave;

    public void initialize() {
        fileNameTxtField.setEditable(true);
        fileNameTxtField.textProperty().addListener((observable, oldValue, newValue) -> {
           fileName = newValue;
        });
        selectDirTxtField.textProperty().addListener((observable, oldValue, newValue) -> {
            dir = newValue;
        });
    }

    public void save(){
        File file = new File(selectDirTxtField.getText() + fileNameTxtField.getText());
        if(file.exists()){
            throw new RuntimeException();
            // You could launch an alert pane...
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
            // You could launch an alert pane...
        }

    }




    public void initPreview(Image image){
        preview.setImage(image);
        imageToSave = image;
    }

}
