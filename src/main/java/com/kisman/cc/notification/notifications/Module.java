package com.kisman.cc.notification.notifications;

import com.kisman.cc.notification.NotificationType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;

import java.awt.*;

public class Module extends GuiMainMenu {
    NotificationType notificationType;

    int x;
    int y;
    int width;
    int height;
    int offsetY;

    String title;

    public Module(NotificationType notificationType, int x, int y, int width, int height, int offsetY, String title) {
        this.notificationType = notificationType;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.offsetY = offsetY;
        this.title = title;
    }

    public void renderComponent(FontRenderer fontRenderer) {
        Gui.drawRect(x, y, x + width, y + height, new Color(43, 40, 40, 171).getRGB());
        Gui.drawRect(x, y, x + 10, y + height, new Color(0, 255, 0, 255).getRGB());
    }
}
