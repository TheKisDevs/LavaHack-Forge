package com.kisman.cc.newclickgui.component.settingcomponents;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;
import com.kisman.cc.newclickgui.component.modulecomponents.ModuleButton;
import com.kisman.cc.newclickgui.component.settingcomponents.components.Slider;
import com.kisman.cc.settings.Setting;

import java.util.ArrayList;

public class SettingButton {
    private ArrayList<Component> components;

    private Module parent;
    private ModuleButton button;

    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    private int width;

    public SettingButton(int x, int y, int width, Module parent, ModuleButton mod) {
        this.x = x;
        this.y = y;
        this.width = width;

        this.parent = parent;

        this.components = new ArrayList<>();

        this.offsetY = 0;

        if(Kisman.instance.settingsManager.getSettingsByMod(this.parent) != null) {
            for (Setting s : Kisman.instance.settingsManager.getSettingsByMod(this.parent)) {
                if(s.isSlider()) {
                    this.components.add(new Slider(200, this.y, this.offsetY, this.width, 15, this, s, parent));
                    this.offsetY += 15;
                }
            }
        }
    }

    public void renderComponent() {
        for(Component comp : this.components) {
            comp.renderComponent();
        }
    }

    public void updateComponent(int mouseX, int mouseY) {
        for(Component comp : this.components) {
            comp.updateComponent(mouseX, mouseY);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        for(Component comp : this.components) {
            comp.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        for(Component comp : this.components) {
            comp.mouseReleased(mouseX, mouseY, button);
        }
    }
}
