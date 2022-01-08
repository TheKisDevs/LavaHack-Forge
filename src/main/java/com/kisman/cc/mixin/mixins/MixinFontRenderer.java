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
        if(Kisman.instance != null && ClientFont.instance != null) {
            if(ClientFont.instance.isToggled() && Kisman.instance.init) {
                int i;

                i = CustomFontUtil.drawString(text, x, y, color);

                cir.setReturnValue(i);
            }
        }
    }
}
