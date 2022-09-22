package com.kisman.cc.mixin.mixins.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

/**
 * @author _kisman_
 * @since 19:14 of 22.09.2022
 */
@Mixin(Class.class)
public interface AccessorClass<T> {
    @Invoker("enumConstantDirectory") Map<String, T> enumConstantDirectory();
}
