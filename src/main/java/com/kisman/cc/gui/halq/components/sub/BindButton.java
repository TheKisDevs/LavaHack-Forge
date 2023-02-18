package com.kisman.cc.gui.halq.components.sub;

import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.client.collections.Bind;
import com.kisman.cc.util.client.interfaces.IBindable;
import com.kisman.cc.util.enums.BindType;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import org.lwjgl.input.Keyboard;

import java.util.function.BooleanSupplier;

public class BindButton extends ShaderableImplementation implements Component {
    public final IBindable bindable;
    private boolean changing;

    public BooleanSupplier visibleSupplier = null;

    public BindButton(IBindable bindable, int x, int y, int offset, int count, int layer) {
        super(x, y, count, offset, layer);
        this.bindable = bindable;
    }

    public BindButton setVisible(
            BooleanSupplier visible
    ) {
        this.visibleSupplier = visible;

        return this;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);

        normalRender = () -> Render2DUtil.drawRectWH(getX(), getY(), getWidth(), HalqGui.height, HalqGui.backgroundColor.getRGB());

        Runnable shaderRunnable1 = () -> {
            if(HalqGui.shadow) {
                if(changing) {
                    Render2DUtil.drawAbstract(
                            new AbstractGradient(
                                    new Vec4d(
                                            new double[] {getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                            new double[] {getX() + getWidth() - HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                            new double[] {getX() + getWidth() - HalqGui.offsetsX, getY() + HalqGui.height - HalqGui.offsetsY},
                                            new double[] {getX() + HalqGui.offsetsX, getY() + HalqGui.height - HalqGui.offsetsY}
                                    ),
                                    ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.minPrimaryAlpha.getValInt()),
                                    HalqGui.getGradientColour(getCount()).getColor()
                            )
                    );
                }
            } else if(HalqGui.test2 || changing) Render2DUtil.drawRectWH(getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY, getWidth() - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, changing ? HalqGui.getGradientColour(getCount()).getRGB() : HalqGui.backgroundColor.getRGB());
        };

        Runnable shaderRunnable2 = () -> HalqGui.drawString(changing ? "Press a key..." : bindable.getButtonName() + ": " + bindable.Companion.getName(bindable) , getX(), getY(), getWidth(), HalqGui.height);

        shaderRender = new Bind<>(shaderRunnable1, shaderRunnable2);
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

    public boolean visible() { return visibleSupplier != null ? visibleSupplier.getAsBoolean() : (!(bindable instanceof Setting) || (((Setting) bindable).isVisible() && HalqGui.visible(((Setting) bindable).getTitle()))); }
}
