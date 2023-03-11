package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventCape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractClientPlayer.class, priority = 10000)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {
    @Shadow public NetworkPlayerInfo playerInfo;

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        EventCape event = new EventCape(playerInfo);
        event.setResLoc(playerInfo == null ? null : playerInfo.getLocationCape());
        Kisman.EVENT_BUS.post(event);
        cir.setReturnValue(event.getResLoc());
    }
}