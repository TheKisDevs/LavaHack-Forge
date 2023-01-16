package com.kisman.cc.gui.halq.components.sub;

import com.kisman.cc.features.module.BindType;
import com.kisman.cc.features.module.IBindable;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import org.lwjgl.input.Keyboard;

import java.util.function.BooleanSupplier;

public class BindButton implements Component {
    private final IBindable bindable;
    private int x, y, offset, count;
    private boolean changing;
    private int width = HalqGui.width;
    private int layer;

    public BooleanSupplier visibleSupplier = null;

    public BindButton(IBindable bindable, int x, int y, int offset, int count, int layer) {
        this.bindable = bindable;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
        this.layer = layer;
        this.width = LayerControllerKt.getModifiedWidth(layer, width);
    }

    public BindButton setVisible(
            BooleanSupplier visible
    ) {
        this.visibleSupplier = visible;

        return this;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Component.super.drawScreen(mouseX, mouseY);
        Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());

        HalqGui.prepare();
        if(HalqGui.shadow) {
            if(changing) {
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[] {x + HalqGui.offsetsX, y + offset + HalqGui.offsetsY},
                                        new double[] {x + width - HalqGui.offsetsX, y + offset + HalqGui.offsetsY},
                                        new double[] {x + width - HalqGui.offsetsX, y + offset + HalqGui.height - HalqGui.offsetsY},
                                        new double[] {x + HalqGui.offsetsX, y + offset + HalqGui.height - HalqGui.offsetsY}
                                ),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.idkJustAlpha.getValInt()),
                                HalqGui.getGradientColour(count).getColor()
                        )
                );
            }
        } else if(HalqGui.test2 || changing) Render2DUtil.drawRectWH(x + HalqGui.offsetsX, y + offset + HalqGui.offsetsY, width - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, changing ? HalqGui.getGradientColour(count).getRGB() : HalqGui.backgroundColor.getRGB());
        HalqGui.release();

        HalqGui.drawString(changing ? "Press a key..." : bindable.getButtonName() + ": " + bindable.Companion.getName(bindable) , x, y + offset, width, HalqGui.height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) changing = !changing;
        if(isMouseOnButton(mouseX, mouseY) && button == 1) {
            changing = false;
            bindable.setType(BindType.Keyboard);
            bindable.setKeyboardKey(Keyboard.KEY_NONE);
            bindable.setMouseButton(-1);
        }

        if(button > 1 && changing) {
            changing = false;
            bindable.setType(BindType.Mouse);
            bindable.setMouseButton(button);
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(changing) {
            changing = false;
            bindable.setType(BindType.Keyboard);
            bindable.setKeyboardKey(key);
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
        return HalqGui.height;
    }

    public boolean visible() { return visibleSupplier != null ? visibleSupplier.getAsBoolean() : (!(bindable instanceof Setting) || (((Setting) bindable).isVisible() && HalqGui.visible(((Setting) bindable).getTitle()))); }

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

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getY() {
        return y + offset;
    }
}
