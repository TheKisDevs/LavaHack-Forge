package com.kisman.cc.clickgui;

import com.kisman.cc.Kisman;
import com.kisman.cc.clickgui.components.Component;
import com.kisman.cc.module.Category;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;

public class Frame {
    public ArrayList<Component> components;
    public Category category;
    private boolean open;
    private int width;
    private int x;
    private int y;
    private int height;
    private boolean isDragging;
    public int dragX;
    public int dragY;

    public String frameName;

    public Frame(String frameName) {
        this.frameName = frameName;
        this.x = 1;
        this.y = 1;
        this.height = 800;
        this.width = 600;
        this.dragX = 0;
        this.open = false;
        this.isDragging = false;

        //for(Category cat : )
    }

    public void renderFrame(FontRenderer fontRenderer) {
        Kisman.LOGGER.info("newClickGui rendering #1");
        Gui.drawRect(1, 1, 1 + this.width, 1 + this.height, -1);//new Color(80, 75, 75, 150).getRGB()
        Kisman.LOGGER.info("newClickGui rendering #2");
    }
}
