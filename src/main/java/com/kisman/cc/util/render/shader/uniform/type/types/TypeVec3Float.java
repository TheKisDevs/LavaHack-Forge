package com.kisman.cc.util.render.shader.uniform.type.types;

import com.kisman.cc.util.math.vectors.xyz.Vec3f;
import com.kisman.cc.util.render.shader.uniform.type.Type;
import org.lwjgl.opengl.GL20;

/**
 * @author _kisman_
 * @since 15:18 of 16.08.2022
 */
public class TypeVec3Float extends Type<Vec3f, TypeVec3Float> {
    @Override public Vec3f getDefault() { return new Vec3f( 0, 0, 0); }
    @Override public TypeVec3Float setup(int uniform) {
        GL20.glUniform3f(uniform, get().getX(), get().getY(), get().getZ());
        return this;
    }
}
