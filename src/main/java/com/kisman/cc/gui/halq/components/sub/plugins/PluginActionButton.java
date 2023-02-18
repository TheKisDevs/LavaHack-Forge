package com.kisman.cc.gui.halq.components.sub.plugins;

import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.features.plugins.ModulePlugin;
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

/**
 * @author _kisman_
 * @since 20:07 of 09.06.2022
 */
public class PluginActionButton extends ShaderableImplementation implements Component, ModuleComponent {
    private final ModulePlugin plugin;
    private final Action action;

    public PluginActionButton(ModulePlugin plugin, Action action, int x, int y, int offset, int count, int layer) {
        super(x, y, count, offset, layer);
        this.plugin = plugin;
        this.action = action;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);

        normalRender = () -> Render2DUtil.drawRectWH(getX(), getY(), getWidth(), HalqGui.height, HalqGui.backgroundColor.getRGB());

        Runnable shaderRunnable1 = () -> {
            if(HalqGui.shadow) {
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[] {getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                        new double[] {getX() + getWidth() / 2.0 + HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                        new double[] {getX() + getWidth() / 2.0 + HalqGui.offsetsX, getY() + HalqGui.offsetsY + HalqGui.height},
                                        new double[] {getX() + HalqGui.offsetsX, getY() + HalqGui.height + HalqGui.offsetsY}
                                ),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.minPrimaryAlpha.getValInt()),
                                HalqGui.getGradientColour(getCount()).getColor()
                        )
                );
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[] {getX() + getWidth() / 2.0 + HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                        new double[] {getX() + getWidth() + HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                        new double[] {getX() + getWidth() + HalqGui.offsetsX, getY() + HalqGui.height + HalqGui.offsetsY},
                                        new double[] {getX() + getWidth() / 2.0 + HalqGui.offsetsX, getY() + HalqGui.height + HalqGui.offsetsY}
                                ),
                                HalqGui.getGradientColour(getCount()).getColor(),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.minPrimaryAlpha.getValInt())
                        )
                );
            } else Render2DUtil.drawRectWH(getX(), getY(), getWidth(), HalqGui.height, HalqGui.getGradientColour(getCount()).getRGB());
        };

        Runnable shaderRunnable2 = () -> HalqGui.drawString(action.name, getX(), getY(), getWidth(), HalqGui.height);

        shaderRender = new Bind<>(shaderRunnable1, shaderRunnable2);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            switch(action) {
                case LOAD:
                    plugin.load();
                    break;
                case UNLOAD:
                    plugin.unload();
                    break;
                case RELOAD:
                    plugin.reload();
                    break;
            }
        }
    }

    @Override
    public boolean visible() {
        return (plugin.getLoaded() == (action != Action.LOAD)) && HalqGui.visible(action.name);
    }

    public enum Action {
        LOAD("Load"),
        UNLOAD("Unload"),
        RELOAD("Reload");

        final String name;
        Action(String name) {this.name = name;}
    }

    @NotNull
    @Override
    public Module module() {
        return plugin;
    }
}
