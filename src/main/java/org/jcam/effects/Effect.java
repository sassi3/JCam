package org.jcam.effects;

import org.jcam.lib.Applicable;
import org.jcam.lib.Enableable;
import org.jcam.lib.Resettable;

public abstract class Effect implements Enableable, Applicable, Resettable {
    private boolean enabled;
    private boolean applied;

    public Effect() {
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
    public void apply() {
        applied = !applied;
    }

    @Override
    public boolean isApplied() {
        return applied;
    }

    @Override
    public void reset(){
        applied = false;
        enabled = true;
    }
}
