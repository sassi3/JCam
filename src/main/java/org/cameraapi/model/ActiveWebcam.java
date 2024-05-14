package org.cameraapi.model;

import com.github.sarxos.webcam.Webcam;

public class ActiveWebcam {
    String name;
    double rotation;
    Webcam activeWebcam;

    public ActiveWebcam(Webcam activeWebcam) {
        this.activeWebcam = activeWebcam;
        this.name = activeWebcam.getName();
        rotation = 180;
    }

    public ActiveWebcam(String name, Webcam activeWebcam) {
        this.name = name;
        this.activeWebcam = activeWebcam;
        rotation = 180;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public Webcam getActiveWebcam() {
        return activeWebcam;
    }

    public void setActiveWebcam(Webcam activeWebcam) {
        this.activeWebcam = activeWebcam;
    }
}