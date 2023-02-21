package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.schematica.schematica.Schematica;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author _kisman_
 * @since 12:23 of 04.09.2022
 */
@Mixin(Locale.class)
public class MixinLocale {
    @Inject(
            method = "isUnicode",
            at = @At("HEAD"),
            cancellable = true
    )
    private void isUnicodeHook(
            CallbackInfoReturnable<Boolean> cir
    ) {
        if(Kisman.instance.moduleManager != null && Kisman.instance.moduleManager.getModule("ClientFixer").isToggled() && !Minecraft.getMinecraft().gameSettings.forceUnicodeFont && Minecraft.getMinecraft().gameSettings.language.equalsIgnoreCase("ru_ru")) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(
            method = "translateKeyPrivate",
            at = @At("HEAD"),
            cancellable = true
    )
    private void translateKeyPrivateHook(String translateKey, CallbackInfoReturnable<String> cir) {
        //TODO: event!!!

        String value = Schematica.properties.get(translateKey);

        if(value != null) {
            cir.setReturnValue(value);
            cir.cancel();
        }
    }
}
