package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.clickguiEvents.drawScreen.render.GuiRenderPostEvent;
import com.kisman.cc.event.events.clickguiEvents.mouseClicked.MouseClickedPreEvent;
import com.kisman.cc.event.events.clickguiEvents.mouseReleased.MouseReleasedPreEvent;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.settings.*;
import com.kisman.cc.util.LineMode;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.Vec2f;

import java.awt.*;

public class ColorPickerSimpleButton extends Component{
    public float PICKER_HEIGHT = 64;

    private Setting set;
    public Button button;

    private Vec2f selectorPosition;
    private float brightnessPosition;
    private float transparencyPosition;
    private final ColorHolder selectedColor;
    private Vec2f pos;

    public int offset;

    public boolean open;
    private boolean drag;

    public ColorPickerSimpleButton(Setting s, Button parent, int offset) {
        this.set = s;
        this.button = parent;
        this.offset = offset;

        float[] color = s.getColorHSB();
        this.selectedColor = new ColorHolder(color[0], color[1], color[2], color[3]);
        this.pos = new Vec2f(button.parent.getX(), button.parent.getY() + offset);

        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(listener2);
    }

    public void renderComponent() {
        Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + 3, button.parent.getY() + offset + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());

        if(ClickGui.getSetLineMode() == LineMode.SETTINGONLYSET || ClickGui.getSetLineMode() == LineMode.SETTINGALL) {
            Gui.drawRect(
                    button.parent.getX() + 88 - 3,
                    button.parent.getY() + offset,
                    button.parent.getX() + button.parent.getWidth() - 2,
                    button.parent.getY() + offset + 12,
                    new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB()
            );
        }

        Vec2f pos = new Vec2f(button.parent.getX(), button.parent.getY() + offset);


    }

    public void updateComponent(int mouseX, int mouseY) {
        pos = new Vec2f(button.parent.getX(), button.parent.getY() + offset);
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 1) {
            open = !open;
        }
    }

    @EventHandler
    private final Listener<GuiRenderPostEvent> listener = new Listener<>(event -> {
        if(event.gui.equals(GuiRenderPostEvent.Gui.OldGui)) {

        }
    });

    @EventHandler
    private final Listener<MouseClickedPreEvent> listener1 = new Listener<>(event -> {
        if(event.gui.equals(GuiRenderPostEvent.Gui.OldGui)) {

        }
    });

    @EventHandler
    private final Listener<MouseReleasedPreEvent> listener2 = new Listener<>(event -> {
        if(event.gui.equals(GuiRenderPostEvent.Gui.OldGui)) {
            drag = false;
        }
    });

    private boolean isMouseOnButton(int x, int y) {
        if(x >= pos.x && x <= pos.x + 98 && y >= pos.y && y <= pos.y + 14) return true;

        return false;
    }

    public static class ColorHolder {

        private float hue, saturation, brightness, transparency;

        public ColorHolder(float hue, float saturation, float brightness, float transparency) {
            this.hue = hue;
            this.saturation = saturation;
            this.brightness = brightness;
            this.transparency = transparency;
        }

        public float getHue() {
            return hue;
        }

        public void setHue(float in) {
            hue = in;
        }

        public float getSaturation() {
            return saturation;
        }

        public void setSaturation(float in) {
            saturation = in;
        }

        public float getBrightness() {
            return brightness;
        }

        public void setBrightness(float in) {
            brightness = in;
        }

        public float getTransparency() {
            return transparency;
        }

        public void setTransparency(float in) {
            transparency = in;
        }
    }
}
