package com.kisman.cc.mixin.mixins;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EventBus.class)
public class MixinEventBus {
    @Redirect(remap = false, method = "register(Ljava/lang/Object;)V", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    private void registerHook(Logger instance, String s, Object o1, Object o2) {}
}