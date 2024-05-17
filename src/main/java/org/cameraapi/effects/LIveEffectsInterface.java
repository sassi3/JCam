package org.cameraapi.effects;

public interface LIveEffectsInterface {
    void enable();

    void disable();

    boolean isDisabled();

    void apply();

    boolean isApplied();
}
