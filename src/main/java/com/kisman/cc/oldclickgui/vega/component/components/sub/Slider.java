package com.kisman.cc.oldclickgui.vega.component.components.sub;

import com.kisman.cc.oldclickgui.vega.component.Component;
import com.kisman.cc.oldclickgui.vega.component.components.Button;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.Gui;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends Component {
    public Button b;
    public Setting s;
    public int offset;

    private int x, y;
    private int width, height;
    private double renderWidth;
    private boolean drag = false;
    private boolean hover = false;

    public Slider(Button b, Setting s, int offset) {
        this.b = b;
        this.s = s;
        this.offset = offset;

        this.x = b.parent.x;
        this.y = b.parent.y;
        this.width = b.parent.width;
        this.height = b.parent.height;
    }

    public void renderComponent() {
        Gui.drawRect(this.x -3, this.y + 3, (int)((double)this.x + (double)this.width + 3), this.y + this.height + 3, (ColorUtils.getColor(40, 40, 50)));
        Gui.drawRect(this.x - 2, this.y + 4, (int)((double)this.x + (double)this.width + 2), this.y + this.height + 1, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x - 1, this.y + 5, (int)((double)this.x + (double)this.width + 1), this.y + this.height, (ColorUtils.getColor(34, 34, 40)));
        Gui.drawRect(this.x - 1, this.y + 5, (int)((double)this.x + this.renderWidth + 1), this.y + this.height, (ColorUtils.getColor(24, 24, 30)));
        Gui.drawRect(this.x, this.y + 6, (int)((double)this.x + 3 + this.renderWidth - 3), this.y + this.height - 1, (ColorUtils.getColor(65, 65, 80)));
        Gui.drawRect(this.x, this.y + 7, (int)((double)this.x + 3 + this.renderWidth - 3), this.y + this.height - 2, (ColorUtils.getColor(80, 80, 95)));
        Gui.drawRect(this.x, this.y + 8, (int)((double)this.x + 3 + this.renderWidth - 3), this.y + this.height - 3, (ColorUtils.getColor(95, 95, 115)));

        CustomFontUtil.drawCenteredStringWithShadow(s.getName() + ": " + s.getValDouble(), x + (width / 2), y + ((height - CustomFontUtil.getFontHeight()) / 2), drag ? ColorUtils.astolfoColors(100, 100) : -1);
    }

    public void updateComponent(int mouseX, int mouseY) {
        this.x = b.parent.x;
        this.y = b.parent.y + offset;
        this.width = b.parent.width;
        this.height = b.parent.height;

        hover = isMouseOnButton(mouseX, mouseY);

        double diff = Math.min(88, Math.max(0, mouseX - this.x));

        double min = s.getMin();
        double max = s.getMax();

        renderWidth = (88) * (s.getValDouble() - min) / (max - min);

        if (drag) {
            if (diff == 0) {
                s.setValDouble(s.getMin());
            }
            else {
                double newValue = roundToPlace(((diff / 88) * (max - min) + min), 2);
                s.setValDouble(newValue);
            }
        }
    }

    public void newOff(int newOff) {
        offset = newOff;
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(button == 0) {
            if(isMouseOnButton(mouseX, mouseY)) {
                drag = true;
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        drag = false;
    }

    public boolean isMouseOnButtonD(int x, int y) {
        if(x > this.x && x < this.x + (b.parent.width / 2 + 1) && y > this.y && y < this.y + 12) {
            return true;
        }
        return false;
    }

    public boolean isMouseOnButtonI(int x, int y) {
        if(x > this.x + b.parent.width / 2 && x < this.x + b.parent.width && y > this.y && y < this.y + 12) {
            return true;
        }
        return false;
    }

    private boolean isMouseOnButton(int x, int y) {
        if(x > this.x && x < this.x + this.width && y > this.y + offset && y < this.y + this.height + this.offset) return true;

        return false;
    }

    private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
