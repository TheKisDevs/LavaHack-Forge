package com.kisman.cc.util.render.shader.uniform.type.types;

import com.kisman.cc.util.render.shader.uniform.type.Type;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

/**
 * @author _kisman_
 * @since 20:36 of 16.08.2022
 */
public class TypeImage extends Type<Integer, TypeImage> {
    @Override public Integer getDefault() { return 0; }
    @Override public TypeImage setup(int uniform) {
        GL13.glActiveTexture(GL13.GL_TEXTURE8);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, get());
        GL20.glUniform1i(uniform, 8);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        return this;
    }
}
