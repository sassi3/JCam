package org.cameraapi.effects;


import javafx.scene.image.ImageView;

public interface LIveEffectsInterface {
    void enable();
    void disable();
    boolean isDisabled();
    boolean isEnabled();
    void apply();
    boolean isApplied();
    void applyEffect(ImageView imageAffected);
    void resetStatus();
}
