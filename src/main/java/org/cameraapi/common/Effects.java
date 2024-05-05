package org.cameraapi.common;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;

public class Effects {
    private static boolean frozen = false;
    private static boolean flipped = false;

    public static boolean isFrozen() {
        return frozen;
    }

    public static boolean isFlipped() {
        return flipped;
    }

    // ---------------- FLIPPER ----------------
    public static void imgFlipper(GraphicsContext graphicsContext2D) {
        graphicsContext2D.setTransform(flipperMaker(graphicsContext2D.getCanvas().getWidth()));
    }
    private static Affine flipperMaker(double canvasWidth){
        return new Affine(-1, 0, canvasWidth, 0, 1, 0);
        /* Affine represents a 2x3 matrix where the 3 first numbers represent the first row and the
         * other represent the second one.
         * The first two columns form the matrix relative to the linear transformation applied, while the last
         * indicates the translation through the x,y-axis
         *
         * In this case what's displayed in the canvas will be flipped around the y-axis and then moved right
         * (or through the x-axis) by the canvas' width.
         *
         * This last step is necessary as the result of the image being flipped will be that the image
         * has been moved at the left of the y-axis, which corresponds to the left border of the canvas,
         * displaying nothing at the end.
         *
         * Example for even more clarification:
         * visualize the image as a series of points in a plane, if I apply the transformation represented by
         * the 2x2 matrix {-1,0,0,1} to the point (1,0) I will get as result the point (-1,0). The latter's x
         * being negative implies it is placed at the left of the y-axis, thus not in the range of what's showed
         * by the canvas. We can fix that by moving the whole image to the right by its width.
         */
    }
    //Identity matrix
    private static Affine unflipperMaker(){
        return new Affine(1, 0, 0, 0, 1, 0);
    }
    public static void imgUnflipper(GraphicsContext graphicsContext2D) {
        graphicsContext2D.setTransform(unflipperMaker());
    }

    public static void flip() {
        flipped = !flipped;
        System.out.println("flip: " + flipped);
    }

    // ---------------- FREEZE ----------------
    public static void freeze(AnimationTimer timer) {
        if (frozen) timer.start(); else timer.stop();
        frozen = !frozen;
        System.out.println("freeze: " + frozen);
    }

    // Unused mat2Image converter, but maybe useful for
    /* private static Image mat2Image(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    } */
}
