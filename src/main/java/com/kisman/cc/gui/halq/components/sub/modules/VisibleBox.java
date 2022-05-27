package com.kisman.cc.gui.halq.components.sub.modules;

import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.*;
import com.kisman.cc.util.render.ColorUtils;

public class VisibleBox implements Component {
    private final Module module;
    private int x, y, offset, count;
    private int width = HalqGui.width;
    private int layer;

    public VisibleBox(Module module, int x, int y, int offset, int count) {
        this.module = module;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());
        if(HalqGui.shadowCheckBox) {
            if(module.visible) {
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[] {x + HalqGui.offsets, y + offset + HalqGui.offsets},
                                        new double[] {x + width - HalqGui.offsets, y + offset + HalqGui.offsets},
                                        new double[] {x + width - HalqGui.offsets, y + offset + HalqGui.height - HalqGui.offsets},
                                        new double[] {x + HalqGui.offsets, y + offset + HalqGui.height - HalqGui.offsets}
                                ),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), 30),
                                HalqGui.getGradientColour(count).getColor()
                        )
                );
            }
        } else if(HalqGui.test2 || module.visible) Render2DUtil.drawRectWH(x + HalqGui.offsets, y + offset + HalqGui.offsets, width - HalqGui.offsets * 2, HalqGui.height - HalqGui.offsets * 2, module.visible ? HalqGui.getGradientColour(count).getRGB() : HalqGui.backgroundColor.getRGB());

        HalqGui.drawString("Visible", x, y + offset, width, HalqGui.height);
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
    public void setWidth(int width) {this.width = width;}
    public void setX(int x) {this.x = x;}
    public int getX() {return x;}
    public void setLayer(int layer) {this.layer = layer;}
    public int getLayer() {return layer;}

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }
}
