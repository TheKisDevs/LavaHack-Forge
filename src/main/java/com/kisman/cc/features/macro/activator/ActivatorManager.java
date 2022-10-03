package com.kisman.cc.features.macro.activator;

import com.kisman.cc.features.macro.activator.activators.KeyActivator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Cubic
 * @since 02.10.2022
 */
public final class ActivatorManager {

    private static final Map<String, ActivatorFactory<?>> ACTIVATORS_STRINGS = new ConcurrentHashMap<>();

    private static final Map<Class<?>, ActivatorFactory<?>> ACTIVATORS_CLASSES = new ConcurrentHashMap<>();

    public static ActivatorFactory<?> getFactory(String name) {
        return ACTIVATORS_STRINGS.get(name);
    }

    public static ActivatorFactory<?> getFactory(Class<?> cls) {
        return ACTIVATORS_CLASSES.get(cls);
    }

    static {
        ACTIVATORS_STRINGS.put("key", KeyActivator::new);
        ACTIVATORS_CLASSES.put(KeyActivator.class, KeyActivator::new);
    }
}
