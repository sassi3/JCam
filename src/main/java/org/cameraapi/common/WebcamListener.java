package org.cameraapi.common;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import org.cameraapi.HomeController;

public class WebcamListener implements WebcamDiscoveryListener {
    public WebcamListener() {
        for (Webcam webcam : Webcam.getWebcams()) {
            System.out.println("Webcam detected: " + webcam.getName());
            HomeController.getWebcams().addLast(webcam);
        }
        Webcam.addDiscoveryListener(this);
        System.out.println("Webcam added. Listening for events...");
    }

    @Override
    public void webcamFound(WebcamDiscoveryEvent webcamDiscoveryEvent) {
        System.out.println("Webcam connected: " + webcamDiscoveryEvent.getWebcam().getName());
        HomeController.getWebcams().addLast(webcamDiscoveryEvent.getWebcam());
    }

    @Override
    public void webcamGone(WebcamDiscoveryEvent webcamDiscoveryEvent) {
        System.out.println("Webcam disconnected: " + webcamDiscoveryEvent.getWebcam().getName());
        HomeController.getWebcams().remove(webcamDiscoveryEvent.getWebcam());
    }
}
