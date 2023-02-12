package com.kisman.cc.mixin.mixins;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.RenderEvent;
import com.google.common.base.Predicate;
import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import com.kisman.cc.features.module.player.CameraClip;
import com.kisman.cc.features.module.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes", "Guava"})
@Mixin(value = EntityRenderer.class, priority = 10000)
public class MixinEntityRenderer {
    @Mutable @Shadow @Final public int[] lightmapColors;

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    public void setupFog(int startCoords, float partialTicks, CallbackInfo ci) {
        EventSetupFog event = new EventSetupFog();
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled()) ci.cancel();
    }

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        EventRenderGetEntitiesINAABBexcluding event = new EventRenderGetEntitiesINAABBexcluding();
        Kisman.EVENT_BUS.post(event);

        if(event.isCancelled()) return new ArrayList<>();
        else return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    private void hurt(float particalTicks, CallbackInfo ci) {
        if(NoRender.instance.isToggled() && NoRender.instance.hurtCam.getValBoolean()) ci.cancel();
    }

    @Inject(method = "updateLightmap", at = @At("HEAD"), cancellable = true)
    private void skylightFix(float partialTicks, CallbackInfo ci) {
        EventUpdateLightmap event = new EventUpdateLightmap.Pre();
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled()) ci.cancel();
    }

    @Inject(method = "updateLightmap", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V", shift = At.Shift.BEFORE ))
    private void updateTextureHook(float partialTicks, CallbackInfo ci) {
        EventUpdateLightmap.Post event = new EventUpdateLightmap.Post(lightmapColors);
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled()) lightmapColors = event.getLightmapColors();
    }

    @Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"), expect = 0)
    private RayTraceResult rayTraceBlocks(WorldClient worldClient, Vec3d start, Vec3d end) {
        return CameraClip.instance.isToggled() ? null : worldClient.rayTraceBlocks(start, end);
    }

    @Redirect(method={"setupCameraTransform"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
        EventAspect event = new EventAspect((float) Minecraft.getMinecraft().displayWidth / Minecraft.getMinecraft().displayHeight);
        Kisman.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(method={"renderWorldPass"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
        EventAspect event = new EventAspect((float) Minecraft.getMinecraft().displayWidth / Minecraft.getMinecraft().displayHeight);
        Kisman.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(method={"renderCloudsCheck"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
        EventAspect event = new EventAspect((float) Minecraft.getMinecraft().displayWidth / Minecraft.getMinecraft().displayHeight);
        Kisman.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Inject(
            method = "renderWorldPass",
            at = @At(
                    value = "INVOKE_STRING",
                    target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
                    args = {"ldc=hand"}
            )
    )
    private void renderWorldPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        for (IBaritone ibaritone : BaritoneAPI.getProvider().getAllBaritones()) {
            ibaritone.getGameEventHandler().onRenderPass(new RenderEvent(partialTicks));
        }
    }

    @Inject(method = "applyBobbing", at = @At("HEAD"), cancellable = true)
    private void onApplyBobbing(float partialTicks, CallbackInfo ci){
        EventApplyBobbing event = new EventApplyBobbing(partialTicks);
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled())
            ci.cancel();
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void onRenderArm(float partialTicks, int pass, CallbackInfo ci){
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.player == null || mc.world == null)
            return;
        if(!mc.player.getHeldItemMainhand().isEmpty)
            return;
        if(NoRender.instance.isToggled() &&  NoRender.instance.hands.getValBoolean())
            ci.cancel();
    }
}
