package com.kisman.cc.newclickgui.component.modulecomponents.components;

import com.kisman.cc.module.Module;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class BindButton {
    private int x;
    private int y;
    private char chat;
    private int key;
    private boolean listen;
    private int offset;
    private Module parent;

    private String str = "";

    public BindButton(int x, int y, int offset, Module parent) {
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.parent = parent;
    }

    public void renderComponent() {
        CustomFontUtil.drawStringWithShadow(this.str, this.x, this.y + this.offset, this.listen ? new Color(255, 0, 0, 255).getRGB() : new Color(59, 59, 59, 255).getRGB());
    }

    public void updateComponent(int mouseX, int mouseY) {
        this.str = "[" + (this.listen ? "Press a key..." : Keyboard.getKeyName(this.parent.getKey())) + "]";
    }

    public void keyTyped(char typedChar, int keyCode) {
        if(listen) {
            this.parent.setKey(keyCode);
            this.listen = false;
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.listen = !this.listen;
        }
    }

    private boolean isMouseOnButton(int x, int y) {
        if(x > this.x - 1 && x < this.x + CustomFontUtil.getStringWidth(this.str) + 1 && y > this.y + this.offset - 1 && y < this.y + this.offset + CustomFontUtil.getFontHeight() + 1) return true;

        return false;
    }
}
