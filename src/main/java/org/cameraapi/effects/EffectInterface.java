package org.cameraapi.effects;

public interface EffectInterface {
    void enable();
    boolean isEnabled();
    void disable();
    boolean isDisabled();
    void apply();
    boolean isApplied();
}
