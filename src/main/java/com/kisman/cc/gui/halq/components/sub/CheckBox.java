package com.kisman.cc.gui.halq.components.sub;

import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.Openable;
import com.kisman.cc.gui.api.SettingComponent;
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.client.collections.Bind;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class CheckBox extends ShaderableImplementation implements Openable, SettingComponent {
    private final Setting setting;

    private final BindButton bind;
    private boolean open;

    public CheckBox(Setting setting, int x, int y, int offset, int count, int layer) {
        super(x, y, count, offset, layer);
        this.setting = setting;
        this.bind = new BindButton(setting, x, y, offset + HalqGui.height, count, layer + 1);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);

        normalRender = () -> Render2DUtil.drawRectWH(getX(), getY(), getWidth(), HalqGui.height, HalqGui.backgroundColor.getRGB());

        Runnable shaderRunnable1 = () -> {
            if(HalqGui.shadow) {
                if(setting.getValBoolean()) {
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
            } else if(HalqGui.test2 || setting.getValBoolean()) Render2DUtil.drawRectWH(getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY, getWidth() - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, setting.getValBoolean() ? HalqGui.getGradientColour(getCount()).getRGB() : HalqGui.backgroundColor.getRGB());
        };

        Runnable shaderRunnable2 = () -> {
            if (Config.instance.guiShowBinds.getValBoolean() && setting.Companion.valid(setting)) {
                HalqGui.drawSuffix(
                        setting.Companion.getName(setting),
                        setting.getTitle(),
                        getX(),
                        getY(),
                        getWidth(),
                        HalqGui.height,
                        getCount(),
                        3
                );
            }

            HalqGui.drawString(setting.getTitle(), getX(), getY(), getWidth(), HalqGui.height);
        };

        shaderRender = new Bind<>(shaderRunnable1, shaderRunnable2);

        if(open)HalqGui.drawComponent(bind);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            setting.setValBoolean(!setting.getValBoolean());
        }
        if(isMouseOnButton(mouseX, mouseY) && button == 1) open = !open;
        if(open) bind.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void updateComponent(int x, int y) {
        super.updateComponent(x, y);

        if(open) bind.updateComponent((x - LayerControllerKt.getXOffset(getLayer())) + LayerControllerKt.getXOffset(bind.getLayer()), y);
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(open) bind.keyTyped(typedChar, key);
    }

    public boolean visible() {return setting.isVisible() && HalqGui.visible(setting.getTitle());}

    public void setWidth(int width) {
        super.setWidth(width);
        this.bind.setWidth(width - 10);
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @NotNull
    @Override
    public ArrayList<Component> getComponents() {
        return new ArrayList<>(Collections.singletonList(bind));
    }

    @NotNull
    @Override
    public Setting setting() {
        return setting;
    }
}
