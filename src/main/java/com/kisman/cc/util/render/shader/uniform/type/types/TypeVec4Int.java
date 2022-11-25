package com.kisman.cc.util.render.shader.uniform.type.types;

import com.kisman.cc.util.math.vectors.xyzw.Vec4i;
import com.kisman.cc.util.render.shader.uniform.type.Type;
import org.lwjgl.opengl.GL20;

/**
 * @author _kisman_
 * @since 15:21 of 16.08.2022
 */
public class TypeVec4Int extends Type<Vec4i, TypeVec4Int> {
    @Override public Vec4i getDefault() { return new Vec4i(0, 0, 0, 0); }
    @Override public TypeVec4Int setup(int uniform) {
        GL20.glUniform4i(uniform, get().getX(), get().getY(), get().getZ(), get().getW());
        return this;
    }
}
