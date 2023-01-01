package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.client.PingBypass;
import com.kisman.cc.features.pingbypass.gui.GuiAddPingBypass;
import com.kisman.cc.features.pingbypass.gui.GuiButtonPingBypassOptions;
import com.kisman.cc.features.pingbypass.gui.GuiConnectingPingBypass;
import com.kisman.cc.features.viaforge.ViaForge;
import com.kisman.cc.features.viaforge.protocol.ProtocolCollection;
import com.kisman.cc.gui.alts.AltManagerGUI;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(value = GuiMultiplayer.class, priority = 10000)
public class MixinGuiMultiplayer extends GuiScreen {
    private GuiButton pingBypassButton;

    @Inject(method = "initGui", at = @At("RETURN"))
    public void initGui(CallbackInfo ci) {
        buttonList.add(new GuiButton(417, 7, 7, 60, 20, "Alts"));
        buttonList.add(new GuiButton(1337, 7, 7 * 2 + 20, 98, 20, ProtocolCollection.getProtocolById(ViaForge.getInstance().getVersion()).getName()));

        buttonList.add(new GuiButtonPingBypassOptions(7331, width - 24, 5));
        pingBypassButton = addButton(new GuiButton(1773, width - 126, 5, 100, 20, getDisplayString()));
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void actionPerformed(GuiButton button, CallbackInfo ci) {
        if(button.id == 417) mc.displayGuiScreen(new AltManagerGUI(this));
        else if (button.id == 1337) mc.displayGuiScreen(Kisman.instance.viaForgeGui.setLastGui(this));
        else if(button.id == 1773) {
            PingBypass.INSTANCE.toggle();
            pingBypassButton.displayString = getDisplayString();
        } else if(button.id == 7331) mc.displayGuiScreen(new GuiAddPingBypass(this));
    }

    @Inject(method = "confirmClicked", at = @At("HEAD"))
    public void confirmClickedHook(boolean result, int id, CallbackInfo ci) {
        if (id == 1773) mc.displayGuiScreen(this);
    }

    @Inject(method = "connectToServer", at = @At("HEAD"), cancellable = true)
    public void connectToServerHook(ServerData data, CallbackInfo ci) {
        if (PingBypass.INSTANCE.isToggled()) {
            mc.displayGuiScreen(new GuiConnectingPingBypass(this, mc, data));
            ci.cancel();
        }
    }

    private String getDisplayString() {
        return "PingBypass: " + (PingBypass.INSTANCE.isToggled() ? TextFormatting.GREEN + "On" : TextFormatting.RED + "Off");
    }
}
