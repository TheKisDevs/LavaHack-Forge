package com.kisman.cc.newclickgui.component.modulecomponents;

import com.kisman.cc.module.Module;
import com.kisman.cc.newclickgui.component.catcomponents.CatButton;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class ModuleButton {
    private int x;
    private int y;
    private int offset;

    private String name;
    private Module parent;
    private CatButton catParent;

    private boolean listen;

    private int mouseX;
    private int mouseY;

    private boolean toggle;

    private boolean hover;

    private Color b1 = new Color(0.3f, 0.3f, 0.3f, 0.8f);

    public ModuleButton(int x, int y, int offset, String name, Module parent, CatButton catParent) {
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.name = name;
        this.parent = parent;
        this.catParent = catParent;
    }

    public void renderComponent() {
        if(this.hover) GuiScreen.drawRect(this.x, this.y + this.offset, this.x + CustomFontUtil.getStringWidth(this.name) + 1, this.y + this.offset + 2 + CustomFontUtil.getFontHeight(), this.b1.getRGB());

        CustomFontUtil.drawStringWithShadow(this.name, this.x, this.y + this.offset, this.toggle ? new Color(255, 0, 0, 255).getRGB() : -1);
    }

    public void updateComponent(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        this.toggle = this.parent.isToggled();

        this.hover = isMouseOnButton(mouseX, mouseY);
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        System.out.println(this.x + " " + this.y + " " + mouseX + " " + mouseY + " " + this.mouseX + " " + this.mouseY);

        if(button == 0) {
            System.out.println("daun");
        }

        System.out.println("test555676");
        if(isMouseOnButton(Mouse.getX(), mouseY)) {
            System.out.println("lox");
        }

        if(isMouseOnButton(this.mouseX, this.mouseY) && button == 0) {
            System.out.println("yyyyyy");
            this.parent.setToggled(!this.parent.isToggled());
            return;
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        if(x > this.x - 1 && x < this.x + 100 && y > this.y + this.offset && y < this.y + this.offset + CustomFontUtil.getFontHeight()) return true;

        return false;
    }
}
