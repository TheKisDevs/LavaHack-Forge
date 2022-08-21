package com.kisman.cc.loader.mixins;

import com.kisman.cc.loader.LoaderKt;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author _kisman_
 * @since 2:09 of 15.08.2022
 */
//@Mixin(Loader.class)
public class MixinLoader {
//    @Inject(remap = false, method = "initializeMods", at = @At("TAIL"))
    public void initializeMods(CallbackInfo ci) {
        LoaderKt.initLavaHack();
    }
}
