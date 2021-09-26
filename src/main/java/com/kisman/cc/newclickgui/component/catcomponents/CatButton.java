package com.kisman.cc.newclickgui.component.catcomponents;

import com.kisman.cc.util.customfont.CustomFontUtil;

import java.awt.*;

public class CatButton {
    private int x;
    private int y;
    private int offset;
    private int width;

    private String name;

    private boolean listen;

    public CatButton(int x, int y, int offset, int width, String title) {
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.width = width;

        this.name = title;
    }

    public void renderComponent() {
        CustomFontUtil.drawStringWithShadow(this.name, this.x, this.y + this.offset, this.listen ? new Color(255, 0, 0, 255).getRGB() : -1);
    }

    public void updateComponent(int mouseX, int mouseY) {

    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.listen = !this.listen;
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        if(x > this.x && x < this.x + this.width && y > this.y + this.offset && y < this.y + this.offset + CustomFontUtil.getFontHeight()) return true;

        return false;
    }

    public boolean isListen() {
        return this.listen;
    }

    public void setListen(boolean listen) {
        this.listen = listen;
    }
}
