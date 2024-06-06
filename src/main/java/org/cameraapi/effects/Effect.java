package org.cameraapi.effects;

public interface Effect {
    void enable();
    void disable();
    boolean isDisabled();
    boolean isEnabled();
    boolean isApplied();
    void resetStatus();
}
