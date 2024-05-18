package org.cameraapi.effects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Freeze extends LiveEffect {
    public Freeze() {}

    @Override
    public void apply(ImageView imageAffected) {
        System.out.println("freeze: " + isApplied());
    }

    public static void freeze(ImageView imageView, Image image) {
        imageView.setImage(image);
    }

    // Unused mat2Image converter, but maybe useful for
    /* private static Image mat2Image(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    } */
}
