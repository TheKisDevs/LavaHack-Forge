package com.kisman.cc.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GLUtil {
    public static void enableCharmsProfile() {
        GlStateManager.color(30 / 255f, 220 / 255, 1, 0.35F);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);
    }

    public static void disableCharmsProfile() {
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.depthMask(true);
    }

    @SideOnly(Side.CLIENT)
    public static enum Profile {
        PLAYER_SKIN {
            public void apply()
            {
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            }

            public void clean()
            {
                GlStateManager.disableBlend();
            }
        },
        TRANSPARENT_MODEL {
            public void apply() {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }
            public void clean() {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.depthMask(true);
            }
        };
    }
}
