package com.kisman.cc.gui.halq.components.sub.modules;

import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.util.collections.Bind;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;

public class VisibleBox extends ShaderableImplementation implements Component {
    private final Module module;
    private int x, y, offset, count;
    private int width = HalqGui.width;
    private int layer;

    public VisibleBox(Module module, int x, int y, int offset, int count, int layer) {
        this.module = module;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
        this.layer = layer;
        this.width = LayerControllerKt.getModifiedWidth(layer, width);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);

        normalRender = () -> Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());

        Runnable shaderRunnable1 = () -> {
            if(HalqGui.shadow) {
                if(module.visible) {
                    Render2DUtil.drawAbstract(
                            new AbstractGradient(
                                    new Vec4d(
                                            new double[] {x + HalqGui.offsetsX, y + offset + HalqGui.offsetsY},
                                            new double[] {x + width - HalqGui.offsetsX, y + offset + HalqGui.offsetsY},
                                            new double[] {x + width - HalqGui.offsetsX, y + offset + HalqGui.height - HalqGui.offsetsY},
                                            new double[] {x + HalqGui.offsetsX, y + offset + HalqGui.height - HalqGui.offsetsY}
                                    ),
                                    ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.idkJustAlpha.getValInt()),
                                    HalqGui.getGradientColour(count).getColor()
                            )
                    );
                }
            } else if(HalqGui.test2 || module.visible) Render2DUtil.drawRectWH(x + HalqGui.offsetsX, y + offset + HalqGui.offsetsY, width - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, module.visible ? HalqGui.getGradientColour(count).getRGB() : HalqGui.backgroundColor.getRGB());
        };

        Runnable shaderRunnable2 = () -> HalqGui.drawString("Visible", x, y + offset, width, HalqGui.height);

        shaderRender = new Bind<>(shaderRunnable1, shaderRunnable2);
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
    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getY() {
        return y + offset;
    }
}
