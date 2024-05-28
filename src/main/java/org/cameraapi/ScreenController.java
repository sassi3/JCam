package org.cameraapi;

import javafx.scene.Parent;
import javafx.scene.Scene;

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
        main.setRoot(screenMap.get(name));
    }
}
