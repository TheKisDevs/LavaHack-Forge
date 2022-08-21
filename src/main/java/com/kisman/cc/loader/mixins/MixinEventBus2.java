package com.kisman.cc.loader.mixins;

import com.google.common.eventbus.EventBus;
import com.kisman.cc.loader.LoaderKt;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author _kisman_
 * @since 10:49 of 15.08.2022
 */
@Mixin(EventBus.class)
public class MixinEventBus2 {
    @Inject(remap = false, method = "post", at = @At("TAIL"))
    private void postHook(Object event, CallbackInfo ci) {
        if(event instanceof FMLInitializationEvent) {
            System.out.println("meow3");
            LoaderKt.initLavaHack();
        }
    }
}
