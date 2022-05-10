package com.kisman.cc.gui.halq.component.components.sub;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.settings.EventSettingChange;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.component.Component;
import com.kisman.cc.gui.halq.util.LayerMap;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import com.kisman.cc.util.render.ColorUtils;

public class CheckBox extends Component {
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
        if(HalqGui.shadowCheckBox) {
            Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());
            if(setting.getValBoolean()) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + width / 2, y + offset}, new double[] {x + width, y + offset}, new double[] {x + width, y + offset + HalqGui.height}, new double[] {x + width / 2, y + offset + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1), HalqGui.getGradientColour(count).getColor()));
        } else Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, setting.getValBoolean() ? HalqGui.getGradientColour(count).getRGB() : HalqGui.backgroundColor.getRGB());

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
        this.bind.setOff(newOff + HalqGui.height);
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(open) bind.keyTyped(typedChar, key);
    }

    @Override
    public int getHeight() {
        return HalqGui.height * (open ? 2 : 1);
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
}
