package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RenderPlayer.class)
public class MixinPenderPlayer {
    @Overwrite
    public ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
        if(Kisman.instance.moduleManager.getModule("KismanESP").isToggled() && entity != Minecraft.getMinecraft().player) {
            GL11.glColor4f(1, 1, 1 ,1);
            if(entity.getName().equalsIgnoreCase("_kisman_")) {
                return new ResourceLocation("kismancc:kisman/kisman.png");
            } else {
                return new ResourceLocation("kismancc:kisman/nokisman");
            }
        } else {
            GL11.glColor4f(1, 1, 1, 1);
            return entity.getLocationSkin();
        }
    }
}
