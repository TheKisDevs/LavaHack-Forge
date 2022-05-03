package com.kisman.cc.gui.csgo.components;

import com.kisman.cc.module.client.Config;
import com.kisman.cc.gui.csgo.*;
import com.kisman.cc.util.Render2DUtil;

import java.util.Locale;
import java.util.function.Function;

public class Slider extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 24;

    private int preferredWidth;
    private int preferredHeight;
    private boolean hovered;

    private double value;
    private double minValue;
    private double maxValue;

    private NumberType numberType;

    private ValueChangeListener<Number> listener;

    private boolean changing = false;
    private boolean open = false;
    private final String values[] = new String[] {"Change", "Reset"};

    public Slider(IRenderer renderer, double value, double minValue, double maxValue, NumberType numberType, int preferredWidth, int preferredHeight) {
        super(renderer);
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.numberType = numberType;

        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;

        setWidth(preferredWidth);
        setHeight(preferredHeight);
    }

    public Slider(IRenderer renderer, double value, double minValue, double maxValue, NumberType numberType) {
        this(renderer, value, minValue, maxValue, numberType, PREFERRED_WIDTH, PREFERRED_HEIGHT);
    }

    @Override
    public void render() {
        renderer.drawRect(x, y, getWidth(), getHeight(), (hovered || changing) ? Window.SECONDARY_FOREGROUND : Window.TERTIARY_FOREGROUND);
        renderer.drawOutline(x, y, getWidth(), getHeight(), 1.0f, (hovered || changing) ? Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND);

        int sliderWidth = 4;
        double sliderPos = (value - minValue) / (maxValue - minValue) * (getWidth() - sliderWidth);

        renderer.drawRect(x + sliderPos, y + 2, sliderWidth, getHeight() - 3, (hovered || changing) ? Config.instance.guiAstolfo.getValBoolean() && hovered ? renderer.astolfoColorToObj() :  Window.TERTIARY_FOREGROUND : Window.SECONDARY_FOREGROUND);

        if(Config.instance.guiGlow.getValBoolean()) Render2DUtil.drawRoundedRect((x + sliderPos) / 2, (y + 2) / 2, (x + sliderPos + sliderWidth) / 2, (y + 2 + getHeight() - 3) / 2, (hovered || changing) ? Config.instance.guiAstolfo.getValBoolean() && hovered ? renderer.astolfoColorToObj() :  Window.TERTIARY_FOREGROUND : Window.SECONDARY_FOREGROUND, Config.instance.glowBoxSize.getValDouble());

        String text = numberType.getFormatter().apply(value);

        renderer.drawString(x + getWidth() / 2 - renderer.getStringWidth(text) / 2, y + getHeight() / 2 - renderer.getStringHeight(text) / 2, text, Window.FOREGROUND);
    }

    @Override public void postRender() {}

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        updateHovered(x, y, offscreen);
        updateValue(x, y);

        return changing;
    }

    private void updateValue(int x, int y) {
        if (changing) {
            double oldValue = value;
            double newValue = Math.max(Math.min((x - this.x) / (double) getWidth() * (maxValue - minValue) + minValue, maxValue), minValue);

            boolean change = true;

            if (oldValue != newValue && listener != null) change = listener.onValueChange(newValue);
            if (change) value = newValue;
        }
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + getWidth() && y <= this.y + getHeight();
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            updateHovered(x, y, offscreen);

            if (hovered) {
                changing = true;

                updateValue(x, y);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            updateHovered(x, y, offscreen);

            if (changing) {
                changing = false;
                updateValue(x, y);
                return true;
            }
        }

        return false;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setListener(ValueChangeListener<Number> listener) {
        this.listener = listener;
    }

    public enum NumberType {
        PERCENT(number -> String.format(Locale.ENGLISH, "%.1f%%", number.floatValue())),
        TIME(number -> Utils.Companion.formatTime(number.longValue())),
        DECIMAL(number -> String.format(Locale.ENGLISH, "%.4f", number.floatValue())),
        INTEGER(number -> Long.toString(number.longValue()));

        private Function<Number, String> formatter;

        NumberType(Function<Number, String> formatter) {
            this.formatter = formatter;
        }

        public Function<Number, String> getFormatter() {
            return formatter;
        }
    }
}