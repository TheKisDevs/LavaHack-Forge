package com.kisman.cc.gui.halq.components.sub;

import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import com.kisman.cc.util.render.ColorUtils;
import org.lwjgl.input.Keyboard;

public class BindButton implements Component {
    private final Setting setting;
    private final Module module;
    private int x, y, offset, count;
    private boolean changing;
    private int width = HalqGui.width;
    private int layer;

    public BindButton(Setting setting, int x, int y, int offset, int count) {
        this.setting = setting;
        this.module = null;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
    }

    public BindButton(Module module, int x, int y, int offset, int count) {
        this.setting = null;
        this.module = module;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(setting == null && module == null) return;

        Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());
        if(HalqGui.shadowCheckBox) {
            if(changing) {
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
        } else if(HalqGui.test2 || changing) Render2DUtil.drawRectWH(x + HalqGui.offsets, y + offset + HalqGui.offsets, width - HalqGui.offsets * 2, HalqGui.height - HalqGui.offsets * 2, changing ? HalqGui.getGradientColour(count).getRGB() : HalqGui.backgroundColor.getRGB());

        HalqGui.drawString(changing ? "Press a key..." : module != null ? "Bind: " + Keyboard.getKeyName(module.getKey()) : setting.getName() + ": " + Keyboard.getKeyName(setting.getKey()) , x, y + offset, width, HalqGui.height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) changing = !changing;
        if(isMouseOnButton(mouseX, mouseY) && button == 1) {
            changing = false;
            if(module != null) module.setKey(Keyboard.KEY_NONE);
            else setting.setKey(Keyboard.KEY_NONE);
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(changing) {
            if(module == null && setting == null) return;
            if(module != null) module.setKey(key);
            else setting.setKey(key);
            changing = false;
        }
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

    public boolean visible() {return setting == null || setting.isVisible();}

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
