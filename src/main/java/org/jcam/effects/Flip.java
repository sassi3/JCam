package org.jcam.effects;

import javafx.geometry.Point3D;
import javafx.scene.image.ImageView;
import lombok.NonNull;

public class Flip extends LiveEffect {
    private static double rotationValue;

    protected Flip() {
        super();
        setApplied(true);
        rotationValue = 0.0;
    }

    @Override
    public void apply(@NonNull ImageView imageAffected) {
        setApplied(!isApplied());
        flip(imageAffected);
    }

    public void flip(@NonNull ImageView picture) {
        if (rotationValue == 180.0) {
            rotationValue = 0.0;
        } else {
            rotationValue = 180.0;
        }
        viewportFlipper(picture);
        System.out.println("flip: " + this.isApplied());
    }

    public static void viewportFlipper(@NonNull ImageView picture) {
        picture.setRotationAxis(new Point3D(0, 1, 0));
        picture.setRotate(rotationValue);
    }
    
}
