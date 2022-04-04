package com.kisman.cc.gui.halq.component.components.sub;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.settings.EventSettingChange;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.component.Component;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends Component {
    private final Setting setting;
    private int x, y, offset, count;
    private boolean dragging;

    public Slider(Setting setting, int x, int y, int offset,  int count) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        double diff = Math.min(HalqGui.width, Math.max(0, mouseX - this.x));
        double min = setting.getMin();
        double max = setting.getMax();

        if (dragging) {
            if (diff == 0) setting.setValDouble(setting.getMin());
            else setting.setValDouble(roundToPlace(((diff / HalqGui.width) * (max - min) + min), 2));
        }

        Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, HalqGui.backgroundColor.getRGB());

        int width = (int) (HalqGui.width * (setting.getValDouble() - min) / (max - min));
        if(HalqGui.shadowCheckBox) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x, y + offset}, new double[] {x + width, y + offset}, new double[] {x + width, y + offset + HalqGui.height}, new double[] {x, y + offset + HalqGui.height}), HalqGui.getGradientColour(count).getColor(), ColorUtils.injectAlpha(HalqGui.backgroundColor, 4)));
        else Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.getGradientColour(count).getRGB());

        HalqGui.drawString(setting.getName() + ": " + setting.getNumberType().getFormatter().apply(setting.getValDouble()), x, y + offset, HalqGui.width, HalqGui.height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) dragging = true;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
        Kisman.EVENT_BUS.post(new EventSettingChange.NumberSetting(setting));
    }

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override public void keyTyped(char typedChar, int key) {super.keyTyped(typedChar, key);}
    @Override public void setOff(int newOff) {this.offset = newOff;}
    @Override public int getHeight() {return HalqGui.height;}
    public boolean visible() {return setting.isVisible();}
    public void setCount(int count) {this.count = count;}
    public int getCount() {return count;}

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }

    private static double roundToPlace(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
