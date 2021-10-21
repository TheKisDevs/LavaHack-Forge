package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.settings.*;
import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;

import static java.awt.Color.*;

public class ArrayList extends HudModule{
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fr = mc.fontRenderer;

    public float[] color;

    Render2DUtil render2DUtil = new Render2DUtil();
    ColorUtil colorUtil = new ColorUtil();

    public ArrayList() {
        super("ArrayList", "arrList", HudCategory.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        java.util.ArrayList<Module> mods = new java.util.ArrayList<>();
        ScaledResolution sr = new ScaledResolution(mc);

        for(Module mod : Kisman.instance.moduleManager.modules) {
            if(mod != null && mod.isToggled() && mod.visible) {
                mods.add(mod);
            }
        }

        Comparator<Module> comparator = (first, second) -> {
            String firstName = first.getName() + (first.getDisplayInfo().equalsIgnoreCase("") ? "" : " " + TextFormatting.GRAY + first.getDisplayInfo());
            String secondName = second.getName() + (second.getDisplayInfo().equalsIgnoreCase("") ? "" : " " + TextFormatting.GRAY + second.getDisplayInfo());
            float dif = CustomFontUtil.getStringWidth(secondName) - CustomFontUtil.getStringWidth(firstName);
            return (dif != 0) ? ((int) dif) : secondName.compareTo(firstName);
        };

        mods.sort(comparator);

        int count = 0;
        float heigth = CustomFontUtil.getFontHeight() + 2;

        for(Module mod : mods) {
            if(mod != null && mod.isToggled() && mod.visible) {
                String name = mod.getName() + (mod.getDisplayInfo().equalsIgnoreCase("") ? "" : " " + TextFormatting.GRAY + mod.getDisplayInfo());

                CustomFontUtil.drawStringWithShadow(name, (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name)), HUD.instance.arrY.getValDouble() + (heigth * count), new Color(HUD.instance.arrColor.getR(), HUD.instance.arrColor.getG(), HUD.instance.arrColor.getB(), HUD.instance.arrColor.getA()).getRGB());

                count++;
            }
        }
    }
}
