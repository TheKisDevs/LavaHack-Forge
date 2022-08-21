package com.kisman.cc.util.render.shader.uniform.type.types;

import com.kisman.cc.util.math.vectors.Vec4f;
import com.kisman.cc.util.render.shader.uniform.type.Type;
import org.lwjgl.opengl.GL20;

/**
 * @author _kisman_
 * @since 15:23 of 16.08.2022
 */
public class TypeVec4Float extends Type<Vec4f, TypeVec4Float> {
    @Override public Vec4f getDefault() { return new Vec4f(0, 0, 0, 0); }
    @Override public TypeVec4Float setup(int uniform) {
        GL20.glUniform4f(uniform, get().getX(), get().getY(), get().getZ(), get().getW());
        return this;
    }
}
