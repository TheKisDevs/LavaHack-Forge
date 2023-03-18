package com.kisman.cc.gui.halq.components;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.catlua.module.ModuleScript;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.features.module.client.ViaForgeModule;
import com.kisman.cc.features.plugins.ModulePlugin;
import com.kisman.cc.features.viaforge.gui.ViaForgeGuiKt;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.ModuleComponent;
import com.kisman.cc.gui.api.Openable;
import com.kisman.cc.gui.api.ToggleableImplementation;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.components.sub.*;
import com.kisman.cc.gui.halq.components.sub.lua.LuaActionButton;
import com.kisman.cc.gui.halq.components.sub.plugins.PluginActionButton;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.gui.hudeditor.DraggableBox;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.client.annotations.FakeThing;
import com.kisman.cc.util.client.collections.Bind;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Button extends ToggleableImplementation implements Openable, ModuleComponent {
    public final ArrayList<Component> comps = new ArrayList<>();
    public final Module mod;
    public final DraggableBox draggable;
    public final Description description;
    public final boolean hud;
    public boolean open = false;

    public Button(Module mod, int x, int y, int offset, int count, int layer) {
        super(x, y, count, offset, layer);
        this.mod = mod;
        this.hud = mod instanceof HudModule;
        this.draggable = hud ? new DraggableBox((HudModule) mod) : null;
        this.description = new Description(mod.getDescription(), count);

        int offsetY = offset + HalqGui.height;
        int count1 = 0;

        if(mod instanceof ModuleScript) {
            comps.add(new LuaActionButton((ModuleScript) mod, LuaActionButton.Action.RELOAD, x, y, offsetY, count1++, layer + 1));
            offsetY += HalqGui.height;
            comps.add(new LuaActionButton((ModuleScript) mod, LuaActionButton.Action.UNLOAD, x, y, offsetY, count1++, layer + 1));
        } else if(mod instanceof ModulePlugin) {
            comps.add(new PluginActionButton((ModulePlugin) mod, PluginActionButton.Action.LOAD, x, y, offsetY, count1++, layer + 1));
            offsetY += HalqGui.height;
            comps.add(new PluginActionButton((ModulePlugin) mod, PluginActionButton.Action.UNLOAD, x, y, offsetY, count1++, layer + 1));
            offsetY += HalqGui.height;
            comps.add(new PluginActionButton((ModulePlugin) mod, PluginActionButton.Action.RELOAD, x, y, offsetY, count1++, layer + 1));
        } else {
            if(!mod.getClass().isAnnotationPresent(FakeThing.class) && mod.toggleable) {
                comps.add(new BindButton(mod, x, y, offsetY, count1++, layer + 1));
                offsetY += HalqGui.height;
                comps.add(new CheckBox(mod.visibleSetting, x, y, offsetY, count1++, layer + 1));
                offsetY += HalqGui.height;
                comps.add(new ModeButton(mod.bindModeSetting, x, y, offsetY, count1++, layer + 1));
                offsetY += HalqGui.height;
            }

            if (Kisman.instance.settingsManager.getSettingsByMod(mod) != null) {
                for (Setting set : Kisman.instance.settingsManager.getSettingsByMod(mod)) {
                    if (set == null || set.parent_ != null) continue;
                    if (set.isGroup() && set instanceof SettingGroup) {
                        comps.add(new GroupButton((SettingGroup) set, x, y, offsetY, count1++, layer + 1));
                        offsetY += HalqGui.height;
                    }
                    if (set.isSlider()) {
                        comps.add(new Slider(set, x, y, offsetY, count1++, layer + 1));
                        offsetY += HalqGui.height;
                    }
                    if (set.isCheck()) {
                        comps.add(new CheckBox(set, x, y, offsetY, count1++, layer + 1));
                        offsetY += HalqGui.height;
                    }
                    if (set.isBind()) {
                        comps.add(new BindButton(set, x, y, offsetY, count1++, layer + 1));
                        offsetY += HalqGui.height;
                    }
                    if (set.isCombo()) {
                        comps.add(new ModeButton(set, x, y, offsetY, count1++, layer + 1));
                        offsetY += HalqGui.height;
                    }
                    if (set.isColorPicker()) {
                        comps.add(new ColorButton(set, x, y, offsetY, count1++, layer + 1));
                        offsetY += HalqGui.height;
                    }
                }
            }
        }

        if(mod instanceof ViaForgeModule) ViaForgeGuiKt.component = this;

        if(!mod.submodules.isEmpty()) {
            for(Module submodule : mod.submodules) {
                comps.add(new Button(submodule, x, y, offsetY, count1++, layer + 1));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);

        if(hud && draggable != null) HalqGui.drawComponent(draggable);

        normalRender = () -> Render2DUtil.drawRectWH(getX(), getY(), getWidth(), HalqGui.height, HalqGui.backgroundColor.getRGB());

        Runnable shaderRunnable1 = () -> {
            if (HalqGui.shadow) {
                if (mod.isToggled()) {
                    Render2DUtil.drawAbstract(
                            new AbstractGradient(
                                    new Vec4d(
                                            new double[]{getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                            new double[]{getX() + getWidth() - HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                            new double[]{getX() + getWidth() - HalqGui.offsetsX, getY() + HalqGui.height - HalqGui.offsetsY},
                                            new double[]{getX() + HalqGui.offsetsX, getY() + HalqGui.height - HalqGui.offsetsY}
                                    ),
                                    ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.minPrimaryAlpha.getValInt()),
                                    HalqGui.getGradientColour(getCount()).getColor()
                            )
                    );
                }
            } else {
                if(HalqGui.test2) Render2DUtil.drawRectWH(getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY, getWidth() - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, HalqGui.test2Color.getRGB());

                drawRect(mod.isToggled());
            }
        };

        Runnable shaderRunnable2 = () -> {
            HalqGui.drawString(mod.displayName, getX(), getY(), getWidth(), HalqGui.height);

            if(HalqGui.openIndicator) HalqGui.drawStringRightSide(open ? "-" : "+", getX(), getY(), getWidth(), HalqGui.height);

            if (!HalqGui.hideAnnotations) {
                if (mod.isBeta()) HalqGui.drawSuffix("beta", mod.displayName, getX(), getY(), getWidth() - HalqGui.offsetsX, HalqGui.height, getCount(), 1);
                if (mod.isAddon()) HalqGui.drawSuffix("addon", mod.displayName, getX(), getY(), getWidth() - HalqGui.offsetsX, HalqGui.height, getCount(), 2);
            }

            if (Config.instance.guiShowBinds.getValBoolean() && mod.Companion.valid(mod)) HalqGui.drawSuffix(mod.Companion.getName(mod), mod.displayName, getX(), getY(), getWidth() - HalqGui.offsetsX, HalqGui.height, getCount(), 3);
        };

        shaderRender = new Bind<>(shaderRunnable1, shaderRunnable2);

        if(open && !comps.isEmpty()) {
            for(Component comp : comps) {
                if(!comp.visible()) continue;
                HalqGui.drawComponent(comp);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(hud && draggable != null) draggable.mouseClicked(mouseX, mouseY, button);
        if(isMouseOnButton(mouseX, mouseY) && button == 0 && mod.toggleable) mod.toggle();
        if(isMouseOnButton(mouseX, mouseY) && button == 1) open = !open;
        if(open && !comps.isEmpty()) for(Component comp : comps) if(comp.visible()) comp.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if(hud && draggable != null) draggable.mouseReleased(mouseX, mouseY, mouseButton);
        if(open && !comps.isEmpty()) for(Component comp : comps) if(comp.visible()) comp.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateComponent(int x, int y) {
        super.updateComponent(x, y);

        if(open && !comps.isEmpty()) for(Component comp : comps) if(comp.visible()) comp.updateComponent(x - LayerControllerKt.getXOffset(getLayer()) + LayerControllerKt.getXOffset(comp.getLayer()), y);
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(open && !comps.isEmpty()) for(Component comp : comps) if(comp.visible()) comp.keyTyped(typedChar, key);
    }

    @Override
    public int getHeight() {
        return HalqGui.height + getSize() * HalqGui.height;
    }

    public void setCount(int count) {
        super.setCount(count);
        description.setCount(count);
    }

    public int getSize() {
        int i = 0;
        for(Component comp : comps) if(comp.visible()) i++;
        return i;
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > getX() && x < getX() + getWidth() && y > getY() && y < getY() + HalqGui.height;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @NotNull
    @Override
    public ArrayList<Component> getComponents() {
        return comps;
    }

    @Override
    public boolean visible() {
        return HalqGui.visible(this) || HalqGui.visible(mod.displayName);
    }

    @NotNull
    @Override
    public Module module() {
        return mod;
    }

    @Nullable
    @Override
    public Description getDescription() {
        return description;
    }
}