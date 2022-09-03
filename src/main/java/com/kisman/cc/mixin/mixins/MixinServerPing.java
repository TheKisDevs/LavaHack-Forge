package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventServerPing;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPinger.class)
public class MixinServerPing {

    @Inject(method = "ping", at = @At("HEAD"), cancellable = true)
    public void onPing(ServerData server, CallbackInfo ci){
        EventServerPing.Normal event = new EventServerPing.Normal(server);
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled())
            ci.cancel();
    }

    @Inject(method = "tryCompatibilityPing", at = @At("HEAD"), cancellable = true)
    public void onTryCompatibilityPing(ServerData server, CallbackInfo ci){
        EventServerPing.Compatibility event = new EventServerPing.Compatibility(server);
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled())
            ci.cancel();
    }
}
