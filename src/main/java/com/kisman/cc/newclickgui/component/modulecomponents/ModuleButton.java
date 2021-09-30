package com.kisman.cc.newclickgui.component.modulecomponents;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;
import com.kisman.cc.newclickgui.component.catcomponents.CatButton;
import com.kisman.cc.newclickgui.component.modulecomponents.components.BindButton;
import com.kisman.cc.newclickgui.component.settingcomponents.SettingButton;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

public class ModuleButton {
    private int x;
    private int y;
    private int offset;

    private String name;
    private Module parent;
    private CatButton catParent;
    private BindButton bind;
    private SettingButton set;
    private SettingButton listenSet;

    private int mouseX;
    private int mouseY;

    private boolean toggle;

    private boolean hover;

    private boolean listen;
    private boolean listenSetb;

    private Color b1 = new Color(0.3f, 0.3f, 0.3f, 0.8f);

    public ModuleButton(int x, int y, int offset, String name, Module parent, CatButton catParent) {
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.name = name;
        this.parent = parent;
        this.catParent = catParent;

        this.bind = new BindButton(this.x + CustomFontUtil.getStringWidth(this.name), this.y, this.offset, this.parent);

        this.set = new SettingButton(this.x, this.y, 200, this.parent, this);
    }

    public void renderComponent() {
        if(this.hover) GuiScreen.drawRect(this.x, this.y + this.offset, this.x + CustomFontUtil.getStringWidth(this.name) + 1, this.y + this.offset + 2 + CustomFontUtil.getFontHeight(), 0x252525);

        CustomFontUtil.drawStringWithShadow(this.name, this.x, this.y + this.offset, this.toggle ? 0x6156CB : 0x303030);

        this.bind.renderComponent();

        if(this.listen) {
            this.set.renderComponent();
        }
    }

    public void updateComponent(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        this.toggle = this.parent.isToggled();

        this.hover = isMouseOnButton(mouseX, mouseY);

        this.bind.updateComponent(mouseX, mouseY);

        if(this.listen) this.set.updateComponent(mouseX, mouseY);
    }

    public void keyTyped(char typedChar, int keyCode) {
        this.bind.keyTyped(typedChar, keyCode);
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(this.mouseX, this.mouseY) && button == 0) {
            this.parent.setToggled(!this.parent.isToggled());
            return;
        }

        if(isMouseOnButton(this.mouseX, this.mouseY) && button == 1) {
            this.listen = !this.listen;

            if(this.listen) this.catParent.setListenSet(this);

            return;
        }

        this.bind.mouseClicked(mouseX, mouseY, button);

        if(this.listen) this.set.mouseClicked(mouseX, mouseY, button);
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        if(this.listen) this.set.mouseReleased(mouseX, mouseY, button);
    }

    public boolean isMouseOnButton(int x, int y) {
        if(x > this.x - 1 && x < this.x + CustomFontUtil.getStringWidth(this.name) + 1 && y > this.y + this.offset && y < this.y + this.offset + CustomFontUtil.getFontHeight()) return true;

        return false;
    }

    public SettingButton getListenSet() {
        return listenSet;
    }

    public void setListenSet(SettingButton listenSet) {
        this.listenSet = listenSet;
    }

    public boolean isListen() {
        return this.listen;
    }

    public void setListen(boolean listen) {
        this.listen = listen;
    }
}
