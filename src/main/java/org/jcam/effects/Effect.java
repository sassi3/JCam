package org.jcam.effects;

import javafx.scene.image.ImageView;
import lombok.NonNull;
import lombok.Setter;
import org.jcam.lib.Applicable;
import org.jcam.lib.Enableable;
import org.jcam.lib.Resettable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class Effect implements Enableable, Applicable, Resettable {
    private boolean enabled;
    @Setter
    private boolean applied;
    protected static Map<Class<? extends Effect>, Effect>  instances = new HashMap<>();

    protected Effect() {
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

    public abstract void apply(@NonNull ImageView imageAffected);

    @Override
    public boolean isApplied() {
        return applied;
    }

    @Override
    public void reset(){
        applied = false;
        enabled = true;
    }

    public static <T extends Effect> T getUniqueInstance(Class<T> effectClass) {
        try {
            if(instances.get(effectClass) == null) {
                instances.put(effectClass, effectClass.cast(Class.forName(effectClass.getName()).getDeclaredConstructor().newInstance()));
            }
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return effectClass.cast(instances.get(effectClass));
    }

}
