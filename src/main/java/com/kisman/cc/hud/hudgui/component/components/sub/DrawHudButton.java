package com.kisman.cc.hud.hudgui.component.components.sub;

import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.settings.*;
import com.kisman.cc.util.Render2DUtil;

import net.minecraft.client.Minecraft;

import com.kisman.cc.oldclickgui.component.components.Button;

import java.awt.*;

public class DrawHudButton extends Component{ 
    private Setting set;
    private Button parent;

    private boolean drag = false;

    private int dragX, dragY;

    private int x1, y1, x2, y2;

    public DrawHudButton(Setting s, Button parent) {
        this.set = s;
        this.parent = parent;
        this.x1 = s.getX1();
        this.y1 = s.getY1();
        this.x2 = s.getX2();
        this.y2 = s.getY2();
        this.dragX = 0;
        this.dragY = 0;
    }

    public void renderComponent() {
        Render2DUtil.drawBox(x1 + dragX, y1 + dragY, x2 + dragX, y2 + dragY, 1, Color.BLACK);
        //Minecraft.getMinecraft().player.sendChatMessage("render");
    }

    public void updateComponent(int mouseX, int mouseY) {
        // if(drag) {
        //     this.x1 += mouseX - this.x1;
        //     this.y1 += mouseX - this.y1;
        //     this.x2 = s.getX2();
        //     this.y2 = s.getY2();
        //     this.dragX = 
        //     this.dragY = mouseY - this.y1;
        // }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.drag = true;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if(isMouseOnButton(mouseX, mouseY) && mouseButton == 0) {
            this.drag = false;
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        if(x > x1 && x < x2 && y > y1 && y < y2) {
            return true;
        }
        return false;
    }
}
