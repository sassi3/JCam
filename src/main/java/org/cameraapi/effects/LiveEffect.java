package org.cameraapi.effects;

public abstract class LiveEffect implements LIveEffectsInterface {
    private boolean enabled;
    private boolean applied;

    public LiveEffect(boolean enabled, boolean applied) {
        this.enabled = enabled;
        this.applied = applied;
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isDisabled() {
        return !enabled;
    }

    @Override
    public void apply() {
        applied = !applied;
    }

    @Override
    public boolean isApplied() {
        return applied;
    }

    @Override
    public void resetStatus(){
        applied = false;
        enabled = true;
    }

}
