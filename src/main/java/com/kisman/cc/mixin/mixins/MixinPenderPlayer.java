package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventRenderEntityName;
import com.kisman.cc.module.render.Charms;
import com.kisman.cc.module.render.Reverse;
import com.kisman.cc.module.render.Spin;
import com.kisman.cc.util.GLUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import static org.lwjgl.opengl.GL11.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class MixinPenderPlayer {
    @Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
    public void onRenderEntityName(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo ci) {
        EventRenderEntityName event = new EventRenderEntityName(entityIn, x, y, z, name, distanceSq);
        Kisman.EVENT_BUS.post(event);

        if(event.isCancelled()) {
            ci.cancel();
        }
    }


    @Inject(method = "preRenderCallback", at = @At("HEAD"))
    public void renderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime, CallbackInfo ci) {
        if(Spin.instance.isToggled()) {
            float f = 0.9357f;
            float hue = (float) (System.currentTimeMillis() % 22600L) / 5.0f;

            GlStateManager.scale(f, f, f);

            GlStateManager.rotate(hue, 1, 0, hue);
        }

        if(Reverse.instance.isToggled() && !Spin.instance.isToggled()) {
            Minecraft mc = Minecraft.getMinecraft();
            double height = mc.player.boundingBox.maxY - mc.player.boundingBox.minY;

            GlStateManager.rotate(180, 1, 0, 0);
            GlStateManager.translate(mc.player.posX, mc.player.posY + height, mc.player.posZ);
        }
    }
}
