package org.cameraapi.effects;

public abstract class EffectAbstract implements Effect {
    private boolean enabled;
    private boolean applied;

    public EffectAbstract() {
        this.enabled = false;
        this.applied = false;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setApplied(boolean applied) {
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
    public boolean isApplied() {
        return applied;
    }

    @Override
    public void resetStatus(){
        applied = false;
        enabled = true;
    }
}
