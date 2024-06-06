package org.cameraapi.effects;

import javafx.scene.image.ImageView;

public interface Effect {
    void enable();
    void disable();
    boolean isDisabled();
    boolean isEnabled();
    boolean isApplied();
    void resetStatus();
}
