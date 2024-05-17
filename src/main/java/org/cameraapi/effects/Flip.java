package org.cameraapi.effects;

import javafx.geometry.Point3D;
import javafx.scene.image.ImageView;

public class Flip extends LiveEffect {
    private static double rotationValue = 0;

    public Flip(boolean enabled, boolean applied) {
        super(enabled, applied);
    }

    @Override
    public void applyEffect(ImageView picture){
        flip(picture);
    }


    public void flip(ImageView picture) {
        if (getRotationValue() == 180) {
            setRotationValue(0);
        } else {
            setRotationValue(180);
        }
        viewportFlipper(picture);
        this.apply();
        System.out.println("flip: " + this.isApplied());
    }

    public static void viewportFlipper(ImageView picture) {
        picture.setRotationAxis(new Point3D(0, 1, 0));
        picture.setRotate(rotationValue);
    }

    public static double getRotationValue() {
        return rotationValue;
    }

    public static void setRotationValue(double rotationValue) {
        Flip.rotationValue = rotationValue;
    }
// I don't want to delete this for now, it can be useful
//    public static void imgFlipper(GraphicsContext graphicsContext2D) {
//        graphicsContext2D.setTransform(flipperMaker(graphicsContext2D.getCanvas().getWidth()));
//    }
//    private static Affine flipperMaker(double viewportWidth){
//        return new Affine(-1, 0, viewportWidth, 0, 1, 0);
//    }
//    // Identity matrix
//    private static Affine unflipperMaker(){
//        return new Affine(1, 0, 0, 0, 1, 0);
//    }
//    public static void imgUnflipper(GraphicsContext graphicsContext2D) {
//        graphicsContext2D.setTransform(unflipperMaker());
//    }
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
