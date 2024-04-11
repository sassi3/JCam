package org.example.cameraapi;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;

public class Camera {
    public static void run() throws Exception {
        FrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();
        Frame frame = grabber.grab();

        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        IplImage img = converter.convert(frame);
        opencv_imgcodecs.cvSaveImage("selfie.jpg", img);

        CanvasFrame canvas = new CanvasFrame("Web Cam");
        canvas.showImage(frame);
    }

    public static void main(String[] args) throws Exception {
        Camera.run();
    }
}

