package com.kisman.cc.mixin.mixins;

import com.google.common.eventbus.EventBus;
import com.kisman.cc.Kisman;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

/**
 * @author _kisman_
 * @since 10:49 of 15.08.2022
 */
@Mixin(EventBus.class)
public class MixinGoogleEventBus {
    @Inject(remap = false, method = "post", at = @At("TAIL"))
    private void postHook(Object event, CallbackInfo ci) {
        System.out.println("meow");
        if(event instanceof FMLInitializationEvent) {
            if(!Kisman.runningFromIntelliJ() && classExits("com.kisman.cc.loader.LoaderKt")) {
                System.out.println("meow1");
                try {
                    Kisman.instance.init();
                } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                    Kisman.unsafeCrash();
                }
            }
            System.out.println("meow2");
        }
    }

    private boolean classExits(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
