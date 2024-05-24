package org.cameraapi.common;

import com.github.sarxos.webcam.WebcamMotionDetector;
import javafx.scene.control.RadioButton;

public class StabilizerThread extends Thread {
    WebcamMotionDetector motionDetector;
    RadioButton tray;
    boolean status;

    public StabilizerThread(WebcamMotionDetector motionDetector, RadioButton tray) {
        this.motionDetector = motionDetector;
        this.tray = tray;
        status = false;
    }

    @Override
    public void run() {
        System.out.println(getName() + " started.");
        while (!interrupted()) {
            if (motionDetector.isMotion()) {
                status = motionDetector.isMotion();

            }
        }
    }
}
