module org.example.cameraapi {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.bytedeco.javacv;
    requires org.bytedeco.opencv;
    requires java.desktop;
    requires webcam.capture;


    opens org.cameraapi to javafx.fxml;
    exports org.cameraapi;
    exports org.cameraapi.model;
    opens org.cameraapi.model to javafx.fxml;
    exports org.cameraapi.common;
    opens org.cameraapi.common to javafx.fxml;
    exports org.cameraapi.effects;
    opens org.cameraapi.effects to javafx.fxml;
}