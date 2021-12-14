package com.kisman.cc.oldclickgui.vega.component.components.frame.sub;

import com.kisman.cc.event.events.clickguiEvents.drawScreen.render.GuiRenderPostEvent;
import com.kisman.cc.event.events.clickguiEvents.mouseClicked.MouseClickedPreEvent;
import com.kisman.cc.oldclickgui.vega.component.components.frame.Component;
import com.kisman.cc.oldclickgui.vega.component.components.frame.Frame;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.Gui;

public class ModeButton extends Component {
    public int x, y;
    public Frame parent;
    public Setting set;
    public boolean open;
    public int width, height;
    public int offset;
    private boolean hover;

    public ModeButton(int x, int y, int offset, int width, int height, Frame parent, Setting set) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.set = set;
        this.width = width;
        this.height = height;
        this.offset = offset;
    }

    @EventHandler
    private final Listener<GuiRenderPostEvent> listener1 = new Listener<>(event -> {
        Gui.drawRect(this.x - 3, this.y + 3 + offset, this.x + this.width + 3, this.y + this.height + 3 + offset, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x - 3, this.y + offset, this.x + this.width + 3, this.y + this.height + offset, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x - 2, this.y + 2 + offset, this.x + this.width + 2, this.y + this.height + 2 + offset, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x - 2, this.y + offset, this.x + this.width + 2, this.y + this.height + offset, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x - 1, this.y + 1 + offset, this.x + this.width + 1, this.y + this.height + 1 + offset, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x - 1, this.y + offset, this.x + this.width + 1, this.y + this.height + offset, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x, this.y + offset, this.x + this.width, this.y + this.height + offset, (ColorUtils.getColor(34, 34, 40)));

        CustomFontUtil.drawStringWithShadow(set.getName() + ": " + set.getValString(), x + 6, y + ((height - CustomFontUtil.getFontHeight()) / 2) + offset, hover ? ColorUtils.astolfoColors(100, 100) : -1);
    });

    public void updateComponent(int mouseX, int mouseY) {
        this.x = parent.x;
        this.y = parent.y;
        hover = isMouseOnButton(mouseX, mouseY);
    }

    @EventHandler
    private final Listener<MouseClickedPreEvent> listener = new Listener<>(event -> {
        if(isMouseOnButton(event.mouseX, event.mouseY) && event.mouseButton == 0) {
            if(set.getOptions() != null) {
                int maxIndex = set.getOptions().size();
                int modeIndex = 0;

                if(modeIndex++ > maxIndex) {
                    modeIndex = 0;
                }

                set.setValString(set.getOptions().get(modeIndex));
            } else if(set.getOptionEnum() != null) {
                Enum nextSettingVal = set.getNextModeEnum();

                set.setValString(nextSettingVal.name());
                set.setValEnum(nextSettingVal);
            }
        }

        if(isMouseOnButton(event.mouseX, event.mouseY) && event.mouseButton == 1) {
            if(set.getOptions() != null) {
                int maxIndex = set.getOptions().size();
                int modeIndex = 0;

                if(modeIndex-- < 0) {
                    modeIndex = maxIndex;
                }

                set.setValString(set.getOptions().get(modeIndex));
            } else if(set.getOptionEnum() != null) {
                Enum nextSettingVal = set.getNextModeEnum();

                set.setValString(nextSettingVal.name());
                set.setValEnum(nextSettingVal);
            }
        }
    });

    private boolean isMouseOnButton(int x, int y) {
        if(x >= this.x && x <= this.x + width && y >= this.y + offset && y <= this.y + offset + height) return true;

        return false;
    }
}

