package com.kisman.cc.util.render.shader.uniform.type.types;

import com.kisman.cc.util.render.shader.uniform.type.Type;
import org.lwjgl.opengl.GL20;

/**
 * @author _kisman_
 * @since 15:12 of 16.08.2022
 */
public class TypeFloat extends Type<Float, TypeFloat> {
    @Override public Float getDefault() { return 0f; }
    @Override public TypeFloat setup(int uniform) {
        GL20.glUniform1f(uniform, get());
        return this;
    }
}
