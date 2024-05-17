package org.cameraapi.effects;

public abstract class LiveEffect implements LIveEffectsInterface {
    // ---- Status variables---
    private boolean enabled;
    private boolean applied;
    // ---- Identification variables -----
    public static final int FLIP = 0;
    public static final int FREEZE = 1;



    
    public LiveEffect(boolean enabled, boolean applied) {
        this.enabled = enabled;
        this.applied = applied;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    public void apply() {
        applied = !applied;
    }

    public boolean isApplied() {
        return applied;
    }
}
