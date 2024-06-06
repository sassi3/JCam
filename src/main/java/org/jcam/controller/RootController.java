package org.jcam.controller;

import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class RootController {
    @Getter
    private static Map<String, Parent> rootMap;
    private static Deque<Parent> rootStack;
    private static Scene scene;

    public static void initRootController(@NonNull Scene main, @NonNull String rootName) {
        rootMap = new HashMap<>();
        rootStack = new ArrayDeque<>();
        RootController.scene = main;
        addRoot(rootName, main.getRoot());
    }

    public static <T extends Parent> void addRoot(@NonNull String name, @NonNull T pane){
        rootMap.put(name, pane);
    }

    public static void removeRoot(String name){
        rootMap.remove(name);
    }

    public static void changeRoot(@NonNull String name) {
        if (!rootMap.containsKey(name)) {
            throw new IllegalArgumentException("No such root: " + name);
        }
        Parent nextRoot = rootMap.get(name);
        Parent currentRoot = scene.getRoot();
        goForward(currentRoot);
        scene.setRoot(nextRoot);
        System.out.println(name.toUpperCase() + " activated.");
    }

    public static void goBack() {
        Parent backRoot = rootStack.pop();
        scene.setRoot(backRoot);
    }

    public static void goForward(@NonNull Parent forwardRoot) {
        rootStack.push(forwardRoot);
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
