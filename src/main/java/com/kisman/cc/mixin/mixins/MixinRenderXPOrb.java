package com.kisman.cc.mixin.mixins;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderXPOrb;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author _kisman_
 * @since 20:42 of 08.01.2023
 */
@Mixin(RenderXPOrb.class)
public class MixinRenderXPOrb extends MixinRender<EntityXPOrb> {
    /**
     * @author idc
     * @reason idc
     */
    @Overwrite
    public void doRender(EntityXPOrb entity, double x, double y, double z, float entityYaw, float partialTicks) {
        try {
            if (!renderOutlines) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                bindEntityTexture(entity);
                RenderHelper.enableStandardItemLighting();
                int i = entity.getTextureByXP();
                float f = (float) (i % 4 * 16) / 64.0F;
                float f1 = (float) (i % 4 * 16 + 16) / 64.0F;
                float f2 = (float) (i / 4 * 16) / 64.0F;
                float f3 = (float) (i / 4 * 16 + 16) / 64.0F;
                int j = entity.getBrightnessForRender();
                int k = j % 65536;
                int l = j / 65536;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) k, (float) l);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                float f9 = ((float) entity.xpColor + partialTicks) / 2.0F;
                l = (int) ((MathHelper.sin(f9 + 0.0F) + 1.0F) * 0.5F * 255.0F);
                int j1 = (int) ((MathHelper.sin(f9 + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
                GlStateManager.translate(0.0F, 0.1F, 0.0F);
                GlStateManager.rotate(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate((renderManager.options.thirdPersonView == 2 ? -1 : 1) * -renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                float f7 = 0.3F;
                GlStateManager.scale(0.3F, 0.3F, 0.3F);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                bufferbuilder.pos(-0.5D, -0.25D, 0.0D).tex(f, f3).color(l, 255, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
                bufferbuilder.pos(0.5D, -0.25D, 0.0D).tex(f1, f3).color(l, 255, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
                bufferbuilder.pos(0.5D, 0.75D, 0.0D).tex(f1, f2).color(l, 255, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
                bufferbuilder.pos(-0.5D, 0.75D, 0.0D).tex(f, f2).color(l, 255, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
                tessellator.draw();
                GlStateManager.disableBlend();
                GlStateManager.disableRescaleNormal();
                GlStateManager.popMatrix();
                super.doRender(entity, x, y, z, entityYaw, partialTicks);
            }
        } catch(Throwable ignored) { }
    }
}
