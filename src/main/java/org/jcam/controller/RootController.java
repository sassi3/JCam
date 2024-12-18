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
        goForward();
        scene.setRoot(nextRoot);
    }

    public static void goBack() {
        Parent backRoot = rootStack.pop();
        scene.setRoot(backRoot);
    }

    public static void goForward() {
        Parent oldRoot = scene.getRoot();
        rootStack.push(oldRoot);
    }
}
