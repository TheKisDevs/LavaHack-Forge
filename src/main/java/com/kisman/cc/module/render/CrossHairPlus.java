package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class CrossHairPlus extends Module {
    public static boolean crosshair = false;

    public CrossHairPlus() {
        super("CrossHair+", "", Category.RENDER);
    }

    public void onEnable() {
        crosshair = true;
    }

    public void onDisable() {
        crosshair = false;
    }
}
