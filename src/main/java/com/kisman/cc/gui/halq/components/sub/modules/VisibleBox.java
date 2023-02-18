package com.kisman.cc.gui.halq.components.sub.modules;

import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.ModuleComponent;
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.util.client.collections.Bind;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import org.jetbrains.annotations.NotNull;

public class VisibleBox extends ShaderableImplementation implements Component, ModuleComponent {
    private final Module module;

    public VisibleBox(Module module, int x, int y, int offset, int count, int layer) {
        super(x, y, count, offset, layer);
        this.module = module;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);

        normalRender = () -> Render2DUtil.drawRectWH(getX(), getY(), getWidth(), HalqGui.height, HalqGui.backgroundColor.getRGB());

        Runnable shaderRunnable1 = () -> {
            if(HalqGui.shadow) {
                if(module.visible) {
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
            } else if(HalqGui.test2 || module.visible) Render2DUtil.drawRectWH(getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY, getWidth() - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, module.visible ? HalqGui.getGradientColour(getCount()).getRGB() : HalqGui.backgroundColor.getRGB());
        };

        Runnable shaderRunnable2 = () -> HalqGui.drawString("Visible", getX(), getY(), getWidth(), HalqGui.height);

        shaderRender = new Bind<>(shaderRunnable1, shaderRunnable2);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) module.visible = !module.visible;
    }

    public boolean visible() {return true;}

    @NotNull
    @Override
    public Module module() {
        return module;
    }
}