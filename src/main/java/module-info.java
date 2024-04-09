module org.example.cameraapi {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.cameraapi to javafx.fxml;
    exports org.example.cameraapi;
}