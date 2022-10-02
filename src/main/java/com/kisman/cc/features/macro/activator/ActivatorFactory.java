package com.kisman.cc.features.macro.activator;

import com.kisman.cc.features.macro.impl.MacroImpl;

/**
 * @author Cubic
 * @since 02.10.2022
 */
public interface ActivatorFactory<T extends Activator> {

    T construct(String condition, MacroImpl macro);
}
