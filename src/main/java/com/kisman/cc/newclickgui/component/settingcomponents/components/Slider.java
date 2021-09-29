package com.kisman.cc.newclickgui.component.settingcomponents.components;

import com.kisman.cc.module.Module;
import com.kisman.cc.newclickgui.component.settingcomponents.Component;
import com.kisman.cc.newclickgui.component.settingcomponents.SettingButton;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.gui.GuiScreen;

public class Slider extends Component {
    private int x;
    private int y;
    private int width;
    private int heigth;
    private int offset;

    private SettingButton button;
    private Setting set;
    private Module mod;

    private boolean dragging;

    public Slider(int x, int y, int offset, int width, int heigth, SettingButton button, Setting set, Module mod) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.heigth = heigth;
        this.offset = offset;
        this.button = button;
        this.set = set;
        this.mod = mod;
    }

    public void renderComponent() {
        GuiScreen.drawRect(this.x, this.y + this.offset, this.x + this.width, this.y + this.offset + this.heigth, -1);
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButtonD(mouseX, mouseY) && button == 0) {
            this.dragging = true;
        }
        if(isMouseOnButtonI(mouseX, mouseY) && button == 0) {
            this.dragging = true;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {

    }

    public boolean isMouseOnButtonD(int x, int y) {
        if(x > this.x && x < this.x + (200 / 2 + 1) && y > this.y && y < this.y + 12) {
            return true;
        }
        return false;
    }

    public boolean isMouseOnButtonI(int x, int y) {
        if(x > this.x + 200 / 2 && x < this.x + 200 && y > this.y && y < this.y + 12) {
            return true;
        }
        return false;
    }
}
