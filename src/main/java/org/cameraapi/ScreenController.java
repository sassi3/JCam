package org.cameraapi;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.HashMap;

public class ScreenController {
    private static HashMap<String, Parent> screenMap;
    private static Scene main;

    public static void setScreenController(Scene main) {
        screenMap = new HashMap<>();
        ScreenController.main = main;
    }

    public static <T extends Parent> void addScreen(String name, T pane){
        screenMap.put(name, pane);
    }

    public static void removeScreen(String name){
        screenMap.remove(name);
    }

    public static void activate(String name){
        Parent newPane = screenMap.get(name);
        main.setRoot(newPane);
    }

    public static void slideFromRight(String name) {
        Parent newParent = screenMap.get(name);
        Parent oldParent = main.getRoot();
        double sceneWidth = oldParent.getScene().getWidth();
        newParent.translateXProperty().set(sceneWidth);
        activate(name);

        Timeline newParentTimeline = new Timeline();
        KeyValue kv = new KeyValue(newParent.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(0.4), kv);
        newParentTimeline.getKeyFrames().add(kf);
        newParentTimeline.play();
    }

    public static void slideFromLeft(String name) {
        Parent newParent = screenMap.get(name);
        Parent oldParent = main.getRoot();
        double sceneWidth = oldParent.getScene().getWidth();
        newParent.translateXProperty().set(-sceneWidth);
        activate(name);

        Timeline newParentTimeline = new Timeline();
        KeyValue kv = new KeyValue(newParent.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(0.4), kv);
        newParentTimeline.getKeyFrames().add(kf);
        newParentTimeline.play();
    }
}
