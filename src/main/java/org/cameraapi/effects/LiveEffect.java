package org.cameraapi.effects;

public abstract class LiveEffect {
    private static boolean enabled;
    private static boolean applied;

    public static void enable() {
        enabled = true;
    }

    public static void disable() {
        enabled = false;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void apply() {
        applied = !applied;
    }

    public static boolean isApplied() {
        return applied;
    }
}
