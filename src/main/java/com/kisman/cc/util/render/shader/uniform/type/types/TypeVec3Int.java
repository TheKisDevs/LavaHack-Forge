package com.kisman.cc.util.render.shader.uniform.type.types;

import com.kisman.cc.util.render.shader.uniform.type.Type;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.opengl.GL20;

/**
 * @author _kisman_
 * @since 15:18 of 16.08.2022
 */
public class TypeVec3Int extends Type<Vec3i, TypeVec3Int> {
    @Override public Vec3i getDefault() { return new Vec3i(0, 0, 0); }
    @Override public TypeVec3Int setup(int uniform) {
        GL20.glUniform3i(uniform, get().getX(), get().getY(), get().getZ());
        return this;
    }
}
