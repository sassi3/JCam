module org.example.cameraapi {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.bytedeco.javacv;
    requires org.bytedeco.opencv;
    requires java.desktop;
    requires webcam.capture;


    opens org.example.cameraapi to javafx.fxml;
    exports org.example.cameraapi;
    exports org.example.cameraapi.controller;
    opens org.example.cameraapi.controller to javafx.fxml;
    exports org.example.cameraapi.model;
    opens org.example.cameraapi.model to javafx.fxml;
}