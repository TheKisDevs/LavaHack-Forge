package com.kisman.cc.mixin.mixins.baritone;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.TabCompleteEvent;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.TabCompleter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TabCompleter.class)
public class MixinTabCompleter {
    @Shadow @Final protected GuiTextField textField;
    @Shadow protected boolean requestedCompletions;
    @Shadow public void setCompletions(String... newCompl) {}
    @Unique protected boolean dontComplete = false;

    @Inject(
            method = "requestCompletions",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRequestCompletions(String prefix, CallbackInfo ci) {
        if (!((Object) this instanceof GuiChat.ChatTabCompleter)) return;

        IBaritone baritone = BaritoneAPI.getProvider().getPrimaryBaritone();

        TabCompleteEvent event = new TabCompleteEvent(prefix);
        baritone.getGameEventHandler().onPreTabComplete(event);

        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        if (event.completions != null) {
            ci.cancel();

            this.dontComplete = true;

            try {
                this.requestedCompletions = true;
                setCompletions(event.completions);
            } finally {
                this.dontComplete = false;
            }
        }
    }
}
