package com.kisman.cc.oldclickgui.vega.component.components.frame;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.vega.component.components.frame.sub.ModeButton;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;

public class Frame {
    public ArrayList<Component> buttons;

    public Module mod;

    public int width = 114, height = 13;
    public int x, y;
    public int dragX = 0, dragY = 0;
    public boolean dragging = false;
    public boolean open = true;

    public Frame(int x, int y, Module mod) {
        buttons = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.mod = mod;

        int offset = height;

        if(!Kisman.instance.settingsManager.getSettingsByMod(mod).isEmpty()) {
            for (Setting set : Kisman.instance.settingsManager.getSettingsByMod(mod)) {
                if(set.isCombo()) {
                    buttons.add(new ModeButton(this.x, this.y, offset, width, height, this, set));//
                    offset += height;
                }
            }
        }
    }

    public void renderComponent() {
        Gui.drawRect(this.x + 3, this.y + 3, this.x + this.width + 3, this.y + this.height - 3, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x + 3, this.y, this.x + this.width + 3, this.y + this.height, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x + 2, this.y + 2, this.x + this.width + 2, this.y + this.height - 2, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x + 2, this.y, this.x + this.width + 2, this.y + this.height, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x + 1, this.y + 1, this.x + this.width + 1, this.y + this.height - 1, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x + 1, this.y, this.x + this.width + 1, this.y + this.height, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x - 3, this.y - 8, this.x + this.width + 3, this.y + this.height - 3, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x - 3, this.y, this.x + this.width + 3, this.y + this.height, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x - 2, this.y - 7, this.x + this.width + 2, this.y + this.height - 2, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x - 2, this.y, this.x + this.width + 2, this.y + this.height, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x - 1, this.y - 6, this.x + this.width + 1, this.y + this.height - 1, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x - 1, this.y, this.x + this.width + 1, this.y + this.height, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x, this.y - 5, this.x + this.width, this.y + this.height, (ColorUtils.astolfoColors(100, 100)));
        Gui.drawRect(this.x - 3, this.y - 1, this.x + this.width + 3, this.y + this.height + 3, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x - 2, this.y - 2, this.x + this.width + 2, this.y + this.height + 2, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x - 1, this.y - 3, this.x + this.width + 1, this.y + this.height + 1, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x, this.y - 4, this.x + this.width, this.y + this.height, (ColorUtils.getColor(34, 34, 40)));

        CustomFontUtil.drawCenteredStringWithShadow(mod.getName(), x + (width / 2), y + ((height - CustomFontUtil.getFontHeight()) / 2), open ? ColorUtils.astolfoColors(100, 100) : -1);

        if(open && !buttons.isEmpty()) {
            for (Component button : buttons) {
                button.renderComponent();
            }
        }
    }

    public void updateComponent(int mouseX, int mouseY) {
        if(dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        if(x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height) return true;

        return false;
    }

    /*public void refresh() {
        int off = height;

        for(Component b : buttons) {
            b.offset = off;
            off += height;

            if(!b.open) continue;

            for(Component comp : b.comp) {
                comp.newOff(off);
                off += height;
            }
        }
    }*/
}
