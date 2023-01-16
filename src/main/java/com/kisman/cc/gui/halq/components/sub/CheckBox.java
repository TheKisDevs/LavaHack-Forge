package com.kisman.cc.gui.halq.components.sub;

import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.Openable;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class CheckBox implements Openable {
    private final Setting setting;
    private int x, y, offset, count;
    private int width = HalqGui.width;
    private int layer;

    private final BindButton bind;
    private boolean open;

    public CheckBox(Setting setting, int x, int y, int offset, int count, int layer) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
        this.layer = layer;
        this.width = LayerControllerKt.getModifiedWidth(layer, width);
        this.bind = new BindButton(setting, x, y, offset + HalqGui.height, count, layer + 1);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Openable.super.drawScreen(mouseX, mouseY);
        Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());

        HalqGui.prepare();
        if(HalqGui.shadow) {
            if(setting.getValBoolean()) {
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
        } else if(HalqGui.test2 || setting.getValBoolean()) Render2DUtil.drawRectWH(x + HalqGui.offsetsX, y + offset + HalqGui.offsetsY, width - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, setting.getValBoolean() ? HalqGui.getGradientColour(count).getRGB() : HalqGui.backgroundColor.getRGB());
        HalqGui.release();

        if(Config.instance.guiShowBinds.getValBoolean() && setting.Companion.valid(setting)) {
            HalqGui.drawSuffix(
                    setting.Companion.getName(setting),
                    setting.getTitle(),
                    x,
                    y + offset,
                    width,
                    HalqGui.height,
                    count,
                    3
            );
        }

        HalqGui.drawString(setting.getTitle(), x, y + offset, width, HalqGui.height);

        if(open) bind.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            setting.setValBoolean(!setting.getValBoolean());
        }
        if(isMouseOnButton(mouseX, mouseY) && button == 1) open = !open;
        if(open) bind.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
        if(open) bind.updateComponent((x - LayerControllerKt.getXOffset(layer)) + LayerControllerKt.getXOffset(bind.getLayer()), y);
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(open) bind.keyTyped(typedChar, key);
    }

    @Override
    public int getHeight() {
        return HalqGui.height;// * (open ? 2 : 1);
    }

    public boolean visible() {return setting.isVisible() && HalqGui.visible(setting.getTitle());}

    public void setCount(int count) {this.count = count;}
    public int getCount() {return count;}
    public void setWidth(int width) {
        this.width = width;
        this.bind.setWidth(width - 10);
    }
    public void setX(int x) {this.x = x;}
    public int getX() {return x;}
    public void setLayer(int layer) {this.layer = layer;}
    public int getLayer() {return layer;}

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @NotNull
    @Override
    public ArrayList<Component> getComponents() {
        return new ArrayList<>(Collections.singletonList(bind));
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
