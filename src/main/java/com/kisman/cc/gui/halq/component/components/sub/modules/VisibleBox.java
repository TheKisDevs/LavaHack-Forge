package com.kisman.cc.gui.halq.component.components.sub.modules;

import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.component.Component;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.*;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

public class VisibleBox extends Component {
    private final Module module;
    private int x, y, offset, count;

    public VisibleBox(Module module, int x, int y, int offset, int count) {
        this.module = module;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(HalqGui.shadowCheckBox) {
            Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, HalqGui.backgroundColor.getRGB());
            if(module.visible) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + HalqGui.width / 2, y + offset}, new double[] {x + HalqGui.width, y + offset}, new double[] {x + HalqGui.width, y + offset + HalqGui.height}, new double[] {x + HalqGui.width / 2, y + offset + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1), HalqGui.getGradientColour(count).getColor()));
        } else Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, module.visible ? HalqGui.getGradientColour(count).getRGB() : HalqGui.backgroundColor.getRGB());

        HalqGui.drawString("Visible", x, y + offset, HalqGui.width, HalqGui.height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) module.visible = !module.visible;
    }

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public int getHeight() {
        return HalqGui.height;
    }

    public boolean visible() {return true;}

    public void setCount(int count) {this.count = count;}
    public int getCount() {return count;}

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }
}
