package com.kisman.cc.oldclickgui.halq.component.components.sub;

import com.kisman.cc.oldclickgui.halq.HalqGui;
import com.kisman.cc.oldclickgui.halq.component.Component;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

public class ModeButton extends Component {
    private final Setting setting;
    private int x, y, offset, index;
    private boolean open;
    private String[] values;

    public ModeButton(Setting setting, int x, int y, int offset) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.values = setting.getStringValues();
        this.index = setting.getSelectedIndex();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(HalqGui.shadowCheckBox) {
            Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, getHeight(), HalqGui.backgroundColor.getRGB());
            Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x, y + offset}, new double[] {x + HalqGui.width / 2, y + offset}, new double[] {x + HalqGui.width / 2, y + offset + HalqGui.height}, new double[] {x, y + offset + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1), HalqGui.primaryColor));
            Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + HalqGui.width / 2, y + offset}, new double[] {x + HalqGui.width, y + offset}, new double[] {x + HalqGui.width, y + offset + HalqGui.height}, new double[] {x + HalqGui.width / 2, y + offset + HalqGui.height}), HalqGui.primaryColor, ColorUtils.injectAlpha(HalqGui.backgroundColor, 1)));
        } else Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, getHeight(), setting.getValBoolean() ? HalqGui.primaryColor.getRGB() : HalqGui.backgroundColor.getRGB());

        HalqGui.drawString(setting.getName() + ": " + values[index], x, y + offset, HalqGui.width, HalqGui.height);

        if(open) {
            int offsetY = offset + HalqGui.height;
            for(int i = 0; i < values.length; i++) {
                if(i == index) continue;
                HalqGui.drawCenteredString(values[i], x, y + offsetY, HalqGui.width, HalqGui.height);
                offsetY += HalqGui.height;
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) open = !open;
        else if(isMouseOnButton2(mouseX, mouseY) && button == 0 && open) {
            int offsetY = y +  offset + HalqGui.height;
            for(int i = 0; i < values.length; i++) {
                if(i == index) continue;

                if(mouseY >= offsetY && mouseY <= offsetY + HalqGui.height) {
                    index = i;
                    open = false;
                    setting.setValString(values[i]);
                    break;
                }
                offsetY += HalqGui.height;
            }
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
        return HalqGui.height + (open ? (values.length - 1) * HalqGui.height : 0);
    }

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }

    private boolean isMouseOnButton2(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y + offset && y < this.y + offset + getHeight();
    }
}
