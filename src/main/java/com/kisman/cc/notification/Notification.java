package com.kisman.cc.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;

import java.awt.Color;

public class Notification extends GuiMainMenu {
    private NotificationType type;
    private String title;
    private String messsage;

    private int x;
    private int y;
    private int width;
    private int height;
    private int offsetY;

    public Notification(NotificationType type, String title, String messsage, int length, int x, int y, int width, int height) {
        this.type = type;
        this.title = title;
        this.messsage = messsage;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render() {
        Color color = new Color(0, 0, 0, 220);
        Color color1;

        if (type == NotificationType.INFO)
            color1 = new Color(0, 26, 169);
        else if (type == NotificationType.WARNING)
            color1 = new Color(204, 193, 0);
        else {
            color1 = new Color(204, 0, 18);
        }

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        Gui.drawRect(x, y - offsetY, x + width, (y + height) - offsetY, color.getRGB());
        Gui.drawRect(x, y - offsetY, x + 5, (y + height) - offsetY, color1.getRGB());

        fontRenderer.drawString(title, x + 8, y + 3 + offsetY, -1);
        fontRenderer.drawString(messsage, x + 8,  (y + 8 + fontRenderer.FONT_HEIGHT) - offsetY, -1);
        offsetY -= height;
    }
}
