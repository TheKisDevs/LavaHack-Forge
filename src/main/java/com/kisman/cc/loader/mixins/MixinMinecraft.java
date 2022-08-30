package com.kisman.cc.loader.mixins;

import com.kisman.cc.loader.LavaHackLoaderMod;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author _kisman_
 * @since 22:33 of 30.08.2022
 */
@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(
            method = "init",
            at = @At("RETURN")
    ) private void initHook(CallbackInfo ci) {
        LavaHackLoaderMod mod = new LavaHackLoaderMod();

        mod.init(null);
    }
}
