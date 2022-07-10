package com.kisman.cc.gui.halq.components.sub;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.settings.EventSettingChange;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import com.kisman.cc.util.render.ColorUtils;

public class ModeButton implements Component {
    private final Setting setting;
    private int x, y, offset, index, count;
    public boolean open;
    private final String[] values;
    private int width = HalqGui.width;
    private int layer;

    public ModeButton(Setting setting, int x, int y, int offset, int count) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.values = setting.getStringValues();
        this.index = setting.getSelectedIndex();
        this.count = count;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        this.index = setting.getSelectedIndex();
        Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());

        if(open) {
            Render2DUtil.drawRectWH(x + 5, y + HalqGui.height + offset, width - 10, getHeight() - HalqGui.height, HalqGui.backgroundColor.getRGB());
        }

        if(HalqGui.shadowCheckBox) {
            Render2DUtil.drawAbstract(
                    new AbstractGradient(
                        new Vec4d(
                                new double[] {x + HalqGui.offsets, y + offset + HalqGui.offsets},
                                new double[] {x + width / 2, y + offset + HalqGui.offsets},
                                new double[] {x + width / 2, y + offset + HalqGui.height - HalqGui.offsets},
                                new double[] {x + HalqGui.offsets, y + offset + HalqGui.height - HalqGui.offsets}
                        ),
                        ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.idkJustAlpha.getValInt()),
                        HalqGui.getGradientColour(count).getColor()
                    )
            );
            Render2DUtil.drawAbstract(
                    new AbstractGradient(
                            new Vec4d(
                                    new double[] {x + width / 2, y + offset + HalqGui.offsets},
                                    new double[] {x + width - HalqGui.offsets, y + offset + HalqGui.offsets},
                                    new double[] {x + width - HalqGui.offsets, y + offset + HalqGui.height - HalqGui.offsets},
                                    new double[] {x + width / 2, y + offset + HalqGui.height - HalqGui.offsets}
                            ),
                            HalqGui.getGradientColour(count).getColor(),
                            ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.idkJustAlpha.getValInt())
                    )
            );
        } else Render2DUtil.drawRectWH(x + HalqGui.offsets, y + offset + HalqGui.offsets, width - HalqGui.offsets * 2, HalqGui.height - HalqGui.offsets * 2, HalqGui.getGradientColour(count).getRGB());

        HalqGui.drawString(setting.getName() + ": " + values[index], x, y + offset, width, HalqGui.height);

        if(open) {
            int offsetY = offset + HalqGui.height;
            for(int i = 0; i < values.length; i++) {
                if(i == index) continue;
                HalqGui.drawCenteredString(values[i], x, y + offsetY, width, HalqGui.height);
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
                    setting.setIndex(i);
                    Kisman.EVENT_BUS.post(new EventSettingChange.ModeSetting(setting));
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

    public boolean visible() {return setting.isVisible();}

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

    private boolean isMouseOnButton2(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y + offset && y < this.y + offset + getHeight();
    }
}
