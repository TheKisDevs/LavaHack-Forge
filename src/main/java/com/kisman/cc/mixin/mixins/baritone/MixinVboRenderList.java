package com.kisman.cc.mixin.mixins.baritone;

import baritone.Baritone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VboRenderList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static org.lwjgl.opengl.GL11.*;

@Mixin(VboRenderList.class)
public class MixinVboRenderList {

    @Redirect( // avoid creating CallbackInfo at all costs; this is called 40k times per second
            method = "renderChunkLayer",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/renderer/GlStateManager.popMatrix()V"
            )
    )
    private void popMatrix() {
        if (Baritone.settings().renderCachedChunks.value && !Minecraft.getMinecraft().isSingleplayer()) {
            // reset the blend func to normal (not dependent on constant alpha)
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        }
        GlStateManager.popMatrix();
    }
}
