package com.kisman.cc.gui.halq.component.components.sub.lua;

import com.kisman.cc.catlua.module.ModuleScript;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.component.Component;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

public class ActionButton extends Component {
    private ModuleScript script;
    private Action action;
    private int x, y, count, offset;

    public ActionButton(ModuleScript script, Action action, int x, int y, int offset, int count) {
        this.script = script;
        this.action = action;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(HalqGui.shadowCheckBox) {
            Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, HalqGui.backgroundColor.getRGB());
            Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x, y + offset}, new double[] {x + HalqGui.width / 2, y + offset}, new double[] {x + HalqGui.width / 2, y + offset + HalqGui.height}, new double[] {x, y + offset + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1), HalqGui.getGradientColour(count).getColor()));
            Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + HalqGui.width / 2, y + offset}, new double[] {x + HalqGui.width, y + offset}, new double[] {x + HalqGui.width, y + offset + HalqGui.height}, new double[] {x + HalqGui.width / 2, y + offset + HalqGui.height}), HalqGui.getGradientColour(count).getColor(), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1)));
        } else Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, HalqGui.getGradientColour(count).getRGB());

        HalqGui.drawString(action.name, x, y + offset, HalqGui.width, HalqGui.height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            switch(action) {
                case RELOAD:
                    script.reload();
                    break;
                case UNLOAD:
                    script.unload(true);
                    break;
            }
        }
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int getHeight() {
        return HalqGui.height;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }

    public enum Action {
        RELOAD("Reload"),
        UNLOAD("Unload");

        String name;
        Action(String name) {this.name = name;}
    }
}
