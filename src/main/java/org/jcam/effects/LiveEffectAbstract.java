package org.jcam.effects;

import javafx.scene.image.ImageView;

public abstract class LiveEffectAbstract extends EffectAbstract {
    public abstract void toggle(ImageView imageAffected);
}
