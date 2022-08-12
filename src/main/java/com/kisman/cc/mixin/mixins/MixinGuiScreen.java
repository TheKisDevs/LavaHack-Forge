package com.kisman.cc.mixin.mixins;

import baritone.utils.accessor.IGuiScreen;
import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventRenderToolTip;
import com.kisman.cc.features.module.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.util.List;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen extends Gui implements GuiYesNoCallback, IGuiScreen {
    @Shadow public Minecraft mc;
    @Shadow protected void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {}
    @Shadow public List<String> getItemToolTip(ItemStack p_191927_1_) {return null;}
    @Override @Invoker("openWebLink") public abstract void openLink(URI url);

    @Inject(method = "drawDefaultBackground", at = @At("HEAD"), cancellable = true)
    public void drof(CallbackInfo ci) {if(NoRender.instance.guiOverlay.getValBoolean() && mc.world != null && mc.player != null) ci.cancel();}
    @Override public void confirmClicked(boolean b, int i) {}
    @Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
    private void toolTipHook(ItemStack stack, int x, int y, CallbackInfo ci) {
        EventRenderToolTip event = new EventRenderToolTip(stack, x, y);
        Kisman.EVENT_BUS.post(event);
        if(!event.isCancelled()) {
            x = event.x;
            y = event.y;
            GuiUtils.preItemToolTip(stack);
            drawHoveringText(getItemToolTip(stack), x, y, mc.fontRenderer);
            GuiUtils.postItemToolTip();
        }
        ci.cancel();
    }
}
