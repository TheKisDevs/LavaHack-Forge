package com.kisman.cc.mixin.mixins;

import com.kisman.cc.features.module.render.NameTags;
import com.kisman.cc.util.client.interfaces.IFakeEntity;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {
    @Inject(method = "renderEntityName(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDLjava/lang/String;D)V", at = @At("HEAD"), cancellable = true)
    private void drawBigBebra(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo ci) {
        if(entityIn instanceof IFakeEntity && !((IFakeEntity) entityIn).showNameTag()) ci.cancel();
        if(NameTags.instance.isToggled() && entityIn instanceof EntityPlayer) ci.cancel();
    }
}
