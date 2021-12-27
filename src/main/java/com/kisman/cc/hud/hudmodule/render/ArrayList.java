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
import com.kisman.cc.util.manager.Managers;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
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
import java.util.concurrent.atomic.AtomicInteger;

import static java.awt.Color.*;

public class ArrayList extends HudModule{
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
        int color = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : new Color(HUD.instance.arrColor.getR(), HUD.instance.arrColor.getG(), HUD.instance.arrColor.getB(), HUD.instance.arrColor.getA()).getRGB();
        float heigth = CustomFontUtil.getFontHeight() + 2;
        float[] hsb = Color.RGBtoHSB(ColorUtils.getRed(color), ColorUtils.getGreen(color), ColorUtils.getBlue(color), null);

        for(Module mod : mods) {
            if(mod != null && mod.isToggled() && mod.visible) {
                String name = mod.getName() + (mod.getDisplayInfo().equalsIgnoreCase("") ? "" : " " + TextFormatting.GRAY + mod.getDisplayInfo());

                switch (HUD.instance.arrGragient.getValString()) {
                    case "None": {
                        CustomFontUtil.drawStringWithShadow(name, (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name)), HUD.instance.arrY.getValDouble() + (heigth * count), color);
                        break;
                    }
                    case "Simple": {
                        CustomFontUtil.drawStringWithShadow(name, (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name)), HUD.instance.arrY.getValDouble() + (heigth * count), ColorUtils.rainbow(count * HUD.instance.arrGradientDiff.getValInt(), hsb[1], Managers.instance.pulseManager.getDifference(count * 2) / 255f).getRGB());
                        break;
                    }
                    case "Sideway": {
                        int update = (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name));

                        for(int i = 0; i < name.length(); ++i) {
                            String str = name.charAt(i) + "";

                            CustomFontUtil.drawStringWithShadow(str, update, HUD.instance.arrY.getValDouble() + (heigth * count), ColorUtils.rainbow(i * HUD.instance.arrGradientDiff.getValInt(), hsb[1], Managers.instance.pulseManager.getDifference(count * 2) / 255f).getRGB());

                            update += CustomFontUtil.getStringWidth(str);
                        }
                        break;
                    }
                }

                count++;
            }
        }
    }
}
