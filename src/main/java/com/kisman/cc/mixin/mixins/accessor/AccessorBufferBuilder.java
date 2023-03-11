package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.client.renderer.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author _kisman_
 * @since 18:45 of 09.03.2023
 */
@Mixin(BufferBuilder.class)
public interface AccessorBufferBuilder {
    @Accessor("isDrawing") boolean drawing();
    @Accessor("vertexCount") void vertexCount(int vertexCount);
}
