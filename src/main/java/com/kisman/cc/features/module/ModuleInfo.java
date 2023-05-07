package com.kisman.cc.features.module;

import com.kisman.cc.features.subsystem.subsystems.Targetable;
import org.lwjgl.input.Keyboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {
    String name();

    String desc() default "";

    Category category() default Category.CLIENT;

    String display() default "";

    int key() default Keyboard.KEY_NONE;

    boolean hold() default false;

    boolean toggled() default false;

    boolean toggleable() default true;

    boolean beta() default false;

    boolean debug() default false;

    boolean pingbypass() default false;

    boolean wip() default false;

    boolean submodule() default false;

    Class<? extends Module>[] modules() default { };

    Targetable targetable() default @Targetable(nearest = true, real = false);
}