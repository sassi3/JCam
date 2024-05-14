package org.cameraapi.effects;

public interface EffectInterface {
    void enable();
    void disable();
    boolean isEnabled();
    void apply();
    boolean isApplied();
}
