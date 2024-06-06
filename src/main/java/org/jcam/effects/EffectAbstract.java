package org.jcam.effects;

import lombok.Setter;

@Setter
public abstract class EffectAbstract implements Effect {
    private boolean enabled;
    private boolean applied;

    public EffectAbstract() {
        this.enabled = false;
        this.applied = false;
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
