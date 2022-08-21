package com.kisman.cc.util.render.shader.uniform.type.types;

import com.kisman.cc.util.math.vectors.Vec2i;
import com.kisman.cc.util.render.shader.uniform.type.Type;
import org.lwjgl.opengl.GL20;

/**
 * @author _kisman_
 * @since 15:15 of 16.08.2022
 */
public class TypeVec2Int extends Type<Vec2i, TypeVec2Int> {
    @Override public Vec2i getDefault() { return new Vec2i(); }
    @Override public TypeVec2Int setup(int uniform) {
        GL20.glUniform2i(uniform, get().x, get().y);
        return this;
    }
}
