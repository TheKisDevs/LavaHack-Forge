package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventRenderToolTip;
import com.kisman.cc.module.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen extends Gui implements GuiYesNoCallback {
    @Shadow public Minecraft mc;
    @Shadow protected FontRenderer fontRenderer;
    @Shadow protected void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {}
    @Shadow public List<String> getItemToolTip(ItemStack p_191927_1_) {return null;}

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
            FontRenderer font = stack.getItem().getFontRenderer(stack);
            GuiUtils.preItemToolTip(stack);
            drawHoveringText(getItemToolTip(stack), x, y, font == null ? fontRenderer : font);
            GuiUtils.postItemToolTip();
        }
        ci.cancel();
    }
}
