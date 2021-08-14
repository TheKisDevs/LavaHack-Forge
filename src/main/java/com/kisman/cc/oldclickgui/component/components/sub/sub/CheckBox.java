package com.kisman.cc.oldclickgui.component.components.sub.sub;

import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.oldclickgui.component.components.Button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class CheckBox extends SubComponent{
    private String name;

    private int x;
    private int y;
    private int offset;
    
    private Setting op;
    private Button button;
    private ColorUtil colorUtil = new ColorUtil();
    private Minecraft mc = Minecraft.getMinecraft();
    private ScaledResolution sr = new ScaledResolution(mc);

    private boolean toggle;

    public CheckBox(Setting option, Button button, int offset) {//
        this.op = option;
        this.toggle = this.op.getValBoolean();
        this.button = button;
        this.offset = offset;
        this.x = sr.getScaledWidth() - 44;
        this.y = sr.getScaledHeight() - 6;
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(sr.getScaledWidth() - 44, sr.getScaledHeight() - 6 + offset, sr.getScaledWidth() + 44, sr.getScaledHeight() + 7 + offset, new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
        Gui.drawRect(x + 2, y + 2 + offset, x + 10, y + 10 + offset, ClickGui.isRainbowBackground() ? colorUtil.getColor() : new Color(ClickGui.getRBackground(), ClickGui.getGBackground(), ClickGui.getBBackground(), ClickGui.getABackground()).getRGB());
        if(toggle) {
            Gui.drawRect(x + 3, y + 3 + offset, x + 9, y + 9 + offset, -1);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            op.setValBoolean(true);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        if(x < this.x && x > this.x + 88 && y < this.y && y > this.y + 12) {
            return true;
        }
        return false;
    }
}
