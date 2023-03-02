package com.kisman.cc.gui.halq.components.sub;

import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.SettingComponent;
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.util.TextUtilKt;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.client.collections.Bind;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends ShaderableImplementation implements Component, SettingComponent {
    private final Setting setting;
    private boolean dragging;

    private String customValue = "";
    private boolean typing = false;

    public Slider(Setting setting, int x, int y, int offset, int count, int layer) {
        super(x, y, count, offset, layer);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);

        double min = setting.getMin();
        double max = setting.getMax();

        if(typing) dragging = false;
        else customValue = "";

        if (dragging) {
            double diff = Math.min(getWidth(), Math.max(0, mouseX - getX()));
            if (diff == 0) setting.setValDouble(setting.getMin());
            else setting.setValDouble(roundToPlace(((diff / getWidth()) * (max - min) + min), 2));
        }

        String toRender = typing ? customValue + "_" : setting.getTitle() + ": " + setting.getNumberType().getFormatter().apply(setting.getValDouble());

        normalRender = () -> Render2DUtil.drawRectWH(getX(), getY(), getWidth(), HalqGui.height, HalqGui.backgroundColor.getRGB());

        int width = (int) (getWidth() * (setting.getValDouble() - min) / (max - min));

        Runnable shaderRunnable1 = () -> {
            if (HalqGui.shadow) {
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[]{getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                        new double[]{getX() + width - HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                        new double[]{getX() + width - HalqGui.offsetsX, getY() + HalqGui.height - HalqGui.offsetsY},
                                        new double[]{getX() + HalqGui.offsetsX, getY() + HalqGui.height - HalqGui.offsetsY}
                                ),
                                HalqGui.getGradientColour(getCount()).getColor(),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.minPrimaryAlpha.getValInt())
                        )
                );
            } else Render2DUtil.drawRectWH(getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY, width - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, HalqGui.getGradientColour(getCount()).getRGB());
        };

        Runnable shaderRunnable2 = () -> HalqGui.drawString(toRender, getX(), getY(), getWidth(), HalqGui.height);

        shaderRender = new Bind<>(shaderRunnable1, shaderRunnable2);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) dragging = true;
        if(isMouseOnButton(mouseX, mouseY) && button == 1) typing = true;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
    }

    @Override public void keyTyped(char typedChar, int key) {
        if(typing) {
            if(key == Keyboard.KEY_RETURN) {
                typing = false;
                if(!customValue.isEmpty()) setting.setValDouble(TextUtilKt.parseNumber(customValue, setting.getValDouble()));
                return;
            }

            if((key == 14 || key == Keyboard.KEY_DELETE) && !customValue.isEmpty()) {
                customValue = customValue.substring(0, customValue.length() - 1);
                return;
            }

            customValue += typedChar;
        }
    }
    public boolean visible() {return setting.isVisible() && HalqGui.visible(setting.getTitle());}

    private static double roundToPlace(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @NotNull
    @Override
    public Setting setting() {
        return setting;
    }
}
