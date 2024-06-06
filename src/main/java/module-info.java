module org.example.cameraapi {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.bytedeco.javacv;
    requires org.bytedeco.opencv;
    requires java.desktop;
    requires webcam.capture;
    requires javafx.swing;
    requires javafx.base;
    requires atlantafx.base;
    requires static lombok;


    opens org.jcam to javafx.fxml;
    exports org.jcam;
    exports org.jcam.common;
    opens org.jcam.common to javafx.fxml;
    exports org.jcam.effects;
    opens org.jcam.effects to javafx.fxml;
    exports org.jcam.controller;
    opens org.jcam.controller to javafx.fxml;
}