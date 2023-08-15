package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.viaforge.ViaForge;
import com.kisman.cc.features.viaforge.protocol.ProtocolCollection;
import com.kisman.cc.gui.alts.AltManagerGUI;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(value = GuiMultiplayer.class, priority = 10000)
public class MixinGuiMultiplayer extends GuiScreen {
    @Inject(method = "initGui", at = @At("RETURN"))
    public void initGui(CallbackInfo ci) {
        buttonList.add(new GuiButton(417, 7, 7, 60, 20, "Alts"));
        buttonList.add(new GuiButton(1337, 7, 7 * 2 + 20, 98, 20, ProtocolCollection.getProtocolById(ViaForge.getInstance().getVersion()).getName()));
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void actionPerformed(GuiButton button, CallbackInfo ci) {
        if(button.id == 417) mc.displayGuiScreen(new AltManagerGUI(this));
        else if (button.id == 1337) mc.displayGuiScreen(Kisman.instance.viaForgeGui.setLastGui(this));
    }
}
