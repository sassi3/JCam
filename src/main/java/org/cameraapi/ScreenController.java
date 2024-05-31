package org.cameraapi;

import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class ScreenController {
    private static Map<String, Parent> screenMap;
    private static Deque<Parent> screenStack;
    private static Scene main;

    public static void initScreenController(Scene main, String rootName) {
        screenMap = new HashMap<>();
        screenStack = new ArrayDeque<>();
        ScreenController.main = main;
        addScreen(rootName, main.getRoot());
    }

    public static Map<String, Parent> getScreenMap() {
        return screenMap;
    }

    public static <T extends Parent> void addScreen(String name, T pane){
        screenMap.put(name, pane);
    }

    public static void removeScreen(String name){
        screenMap.remove(name);
    }

    public static void activate(String name) {
        if (!screenMap.containsKey(name)) {
            throw new IllegalArgumentException("No such screen: " + name);
        }
        Parent nextPane = screenMap.get(name);
        Parent currentPane = main.getRoot();
        goForward(currentPane);
        main.setRoot(nextPane);
        System.out.println(name.toUpperCase() + " activated.");
    }

    public static void goBack() {
        Parent backPane = screenStack.pop();
        main.setRoot(backPane);
    }

    public static void goForward(Parent forwardPane) {
        screenStack.push(forwardPane);
    }

//    public static void slideFromRight(String name) {
//        Parent newParent = screenMap.get(name);
//        Parent oldParent = main.getRoot();
//        double sceneWidth = oldParent.getScene().getWidth();
//        newParent.translateXProperty().set(sceneWidth);
//        activate(name);
//
//        Timeline newParentTimeline = new Timeline();
//        KeyValue kv = new KeyValue(newParent.translateXProperty(), 0, Interpolator.EASE_IN);
//        KeyFrame kf = new KeyFrame(Duration.seconds(0.4), kv);
//        newParentTimeline.getKeyFrames().add(kf);
//        newParentTimeline.play();
//    }

//    public static void slideFromLeft(String name) {
//        Parent newParent = screenMap.get(name);
//        Parent oldParent = main.getRoot();
//        double sceneWidth = oldParent.getScene().getWidth();
//        newParent.translateXProperty().set(-sceneWidth);
//        activate(name);
//
//        Timeline newParentTimeline = new Timeline();
//        KeyValue kv = new KeyValue(newParent.translateXProperty(), 0, Interpolator.EASE_IN);
//        KeyFrame kf = new KeyFrame(Duration.seconds(0.4), kv);
//        newParentTimeline.getKeyFrames().add(kf);
//        newParentTimeline.play();
//    }
}
