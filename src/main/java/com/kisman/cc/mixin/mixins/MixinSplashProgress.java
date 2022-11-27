package com.kisman.cc.mixin.mixins;

import com.kisman.cc.features.module.client.CustomLoadingScreen;
import net.minecraftforge.fml.client.SplashProgress;
import net.minecraftforge.fml.common.FMLLog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashProgress.class)
public class MixinSplashProgress {

    /*
    @Shadow
    private static Thread thread;

    @Shadow
    private static volatile boolean done = false;

    @Shadow
    private static void checkThreadState(){}

    @Shadow
    private static volatile Throwable threadError;

    @Inject(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/SplashProgress;getMaxTextureSize()I", shift = At.Shift.AFTER), cancellable = true)
    private static void onThreadCreate(CallbackInfo ci){
        thread = new Thread(() -> {
            while(!done){
                CustomLoadingScreen.instance.start();
            }
        });
        thread.setUncaughtExceptionHandler((t, e) -> {
            FMLLog.log.error("Splash thread Exception", e);
            threadError = e;
        });
        thread.start();
        checkThreadState();
        ci.cancel();
    }
     */
}
