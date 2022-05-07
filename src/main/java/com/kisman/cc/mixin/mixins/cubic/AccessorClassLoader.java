package com.kisman.cc.mixin.mixins.cubic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.security.ProtectionDomain;

@Mixin(ClassLoader.class)
public interface AccessorClassLoader {

    @Invoker("findLoadedClass")
    Class<?> findLoadedClass(String name);

    @Invoker("getPackage")
    Package getPackage(String name);

    @Invoker("findClass")
    Class<?> findClass(String name) throws ClassNotFoundException;
}
