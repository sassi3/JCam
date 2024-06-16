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
    protected static Map<Class<? extends Effect>, Effect>  instances = new HashMap<>(); // Map already existing classes and their instances

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
            // Checks if the class instance has already being created
            if(instances.get(effectClass) == null) {
                // if not creates a new instance
                instances.put(effectClass, effectClass.cast(Class.forName(effectClass.getName()).getDeclaredConstructor().newInstance()));
            }
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        // In any case returns the unique Instance. So it is recommended to use this every time we want to access instance methods of an Effect class
        return effectClass.cast(instances.get(effectClass));
    }

}
