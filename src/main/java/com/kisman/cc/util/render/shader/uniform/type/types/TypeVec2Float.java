package com.kisman.cc.util.render.shader.uniform.type.types;

import com.kisman.cc.util.render.shader.uniform.type.Type;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.opengl.GL20;

/**
 * @author _kisman_
 * @since 15:17 of 16.08.2022
 */
public class TypeVec2Float extends Type<Vec2f, TypeVec2Float> {
    @Override public Vec2f getDefault() { return new Vec2f(0, 0); }
    @Override public TypeVec2Float setup(int uniform) {
        GL20.glUniform2f(uniform, get().x, get().y);
        return this;
    }
}
