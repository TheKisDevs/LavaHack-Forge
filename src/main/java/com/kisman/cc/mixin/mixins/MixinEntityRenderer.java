package com.kisman.cc.mixin.mixins;

import com.google.common.base.Predicate;
import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventEntityRender;
import com.kisman.cc.event.events.EventRenderGetEntitiesINAABBexcluding;
import com.kisman.cc.module.render.Ambience;
import com.kisman.cc.module.render.NoRender;
import com.kisman.cc.util.MathUtil;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    /*@Shadow
    @Final
    private int[] lightmapColors;*/

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    public void setupFog(int startCoords, float partialTicks, CallbackInfo ci) {
        if(NoRender.instance.isToggled() && NoRender.instance.fog.getValBoolean()) {
            ci.cancel();
        }
    }

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        EventRenderGetEntitiesINAABBexcluding event = new EventRenderGetEntitiesINAABBexcluding();
        Kisman.EVENT_BUS.post(event);

        if(event.isCancelled()) {
            return new ArrayList<>();
        } else {
            return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
        }
    }

    /*@Inject(method = "renderWorldPass", at = @At("HEAD"), cancellable = true)
    private void renderWorldPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        EventEntityRender event = new EventEntityRender(partialTicks, Event.Era.PRE);
        Kisman.EVENT_BUS.post(event);

        if(event.isCancelled()) {
            ci.cancel();
        }
    }*/

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    private void hurt(float particalTicks, CallbackInfo ci) {
        if(NoRender.instance.isToggled() && NoRender.instance.hurtCam.getValBoolean()) {
            ci.cancel();
        }
    }

    /*@Inject(method = "updateLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V", shift = At.Shift.BEFORE))
    private void updateTextureHook(float partialTicks, CallbackInfo ci) {
        if (AMBIENCE.isEnabled()) {
            for (int i = 0; i < lightmapColors.length; i++) {
                Color ambientColor = Ambience.instance.getColor();
                int alpha = ambientColor.getAlpha();
                float modifier = alpha / 255.0f;
                int color = lightmapColors[i];
                int[] bgr = MathUtil.toRGBAArray(color);
                *//*int red = (bgr[2] + ambientColor.getRed()) / 2; // half-half mix of both colors
                int green = (bgr[1] + ambientColor.getGreen()) / 2;
                int blue = (bgr[0] + ambientColor.getBlue()) / 2;*//*
                Vec3d values = new Vec3d(bgr[2] / 255.0f, bgr[1] / 255.0f, bgr[0] / 255.0f);
                Vec3d newValues = new Vec3d(ambientColor.getRed() / 255.0f, ambientColor.getGreen() / 255.0f, ambientColor.getBlue() / 255.0f);
                Vec3d finalValues = MathUtil.mix(values, newValues, modifier);

                *//*int red = (int) (((bgr[2] * (1 - modifier)) + (ambientColor.getRed() * modifier)) / 2.0f); // half-half mix of both colors
                int green = (int) (((bgr[1] * (1 - modifier)) + (ambientColor.getGreen() * modifier)) / 2.0f);
                int blue = (int) (((bgr[0] * (1 - modifier)) + (ambientColor.getBlue() * modifier)) / 2.0f);*//*
                // lightmapColors[i] = MathUtil.toRGB(red, green, blue);
                int red = (int) (finalValues.x * 255);
                int green = (int) (finalValues.y * 255);
                int blue = (int) (finalValues.z * 255);
                lightmapColors[i] = -16777216 | red << 16 | green << 8 | blue;
            }
        }
    }*/
}
