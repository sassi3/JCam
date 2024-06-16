package org.jcam.effects;

public abstract class LiveEffect extends Effect {
    protected LiveEffect() {
        super();
    }

    @Override
    public void apply() {
        throw new IllegalCallerException("This method is unsupported by " + this.getClass().getName());
    }
}
