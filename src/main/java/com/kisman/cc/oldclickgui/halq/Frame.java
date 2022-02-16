package com.kisman.cc.oldclickgui.halq;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.halq.component.Component;
import com.kisman.cc.oldclickgui.halq.component.components.Button;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

import java.util.ArrayList;

public class Frame {
    //vars
    public final ArrayList<Component> mods = new ArrayList<>();
    private final Category cat;
    public int x, y;

    //logic vars
    public boolean dragging, open = true;
    public int dragX, dragY;

    public Frame(Category cat, int x, int y) {
        int offsetY = HalqGui.height;

        for(Module mod : Kisman.instance.moduleManager.getModulesInCategory(cat)) {
            mods.add(new Button(mod, x, y, offsetY));
            offsetY += HalqGui.height;
        }

        this.cat = cat;
        this.x = x;
        this.y = y;
    }

    public void render(int mouseX, int mouseY) {
        if(dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }

        Render2DUtil.drawRectWH(x, y, HalqGui.width, HalqGui.height, HalqGui.primaryColor.getRGB());
        if(HalqGui.shadow) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x - HalqGui.headerOffset, y}, new double[] {x, y}, new double[] {x, y + HalqGui.height}, new double[] {x - HalqGui.headerOffset, y + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.primaryColor, 0), HalqGui.primaryColor));
        if(HalqGui.shadow) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + HalqGui.width, y}, new double[] {x + HalqGui.width + HalqGui.headerOffset, y}, new double[] {x + HalqGui.width + HalqGui.headerOffset, y + HalqGui.height}, new double[] {x + HalqGui.width, y + HalqGui.height}), HalqGui.primaryColor, ColorUtils.injectAlpha(HalqGui.primaryColor, 0)));

        HalqGui.drawString(cat.getName(), x, y, HalqGui.width, HalqGui.height);
    }

    public void renderPost() {
        if(open) {
            if(!HalqGui.line) return;
            int height = mods.size() * HalqGui.height;
            if(!mods.isEmpty()) for(Component comp : mods) if(comp instanceof Button && ((Button) comp).open) for(Component comp1 : ((Button) comp).comps) height += comp1.getHeight();
            Render2DUtil.drawRectWH(x, y + HalqGui.height, 1, height, HalqGui.primaryColor.getRGB());
            Render2DUtil.drawRectWH(x + HalqGui.width - 1, y + HalqGui.height, 1, height, HalqGui.primaryColor.getRGB());
        }
    }

    public void refresh() {
        int offsetY = HalqGui.height;

        for(Component comp : mods) {
            comp.setOff(offsetY);
            offsetY += HalqGui.height;
            if(comp instanceof Button) {
                Button button = (Button) comp;
                if(button.open) {
                    for (Component comp1 : button.comps) {
                        comp1.setOff(offsetY);
                        offsetY += comp1.getHeight();
                    }
                }
            }
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y && y < this.y + HalqGui.height;
    }
}
