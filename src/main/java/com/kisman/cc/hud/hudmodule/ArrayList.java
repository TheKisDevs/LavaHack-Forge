package com.kisman.cc.hud.hudmodule;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.module.render.CrossHairPlus;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.util.DrawLineMode;
import com.kisman.cc.util.Render2DUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;

public class ArrayList extends Gui {
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fr = mc.fontRenderer;

    Render2DUtil render2DUtil = new Render2DUtil();
    ColorUtil colorUtil = new ColorUtil();

    public static class ModuleComparator implements Comparator<Module>{
        @Override
        public int compare(Module arg0, Module arg1){
            if(Minecraft.getMinecraft().fontRenderer.getStringWidth(arg0.getName()) > Minecraft.getMinecraft().fontRenderer.getStringWidth(arg1.getName())){
                return -1;
            }
            if(Minecraft.getMinecraft().fontRenderer.getStringWidth(arg0.getName()) > Minecraft.getMinecraft().fontRenderer.getStringWidth(arg1.getName())) {
                return 1;
            }
            return 0;
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        Collections.sort(Kisman.instance.moduleManager.modules, new ModuleComparator());
        ScaledResolution sr = new ScaledResolution(mc);

        int count = 0;
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT && HUD.isArrList == true) {
            for (Module m : Kisman.instance.moduleManager.getModuleList()) {
                int offset = count * (fr.FONT_HEIGHT + 6);

                if (m.isToggled() && m.visible == true) {
                    fr.drawStringWithShadow(m.getName(), sr.getScaledWidth() - fr.getStringWidth(m.getName()) - 4, 4 + offset, -1);
                    Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.getName()) - 8, offset, sr.getScaledWidth() - fr.getStringWidth(m.getName()) - 7, offset + fr.FONT_HEIGHT + 6, ClickGui.isRainbowLine() ? colorUtil.getColor() : new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
                    count++;
                } else if(m.visible == false){
                    continue;
                }
            }
        }
    }
}
