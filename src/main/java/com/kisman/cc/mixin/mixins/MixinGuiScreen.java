package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.NoRender;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class MixinGuiScreen extends Gui implements GuiYesNoCallback {
    @Inject(method = "drawDefaultBackground", at = @At("HEAD"), cancellable = true)
    public void drof(CallbackInfo ci) {if(NoRender.instance.guiOverlay.getValBoolean()) ci.cancel();}
    @Override public void confirmClicked(boolean b, int i) {}
}
