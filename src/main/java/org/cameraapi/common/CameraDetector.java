package org.cameraapi.common;

import org.bytedeco.javacv.FrameGrabber;

import java.util.List;

public class CameraDetector extends Thread {
    private final List<FrameGrabber> grabber;

    public CameraDetector(String name, List<FrameGrabber> grabber) {
        super(name);
        this.grabber = grabber;
    }

    @Override
    public void run() {
        System.out.println(getName() + " started.");
        for (int i = grabber.size(); !interrupted(); i++) {
            try {
                grabber.addFirst(FrameGrabber.createDefault(i));
                try {
                    sleep(5000L);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    System.err.println("CameraDetector interrupted.");
                    System.exit(1);
                }
            } catch (Exception e) {
                if (!grabber.isEmpty()) {
                    return;
                } else {
                    i = 0;
                }
            }
        }
        System.out.println(getName() + " finished.");
    }
}
