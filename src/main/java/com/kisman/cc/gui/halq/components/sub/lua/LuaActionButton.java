package com.kisman.cc.gui.halq.components.sub.lua;

import com.kisman.cc.features.catlua.module.ModuleScript;
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

public class LuaActionButton extends ShaderableImplementation implements Component, ModuleComponent {
    private final ModuleScript script;
    private final Action action;

    public LuaActionButton(ModuleScript script, Action action, int x, int y, int offset, int count, int layer) {
        super(x, y, count, offset, layer);
        this.script = script;
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
                                        new double[] {getX(), getY()},
                                        new double[] {getX() + getWidth() / 2.0, getY()},
                                        new double[] {getX() + getWidth() / 2.0, getY() + HalqGui.height},
                                        new double[] {getX(), getY() + HalqGui.height}
                                ),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.minPrimaryAlpha.getValInt()),
                                HalqGui.getGradientColour(getCount()).getColor()
                        )
                );
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[] {getX() + getWidth() / 2.0, getY()},
                                        new double[] {getX() + getWidth(), getY()},
                                        new double[] {getX() + getWidth(), getY() + HalqGui.height},
                                        new double[] {getX() + getWidth() / 2.0, getY() + HalqGui.height}
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
                case RELOAD:
                    script.reload();
                    break;
                case UNLOAD:
                    script.unload(true);
                    break;
            }
        }
    }

    @Override
    public boolean visible() {
        return true;
    }

    @NotNull
    @Override
    public Module module() {
        return script;
    }

    public enum Action {
        RELOAD("Reload"),
        UNLOAD("Unload");

        final String name;
        Action(String name) {this.name = name;}
    }
}
