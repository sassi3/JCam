package org.cameraapi.effects;

public class Freeze extends Effect {
    // ---------------- FREEZE ----------------
    public static void freeze() {
        apply();
        System.out.println("freeze: " + isApplied());
    }

    // Unused mat2Image converter, but maybe useful for
    /* private static Image mat2Image(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    } */
}
