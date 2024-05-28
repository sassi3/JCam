package org.cameraapi;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.bytedeco.opencv.presets.opencv_core;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Target;
import java.nio.file.Path;

public class SaveDialogController {

    @FXML
    ChoiceBox<String> typeChoiceBox;
    @FXML
    TextField selectDirTxtField;
    @FXML
    TextField fileNameTxtField;
    @FXML
    ImageView preview;

    private String fileName;
    private String dir;
    private String type;
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
        String os = System.getProperty("os.name");
        File target = getFile(os);
        try {
            target.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
            // You could launch an alert pane...
        }
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(imageToSave,null), typeChoiceBox.getSelectionModel().getSelectedItem(), target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getFile(String os) {
        File target;
        if(os.contains("Windows")){
            target = new File(selectDirTxtField.getText()+ "\\" + fileNameTxtField.getText() + "." + typeChoiceBox.getSelectionModel().getSelectedItem());
        }
        else {
            target = new File(selectDirTxtField.getText()+ "/" + fileNameTxtField.getText() + "." + typeChoiceBox.getSelectionModel().getSelectedItem());
        }

        if(target.exists() || target.isDirectory()){
            throw new RuntimeException();
            // You could launch an alert pane...
        }
        return target;
    }

    public void initTypeChoiceBox() {
        typeChoiceBox.getItems().addAll("png","jpg");
        typeChoiceBox.getSelectionModel().selectFirst();
        typeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            type = newValue;
        });
    }



    public void initPreview(Image image){
        preview.setImage(image);
        imageToSave = image;
    }

}
