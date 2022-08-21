package com.kisman.cc.util.render.shader.uniform.type.types;

import com.kisman.cc.util.render.shader.uniform.type.Type;
import org.lwjgl.opengl.GL20;

/**
 * @author _kisman_
 * @since 15:13 of 16.08.2022
 */
public class TypeBool extends Type<Boolean, TypeBool> {
    @Override public Boolean getDefault() { return false; }
    @Override public TypeBool setup(int uniform) {
        GL20.glUniform1i(uniform, get() ? 1 : 0);
        return this;
    }
}
