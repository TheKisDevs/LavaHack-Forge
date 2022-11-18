package com.kisman.cc.features.module;

public @interface ModuleProps {

    boolean isAddon() default false;

    Addon addon() default @Addon(parents = "");

    boolean beta() default false;

    boolean debug() default false;

    boolean pingBypass() default false;

    boolean workInProgress() default false;
}
