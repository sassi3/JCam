package org.cameraapi;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.bytedeco.opencv.presets.opencv_core;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Target;
import java.nio.file.Path;

public class SaveDialogController {

    @FXML
    AnchorPane root;
    @FXML
    Button selectDirButton;
    @FXML
    ChoiceBox<String> typeChoiceBox;
    @FXML
    TextField selectDirTxtField;
    @FXML
    TextField fileNameTxtField;
    @FXML
    ImageView preview;

    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private Image imageToSave;

    public void initialize() {
        fileNameTxtField.setEditable(true);
        selectDirTxtField.setEditable(false);
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

    public void onSelectDir() {
        File dir = directoryChooser.showDialog(root.getScene().getWindow());
        selectDirTxtField.setText(dir.getAbsolutePath());
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
    }



    public void initPreview(Image image){
        preview.setImage(image);
        imageToSave = image;
    }

}
