package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.ContainerModifier;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = GuiContainer.class, priority = 10000)
public class MixinGuiContainer extends GuiScreen {
    @Shadow protected int guiLeft, guiTop, xSize, ySize;

    @Inject(method = "drawScreen", at = @At("TAIL"))
    public void drawee(int mouseX, int mouseY, float particalTicks, CallbackInfo ci) {
        if(ContainerModifier.instance.isToggled() && ContainerModifier.instance.containerShadow.getValBoolean()) {
            {
                double x = 0, y = (guiTop + xSize / 2) - guiLeft / 2, y2 = (guiTop + xSize / 2) + guiLeft / 2;
                double x2 = guiLeft, y3 = guiTop, y4 = guiTop + ySize;

                Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x, y}, new double[] {x2, y3}, new double [] {x2, y4}, new double[] {x, y2}), Color.BLACK, new Color(0, 0, 0, 0), false));
            }

            {
                double x = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth_double(), y = (guiTop + xSize / 2)  - guiLeft / 2, y2 = (guiTop + xSize / 2)  + guiLeft / 2;

                Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {guiLeft + xSize, guiTop}, new double[] {x, y}, new double [] {x, y2}, new double[] {guiLeft + xSize, guiTop + ySize}), new Color(0, 0, 0, 0), Color.BLACK, false));
            }
        }
    }
}
