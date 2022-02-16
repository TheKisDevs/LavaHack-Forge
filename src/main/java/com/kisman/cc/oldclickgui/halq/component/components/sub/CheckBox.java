package com.kisman.cc.oldclickgui.halq.component.components.sub;

import com.kisman.cc.oldclickgui.halq.HalqGui;
import com.kisman.cc.oldclickgui.halq.component.Component;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

public class CheckBox extends Component {
    private final Setting setting;
    private int x, y, offset;

    public CheckBox(Setting setting, int x, int y, int offset) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.offset = offset;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(HalqGui.shadowCheckBox) {
            Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, HalqGui.backgroundColor.getRGB());
            if(setting.getValBoolean()) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + HalqGui.width / 2, y + offset}, new double[] {x + HalqGui.width, y + offset}, new double[] {x + HalqGui.width, y + offset + HalqGui.height}, new double[] {x + HalqGui.width / 2, y + offset + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1), HalqGui.primaryColor));
        } else Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, setting.getValBoolean() ? HalqGui.primaryColor.getRGB() : HalqGui.backgroundColor.getRGB());

        HalqGui.drawString(setting.getName(), x, y + offset, HalqGui.width, HalqGui.height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) setting.setValBoolean(!setting.getValBoolean());
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

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }
}
