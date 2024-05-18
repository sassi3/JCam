package org.cameraapi.effects;


import javafx.scene.image.ImageView;

public interface LIveEffectsInterface {
    void enable();
    void disable();
    boolean isDisabled();
    boolean isEnabled();
    void toggle(ImageView imageAffected);
    boolean isApplied();
    void resetStatus();
}
