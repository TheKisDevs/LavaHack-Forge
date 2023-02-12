package com.kisman.cc.gui.halq.components.sub;

import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.gui.halq.util.TextUtilKt;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.client.collections.Bind;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import org.lwjgl.input.Keyboard;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends ShaderableImplementation implements Component {
    private final Setting setting;
    private int x, y, offset, count;
    private boolean dragging;
    private int width = HalqGui.width;
    private int layer;

    private String customValue = "";
    private boolean typing = false;

    public Slider(Setting setting, int x, int y, int offset, int count, int layer) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
        this.layer = layer;
        this.width = LayerControllerKt.getModifiedWidth(layer, width);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);

        double min = setting.getMin();
        double max = setting.getMax();

        if(typing) dragging = false;
        else customValue = "";

        if (dragging) {
            double diff = Math.min(width, Math.max(0, mouseX - this.x));
            if (diff == 0) setting.setValDouble(setting.getMin());
            else setting.setValDouble(roundToPlace(((diff / width) * (max - min) + min), 2));
        }

        String toRender = typing ? customValue + "_" : setting.getTitle() + ": " + setting.getNumberType().getFormatter().apply(setting.getValDouble());

        normalRender = () -> Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());

        int width = (int) (this.width * (setting.getValDouble() - min) / (max - min));

        Runnable shaderRunnable1 = () -> {
            if (HalqGui.shadow) {
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[]{x + HalqGui.offsetsX, y + offset + HalqGui.offsetsY},
                                        new double[]{x + width - HalqGui.offsetsX, y + offset + HalqGui.offsetsY},
                                        new double[]{x + width - HalqGui.offsetsX, y + offset + HalqGui.height - HalqGui.offsetsY},
                                        new double[]{x + HalqGui.offsetsX, y + offset + HalqGui.height - HalqGui.offsetsY}
                                ),
                                HalqGui.getGradientColour(count).getColor(),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.minPrimaryAlpha.getValInt())
                        )
                );
            } else Render2DUtil.drawRectWH(x + HalqGui.offsetsX, y + offset + HalqGui.offsetsY, width - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, HalqGui.getGradientColour(count).getRGB());
        };

        Runnable shaderRunnable2 = () -> HalqGui.drawString(toRender, x, y + offset, this.width, HalqGui.height);

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

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override public void keyTyped(char typedChar, int key) {
        if(typing) {
            if(key == Keyboard.KEY_RETURN) {
                typing = false;
                if(!customValue.isEmpty()) setting.setValDouble(TextUtilKt.parseNumber(customValue, setting.getValDouble()));
                return;
            }

            if((key == 14 || key == Keyboard.KEY_DELETE) && !customValue.isEmpty() && TextUtilKt.parseNumber(customValue, setting.getValDouble()) != setting.getValDouble()) {
                customValue = customValue.substring(0, customValue.length() - 1);
                return;
            }

            customValue += typedChar;
        }
    }
    @Override public void setOff(int newOff) {this.offset = newOff;}
    @Override public int getHeight() {return HalqGui.height;}
    public boolean visible() {return setting.isVisible() && HalqGui.visible(setting.getTitle());}
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

    private static double roundToPlace(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getY() {
        return y + offset;
    }
}
