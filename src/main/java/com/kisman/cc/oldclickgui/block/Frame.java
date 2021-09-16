package com.kisman.cc.oldclickgui.block;

import com.kisman.cc.oldclickgui.ClickGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;

public class Frame {
    public ArrayList<Component> components;

    public String name;

    public int x;
    public int y;
    public int heigth;
    public int width;
    public int barHeigth;

    public Frame(String name, int x, int y, int heigth, int width) {
        this.components = new ArrayList<>();

        this.name = name;

        this.x = x;
        this.y = y;
        this.heigth = heigth;
        this.width = width;
        this.barHeigth = 13;
    }

    public void renderFrame(FontRenderer fr) {
        GuiScreen.drawRect(x, y, x + width, y + heigth, new Color(ClickGui.getRBackground(), ClickGui.getGBackground(), ClickGui.getBBackground(), ClickGui.getABackground()).getRGB());
        fr.drawStringWithShadow(name, x + 5, y + ((heigth - fr.FONT_HEIGHT) / 2), new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
    }

    public void updatePosition(int mouseX, int mouseY) {

    }

    public void mouseCliced(int mouseX, int mouseY, int button) {

    }
}
