package com.kisman.cc.gui.halq.components.sub;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.settings.EventSettingChange;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.Openable;
import com.kisman.cc.gui.halq.util.LayerMap;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import com.kisman.cc.util.render.ColorUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class CheckBox implements Openable {
    private final Setting setting;
    private int x, y, offset, count;
    private int width = HalqGui.width;
    private int layer;

    private BindButton bind;
    private boolean open;

    public CheckBox(Setting setting, int x, int y, int offset, int count) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
        this.bind = new BindButton(setting, x, y, offset + HalqGui.height, count);
        this.bind.setLayer(2);
        this.bind.setWidth(80);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());
        if(HalqGui.shadowCheckBox) {
            if(setting.getValBoolean()) {
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[] {x + HalqGui.offsets, y + offset + HalqGui.offsets},
                                        new double[] {x + width - HalqGui.offsets, y + offset + HalqGui.offsets},
                                        new double[] {x + width - HalqGui.offsets, y + offset + HalqGui.height - HalqGui.offsets},
                                        new double[] {x + HalqGui.offsets, y + offset + HalqGui.height - HalqGui.offsets}
                                ),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.idkJustAlpha.getValInt()),
                                HalqGui.getGradientColour(count).getColor()
                        )
                );
            }
        } else if(HalqGui.test2 || setting.getValBoolean()) Render2DUtil.drawRectWH(x + HalqGui.offsets, y + offset + HalqGui.offsets, width - HalqGui.offsets * 2, HalqGui.height - HalqGui.offsets * 2, setting.getValBoolean() ? HalqGui.getGradientColour(count).getRGB() : HalqGui.backgroundColor.getRGB());

        HalqGui.drawString(setting.getName(), x, y + offset, width, HalqGui.height);

        if(open) bind.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            setting.setValBoolean(!setting.getValBoolean());
            Kisman.EVENT_BUS.post(new EventSettingChange.BooleanSetting(setting));
        }
        if(isMouseOnButton(mouseX, mouseY) && button == 1) open = !open;
        if(open) bind.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
        if(open) bind.updateComponent(x + LayerMap.getLayer(bind.getLayer()).modifier / 2, y);
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

    public boolean visible() {return setting.isVisible();}

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
}
