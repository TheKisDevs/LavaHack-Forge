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

import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Shadow
    @Final
    private int[] lightmapColors;

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

    @Inject( method = "updateLightmap", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V", shift = At.Shift.BEFORE ) )
    private void updateTextureHook(float partialTicks, CallbackInfo ci) {
        Ambience ambience = Ambience.instance;
        if (ambience.isToggled()) {
            for (int i = 0; i < this.lightmapColors.length; ++i) {
                Color ambientColor = ambience.getColor();
                int alpha = ambientColor.getAlpha();
                float modifier = ( float ) alpha / 255.0f;
                int color = this.lightmapColors[ i ];
                int[] bgr = toRGBAArray(color);
                Vector3f values = new Vector3f(( float ) bgr[ 2 ] / 255.0f, ( float ) bgr[ 1 ] / 255.0f, ( float ) bgr[ 0 ] / 255.0f);
                Vector3f newValues = new Vector3f(( float ) ambientColor.getRed() / 255.0f, ( float ) ambientColor.getGreen() / 255.0f, ( float ) ambientColor.getBlue() / 255.0f);
                Vector3f finalValues = mix(values, newValues, modifier);
                int red = ( int ) (finalValues.x * 255.0f);
                int green = ( int ) (finalValues.y * 255.0f);
                int blue = ( int ) (finalValues.z * 255.0f);
                this.lightmapColors[ i ] = 0xFF000000 | red << 16 | green << 8 | blue;
            }
        }
    }

    private int[] toRGBAArray(int colorBuffer) {
        return new int[] { colorBuffer >> 16 & 0xFF, colorBuffer >> 8 & 0xFF, colorBuffer & 0xFF };
    }

    private Vector3f mix(Vector3f first, Vector3f second, float factor) {
        return new Vector3f(first.x * (1.0f - factor) + second.x * factor, first.y * (1.0f - factor) + second.y * factor, first.z * (1.0f - factor) + first.z * factor);
    }
}
