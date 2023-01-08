package com.kisman.cc.mixin.mixins;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author _kisman_
 * @since 20:43 of 08.01.2023
 */
@Mixin(Render.class)
public class MixinRender<T extends Entity> {
    @Shadow protected boolean bindEntityTexture(T entity) { return false; }
    @Shadow protected boolean renderOutlines;
    @Shadow @Final protected RenderManager renderManager;

    @Shadow public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) { }
}
