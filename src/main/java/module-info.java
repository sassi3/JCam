module org.example.cameraapi {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.bytedeco.javacv;
    requires org.bytedeco.opencv;
    requires java.desktop;


    opens org.example.cameraapi to javafx.fxml;
    exports org.example.cameraapi;
}