package org.cameraapi.effects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.cameraapi.HomeController;

public class Freeze extends LiveEffect {

    public Freeze(boolean enabled, boolean applied) {
        super(enabled, applied);
    }
    @Override
    public void applyEffect(ImageView imageView){
        apply();
        System.out.println("freeze: " + isApplied());
    }

    // ---------------- FREEZE ----------------
    public static void freeze( ImageView imageView,Image image) {
        imageView.setImage(image);
    }



    // Unused mat2Image converter, but maybe useful for
    /* private static Image mat2Image(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    } */
}
