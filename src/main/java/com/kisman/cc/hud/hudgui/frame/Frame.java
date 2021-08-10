package com.kisman.cc.hud.hudgui.frame;

import com.kisman.cc.hud.hudmodule.Logo;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class Frame {
    public Frame() {

    }

    public void renderFrame(FontRenderer fontRenderer) {
        Gui.drawRect(Logo.x, Logo.y, Logo.x1, Logo.y1, new Color(57, 52, 52, 124).getRGB());
    }

    public void updatePosition() {

    }
}
