package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.client.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontRenderer.class)
public class MixinFontRenderer {
    @Inject(method = "drawString", at = @At("HEAD"), cancellable = true)
    private void onDrawString(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        if(CustomFont.instance != null && ClientFont.instance != null && Kisman.instance != null) {
            if(CustomFont.instance.isToggled() && CustomFont.turnOn && ClientFont.instance.isToggled() && Kisman.instance.customFontRenderer != null) {
                int i = 0;

                switch(CustomFont.instance.mode.getValString()) {
                    case "Verdana": {
                        i = (int) Kisman.instance.customFontRenderer.drawString(text, x, y, color, dropShadow);
                        break;
                    }
                    case "Comfortaa": {
                        i = (int) CustomFontUtil.comfortaa18.drawString(text, x, y, color, dropShadow);
                        break;
                    }
                }

                cir.setReturnValue(i);
            }
        }
    }
}
