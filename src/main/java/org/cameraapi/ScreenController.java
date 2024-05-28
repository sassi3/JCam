package org.cameraapi;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.HashMap;

public class ScreenController {
    private static final HashMap<String, Parent> screenMap = new HashMap<>();
    private static Scene main;

    public ScreenController(Scene main) {
        ScreenController.main = main;
    }

    protected static <T extends Parent> void addScreen(String name, T pane){
        screenMap.put(name, pane);
    }

    protected static void removeScreen(String name){
        screenMap.remove(name);
    }

    protected static void activate(String name){
        main.setRoot(screenMap.get(name));
    }
}
