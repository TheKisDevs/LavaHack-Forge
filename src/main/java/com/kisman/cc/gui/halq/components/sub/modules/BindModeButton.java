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

public class BindModeButton extends ShaderableImplementation implements Component, ModuleComponent {
    private final Module module;
    private int index;
    private final String[] values;
    private boolean open = false;

    public BindModeButton(Module module, int x, int y, int offset, int count, int layer) {
        super(x, y, count, offset, layer);
        this.module = module;
        this.values = new String[] {"Toggle", "Hold"};
        this.index = module.hold ? 1 : 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        this.index = module.hold ? 1 : 0;

        normalRender = () -> Render2DUtil.drawRectWH(getX(), getY(), getWidth(), getHeight(), HalqGui.backgroundColor.getRGB());

        Runnable shaderRunnable1 = () -> {
            if(HalqGui.shadow) {
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[] {getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                        new double[] {getX() + getWidth() / 2.0, getY() + HalqGui.offsetsY},
                                        new double[] {getX() + getWidth() / 2.0, getY() + HalqGui.height - HalqGui.offsetsY},
                                        new double[] {getX() + HalqGui.offsetsX, getY() + HalqGui.height - HalqGui.offsetsY}
                                ),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.minPrimaryAlpha.getValInt()),
                                HalqGui.getGradientColour(getCount()).getColor()
                        )
                );
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[] {getX() + getWidth() / 2.0, getY() + HalqGui.offsetsY},
                                        new double[] {getX() + getWidth() - HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                        new double[] {getX() + getWidth() - HalqGui.offsetsX, getY() + HalqGui.height - HalqGui.offsetsY},
                                        new double[] {getX() + getWidth() / 2.0, getY() + HalqGui.height - HalqGui.offsetsY}
                                ),
                                HalqGui.getGradientColour(getCount()).getColor(),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.minPrimaryAlpha.getValInt())
                        )
                );
            } else Render2DUtil.drawRectWH(getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY, getWidth() - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, HalqGui.getGradientColour(getCount()).getRGB());
        };

        Runnable shaderRunnable2 = () -> {
            HalqGui.drawString("Bind Mode: " + values[index], getX(), getY(), getWidth(), HalqGui.height);

            if (open) {
                int offsetY = HalqGui.height;
                for (int i = 0; i < values.length; i++) {
                    if (i == index) continue;
                    HalqGui.drawCenteredString(values[i], getX(), getY() + offsetY, getWidth(), HalqGui.height);
                    offsetY += HalqGui.height;
                }
            }
        };

        shaderRender = new Bind<>(shaderRunnable1, shaderRunnable2);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton2(mouseX, mouseY) && button == 0) open = !open;
        else if(isMouseOnButton(mouseX, mouseY) && button == 0 && open) {
            int offsetY = getY() + HalqGui.height;
            for(int i = 0; i < values.length; i++) {
                if(i == index) continue;

                if(mouseY >= offsetY && mouseY <= offsetY + HalqGui.height) {
                    index = i;
                    open = false;
                    module.hold = values[i].equals(values[1]);
                    break;
                }
                offsetY += HalqGui.height;
            }
        }
    }
    @Override
    public int getHeight() {
        return HalqGui.height + (open ? (values.length - 1) * HalqGui.height : 0);
    }

    @Override
    public boolean visible() {
        return true;
    }

    private boolean isMouseOnButton2(int x, int y) {
        return x > getX() && x < getX() + getWidth() && y > getY() && y < getY() + HalqGui.height;
    }

    @NotNull
    @Override
    public Module module() {
        return module;
    }
}
