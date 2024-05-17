package org.cameraapi.effects;


import javafx.scene.image.ImageView;

public interface LIveEffectsInterface {
    void enable();

    void disable();

    boolean isDisabled();

    void apply();

    boolean isApplied();

    void applyEffect(ImageView view);
}
