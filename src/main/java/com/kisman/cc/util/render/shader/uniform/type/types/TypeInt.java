package com.kisman.cc.util.render.shader.uniform.type.types;

import com.kisman.cc.util.render.shader.uniform.type.Type;
import org.lwjgl.opengl.GL20;

/**
 * @author _kisman_
 * @since 15:00 of 16.08.2022
 */
public class TypeInt extends Type<Integer, TypeInt> {
    @Override public Integer getDefault() { return 0; }
    @Override public TypeInt setup(int uniform) {
        GL20.glUniform1i(uniform, get());
        return this;
    }
}
