package org.cameraapi;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SaveDialogController {
    @FXML
    StackPane root;
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
        if (selectDirTxtField.getText().isEmpty()) {
            throw new RuntimeException("No directory selected");
        }
        if (fileNameTxtField.getText().isEmpty()) {
            throw new RuntimeException("No file selected");
        }
        File target = getFile(os);
        try {
            target.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
            // You could launch an alert pane...
        }
        try {
            // Necessary step to work with jpg and jpeg.
            // You can find the explanation for why it is this way at this link:
            // https://stackoverflow.com/a/57674578
            BufferedImage awtImage = new BufferedImage((int)imageToSave.getWidth(), (int)imageToSave.getHeight(), BufferedImage.TYPE_INT_RGB);
            SwingFXUtils.fromFXImage(imageToSave, awtImage);
            ImageIO.write(awtImage, typeChoiceBox.getSelectionModel().getSelectedItem(), target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onSelectDir() {
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File dir = directoryChooser.showDialog(root.getScene().getWindow());
        if (dir != null) {
            selectDirTxtField.setText(dir.getAbsolutePath());
        }
    }

    private File getFile(String os) {
        File target;
        if (os.contains("Windows")) {
            target = new File(selectDirTxtField.getText()+ "\\" + fileNameTxtField.getText() + typeChoiceBox.getSelectionModel().getSelectedItem());
        }
        else {
            target = new File(selectDirTxtField.getText()+ "/" + fileNameTxtField.getText() + typeChoiceBox.getSelectionModel().getSelectedItem());
        }

        if (target.exists() || target.isDirectory()) {
            throw new RuntimeException("File exists or is a directory");
            // You could launch an alert pane...
        }

        return target;
    }

    public void initTypeChoiceBox() {
        typeChoiceBox.getItems().addAll(".png", ".jpg", ".jpeg");
        typeChoiceBox.getSelectionModel().selectFirst();
    }

    public void initPreview(Image image) {
        preview.setImage(image);
        imageToSave = image;
    }
}
