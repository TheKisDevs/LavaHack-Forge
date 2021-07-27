package com.kisman.cc.hud.hudmodule;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Collections;
import java.util.Comparator;

public class ArrayList extends GuiMainMenu {
    Minecraft mc = Minecraft.getMinecraft();
    ScaledResolution sr = new ScaledResolution(mc);
    FontRenderer fr = mc.fontRenderer;

    public static class ModuleComparator implements Comparator<Module> {
        @Override
        public int compare(Module arg0, Module arg1) {
            if(Minecraft.getMinecraft().fontRenderer.getStringWidth(arg0.getName()) > Minecraft.getMinecraft().fontRenderer.getStringWidth(arg1.getName())) {
                return -1;
            }
            if(Minecraft.getMinecraft().fontRenderer.getStringWidth(arg0.getName()) < Minecraft.getMinecraft().fontRenderer.getStringWidth(arg1.getName())) {
                return 1;
            }
            return 0;
        }
    }

    public ArrayList() {
        Collections.sort(Kisman.instance.moduleManager.modules,new ModuleComparator());

        int count = 0;
        for(Module m : Kisman.instance.moduleManager.modules) {
            if(!m.isToggled()) {
                continue;
            }
            int offset = count * (fr.FONT_HEIGHT + 6);
            Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.getName()) - 8, offset, sr.getScaledWidth(), 6 + fr.FONT_HEIGHT + offset, 0x9000000);
            fr.drawString(m.getName(), sr.getScaledWidth() - fr.getStringWidth(m.getName()) - 4, 4 + offset, -1);

            count++;
        }
    }
}
