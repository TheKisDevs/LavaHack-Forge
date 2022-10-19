package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventEntityFreeCam;
import com.kisman.cc.event.events.EventItemRenderer;
import com.kisman.cc.features.module.Debug.SwingTest;
import com.kisman.cc.features.module.combat.KillAuraRewrite;
import com.kisman.cc.features.module.render.*;
import com.kisman.cc.util.entity.player.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemRenderer.class, priority = 10000)
public class MixinItemRenderer {
    @Shadow @Final public Minecraft mc;

    @Shadow
    public void renderItemInFirstPerson(AbstractClientPlayer player, float partialTicks, float pitch, EnumHand hand, float swingProgress, ItemStack stack, float equippedProgress){}

    private float lastSwingProgress = 0f;

    private boolean injection = true;

    @Inject(method = "rotateArm", at = @At("HEAD"), cancellable = true)
    private void doRotateArm(float p_187458_1_, CallbackInfo ci) {
        if(NoRender.instance.isToggled() && NoRender.instance.sway.getValBoolean()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "HEAD"), cancellable = true)
    private void onRenderItemInFirstPerson(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo ci){
        if(this.injection){
            if(!SmallShield.INSTANCE.isToggled())
                return;
            if(stack.isEmpty)
                return;
            ci.cancel();
            float xOff;
            float yOff;
            this.injection = false;
            if(hand == EnumHand.MAIN_HAND){
                xOff = SmallShield.INSTANCE.mainX.getValFloat();
                yOff = SmallShield.INSTANCE.mainY.getValFloat();
            } else {
                xOff = SmallShield.INSTANCE.offX.getValFloat();
                yOff = SmallShield.INSTANCE.offY.getValFloat();
            }
            this.renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOff, stack, p_187457_7_ + yOff);
            this.injection = true;
        }
    }


    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", shift = At.Shift.AFTER))
    private void transformSideFirstPersonInvokePushMatrix(AbstractClientPlayer player, float partialTicks, float pitch, EnumHand hand, float swingProgress, ItemStack stack, float equippedProgress, CallbackInfo ci) {
        if(ViewModel.instance.isToggled() && ViewModel.instance.hands.getValBoolean()) ViewModel.instance.hand(hand.equals(EnumHand.MAIN_HAND) ? player.getPrimaryHand() : player.getPrimaryHand().opposite());
        Kisman.EVENT_BUS.post(new EventItemRenderer(
                hand.equals(EnumHand.MAIN_HAND) ? player.getPrimaryHand() : player.getPrimaryHand().opposite(),
                swingProgress
        ));

        lastSwingProgress = swingProgress;
    }

    @Redirect(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformSideFirstPerson(Lnet/minecraft/util/EnumHandSide;F)V"))
    public void transformRedirect(ItemRenderer renderer, EnumHandSide hand, float y) {
        Vec3d translate = new Vec3d((hand == EnumHandSide.RIGHT ? 1 : -1) * 0.56, -0.52 + y * -0.6, -0.72);
        Vec3d rotate = new Vec3d(0.0, 0.0, 0.0);
        Vec3d scale = new Vec3d(1, 1, 1);

        boolean isEating = PlayerUtil.IsEating();
        boolean isSwing = mc.player.swingProgress > 0 && SwingAnimation.instance.isToggled() && SwingAnimation.instance.mode.getValString().equalsIgnoreCase("Strong");
        boolean isSwingMain = (SwingAnimation.instance.ifKillAura.getValBoolean() && Kisman.instance.moduleManager.getModule("KillAuraRewrite").isToggled() && KillAuraRewrite.Companion.getTarget() != null || isSwing) && hand == EnumHandSide.RIGHT && (!SwingAnimation.instance.ignoreEating.getValBoolean() || !isEating);

        if(ViewModel.instance.isToggled()) {
            if (hand == EnumHandSide.RIGHT) {
                if(!(isEating && !ViewModel.instance.customEating.getValBoolean())) {
                    if(ViewModel.instance.translate.getValBoolean()) translate = new Vec3d(ViewModel.instance.translateRightX.getValDouble(), ViewModel.instance.translateRightY.getValDouble(), ViewModel.instance.translateRightZ.getValDouble());
                    if(!isSwingMain) {
                        rotate = new Vec3d(
                                (!ViewModel.instance.autoRotateRigthX.getValBoolean() ? ((float) (ViewModel.instance.rotateRightX.getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f),
                                (!ViewModel.instance.autoRotateRigthY.getValBoolean() ? ((float) (ViewModel.instance.rotateRightY.getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f),
                                (!ViewModel.instance.autoRotateRigthZ.getValBoolean() ? ((float) (ViewModel.instance.rotateRightZ.getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f)
                        );
                    }
                }
                scale = new Vec3d(ViewModel.instance.scaleRightX.getValDouble(), ViewModel.instance.scaleRightY.getValDouble(), ViewModel.instance.scaleRightZ.getValDouble());
            }

            if (hand == EnumHandSide.LEFT) {
                if(!(PlayerUtil.isEatingOffhand() && !ViewModel.instance.customEating.getValBoolean())) {
                    if(ViewModel.instance.translate.getValBoolean()) translate = new Vec3d(ViewModel.instance.translateLeftX.getValDouble(), ViewModel.instance.translateLeftY.getValDouble(), ViewModel.instance.translateLeftZ.getValDouble());
                    rotate = new Vec3d(
                            (!ViewModel.instance.autoRotateLeftX.getValBoolean() ? ((float) (ViewModel.instance.rotateLeftX.getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f),
                            (!ViewModel.instance.autoRotateLeftY.getValBoolean() ? ((float) (ViewModel.instance.rotateLeftY.getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f),
                            (!ViewModel.instance.autoRotateLeftZ.getValBoolean() ? ((float) (ViewModel.instance.rotateLeftZ.getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f)
                    );
                }
                scale = new Vec3d(ViewModel.instance.scaleLeftX.getValDouble(), ViewModel.instance.scaleLeftY.getValDouble(), ViewModel.instance.scaleLeftZ.getValDouble());
            }
        }

        if(isSwingMain) {
            switch (SwingAnimation.instance.strongMode.getValString()) {
                case "Blockhit1": {
                    rotate = new Vec3d(72, 180, 240);
                    break;
                }
                case "Blockhit2": {
                    rotate = new Vec3d(344, 225, 0);
                    break;
                }
                case "Knife": {
                    rotate = new Vec3d(43, 130, 230);
                }
                case "Custom": {
                    rotate = new Vec3d(
                            SwingAnimation.instance.rotateX.getValDouble(),
                            SwingAnimation.instance.rotateY.getValDouble(),
                            SwingAnimation.instance.rotateZ.getValDouble()
                    );
                }
            }

            if(SwingAnimation.instance.test.getValBoolean()) {
                rotate = rotate.scale(lastSwingProgress);
            }
        }

        GlStateManager.translate(translate.x, translate.y, translate.z);
        GlStateManager.scale(scale.x, scale.y, scale.z);
        GlStateManager.rotate((float) rotate.x, 1, 0, 0);
        GlStateManager.rotate((float) rotate.y, 0, 1, 0);
        GlStateManager.rotate((float) rotate.z, 0, 0, 1);

        if(SwingTest.INSTANCE.toggled) {
            SwingTest.INSTANCE.renderItems(hand, lastSwingProgress);
        }
    }

    @Redirect(method = "setLightmap", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
    private EntityPlayerSP redirectLightmapPlayer(Minecraft mc) {
        EventEntityFreeCam event = new EventEntityFreeCam();
        event.entity = mc.player;
        Kisman.EVENT_BUS.post(event);
        return event.entity;
    }

    @Redirect(method = "rotateArm", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
    private EntityPlayerSP rotateArmPlayer(Minecraft mc) {
        EventEntityFreeCam event = new EventEntityFreeCam();
        event.entity = mc.player;
        Kisman.EVENT_BUS.post(event);
        return event.entity;
    }

    @Redirect(method = "renderItemInFirstPerson(F)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
    private EntityPlayerSP redirectPlayer(Minecraft mc) {
        EventEntityFreeCam event = new EventEntityFreeCam();
        event.entity = mc.player;
        Kisman.EVENT_BUS.post(event);
        return event.entity;
    }

    @Redirect(method = "renderOverlays", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
    private EntityPlayerSP renderOverlaysPlayer(Minecraft mc) {
        EventEntityFreeCam event = new EventEntityFreeCam();
        event.entity = mc.player;
        Kisman.EVENT_BUS.post(event);
        return event.entity;
    }
}