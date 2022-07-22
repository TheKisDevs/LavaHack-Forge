package com.kisman.cc.gui.halq.components;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.catlua.module.ModuleScript;
import com.kisman.cc.features.plugins.ModulePlugin;
import com.kisman.cc.gui.api.Openable;
import com.kisman.cc.gui.halq.components.sub.hud.DraggableBox;
import com.kisman.cc.gui.halq.components.sub.lua.LuaActionButton;
import com.kisman.cc.gui.halq.components.sub.modules.BindModeButton;
import com.kisman.cc.gui.halq.components.sub.modules.VisibleBox;
import com.kisman.cc.gui.halq.components.sub.*;
import com.kisman.cc.gui.halq.components.sub.plugins.PluginActionButton;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import com.kisman.cc.util.render.ColorUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

@SuppressWarnings("UnusedAssignment")
public class Button implements Component {
    public final ArrayList<Component> comps = new ArrayList<>();
    public final Module mod;
    public final DraggableBox draggable;
    public final Description description;
    public final boolean hud;
    public int x, y, offset, count;
    public boolean open = false;

    public Button(Module mod, int x, int y, int offset, int count) {
        this.mod = mod;
        this.hud = (mod instanceof HudModule);
        this.draggable = (hud ? new DraggableBox((HudModule) mod) : null);
        this.description = new Description(mod.getDescription(), count);
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;

        int offsetY = offset + HalqGui.height;
        int count1 = 0;

        if(mod instanceof ModuleScript) {
            comps.add(new LuaActionButton((ModuleScript) mod, LuaActionButton.Action.RELOAD, x, y, offsetY, count1++, 1));
            offsetY += HalqGui.height;
            comps.add(new LuaActionButton((ModuleScript) mod, LuaActionButton.Action.UNLOAD, x, y, offsetY, count1++, 1));
        } else if(mod instanceof ModulePlugin) {
            comps.add(new PluginActionButton((ModulePlugin) mod, PluginActionButton.Action.LOAD, x, y, offsetY, count1++, 1));
            offsetY += HalqGui.height;
            comps.add(new PluginActionButton((ModulePlugin) mod, PluginActionButton.Action.UNLOAD, x, y, offsetY, count1++, 1));
            offsetY += HalqGui.height;
            comps.add(new PluginActionButton((ModulePlugin) mod, PluginActionButton.Action.RELOAD, x, y, offsetY, count1++, 1));
        } else {
            comps.add(new BindButton(mod, x, y, offsetY, count1++, 1));
            offsetY += HalqGui.height;
            comps.add(new VisibleBox(mod, x, y, offsetY, count1++, 1));
            offsetY += HalqGui.height;
            comps.add(new BindModeButton(mod, x, y, offsetY, count1++, 1));
            offsetY += HalqGui.height;

            if (Kisman.instance.settingsManager.getSettingsByMod(mod) != null) {
                for (Setting set : Kisman.instance.settingsManager.getSettingsByMod(mod)) {
                    if(mod.getName().equalsIgnoreCase("charmsrewrite")) {
                        System.out.println("Setting in gui " + set.getName() + "{" + set.getTitle() + "}");
                    }
                    if (set == null || set.parent_ != null) continue;
                    if(mod.getName().equalsIgnoreCase("charmsrewrite")) {
                        System.out.println("Setting in gui ^ (post)");
                    }
                    if (set.isGroup() && set instanceof SettingGroup) {
                        comps.add(new GroupButton((SettingGroup) set, x, y, offsetY, count1++, 1));
                        offsetY += HalqGui.height;
                    }
                    if (set.isSlider()) {
                        comps.add(new Slider(set, x, y, offsetY, count1++, 1));
                        offsetY += HalqGui.height;
                    }
                    if (set.isCheck()) {
                        comps.add(new CheckBox(set, x, y, offsetY, count1++, 1));
                        offsetY += HalqGui.height;
                    }
                    if (set.isBind()) {
                        comps.add(new BindButton(set, x, y, offsetY, count1++, 1));
                        offsetY += HalqGui.height;
                    }
                    if (set.isCombo()) {
                        comps.add(new ModeButton(set, x, y, offsetY, count1++, 1));
                        offsetY += HalqGui.height;
                    }
                    if (set.isColorPicker()) {
                        comps.add(new ColorButton(set, x, y, offsetY, count1++, 1));
                        offsetY += HalqGui.height;
                    }
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(hud && draggable != null) draggable.drawScreen(mouseX, mouseY);

        Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, HalqGui.backgroundColor.getRGB());
        if(HalqGui.shadowCheckBox) {
            if(mod.isToggled()) {
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[] {x + HalqGui.offsets, y + offset + HalqGui.offsets},
                                        new double[] {x + HalqGui.width - HalqGui.offsets, y + offset + HalqGui.offsets},
                                        new double[] {x + HalqGui.width - HalqGui.offsets, y + offset + HalqGui.height - HalqGui.offsets},
                                        new double[] {x + HalqGui.offsets, y + offset + HalqGui.height - HalqGui.offsets}
                                ),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), 30),
                                HalqGui.getGradientColour(count).getColor()
                        )
                );
            }
        } else if(HalqGui.test2 || mod.isToggled()) Render2DUtil.drawRectWH(x + HalqGui.offsets, y + offset + HalqGui.offsets, HalqGui.width - HalqGui.offsets * 2, HalqGui.height - HalqGui.offsets * 2, mod.isToggled() ? HalqGui.getGradientColour(count).getRGB() : HalqGui.backgroundColor.getRGB());

        HalqGui.drawString(mod.getName(), x, y + offset, HalqGui.width, HalqGui.height);

        if(mod.isBeta()) {
            GL11.glPushMatrix();
            GL11.glScaled(0.5, 0.5, 1);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("beta", (x + CustomFontUtil.getStringWidth(mod.getName()) + 5) * 2, (y + offset) * 2, HalqGui.getGradientColour(count).getRGB());
            GL11.glPopMatrix();
        }

        if(Config.instance.guiShowBinds.getValBoolean() && mod.Companion.valid(mod)) {
            GL11.glPushMatrix();
            GL11.glScaled(0.5, 0.5, 1);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(mod.Companion.getName(mod), (x + CustomFontUtil.getStringWidth(mod.getName()) + 5) * 2, (y + offset + HalqGui.height - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f)) * 2, HalqGui.getGradientColour(count).getRGB());
            GL11.glPopMatrix();
        }

         if(open && !comps.isEmpty()) {
             for(Component comp : comps) {
                 boolean flag = false;
                 if(comp instanceof GroupButton) {
                     flag = true;
                     GroupButton group = (GroupButton) comp;
                     System.out.println("Render Group " + group.getSetting().getName() + "{" + group.getSetting().getTitle() + "}");
                 }
                 if(!comp.visible()) continue;
                 comp.drawScreen(mouseX, mouseY);
                 if(flag) {
                     System.out.println("Render Group ^ (post)");
                 }
             }
             if(HalqGui.test) {
                 int height = doIterationUpdateComponent(comps, 0);
                 Render2DUtil.drawRectWH(x, y + offset + HalqGui.height, HalqGui.width, 1, HalqGui.getGradientColour(count).getRGB());
                 Render2DUtil.drawRectWH(x, y + offset + HalqGui.height + height - 1, HalqGui.width, 1, HalqGui.getGradientColour(getLastColorCount()).getRGB());
             }
         }
    }

    private int doIterationUpdateComponent(ArrayList<Component> components, int height) {
        for(Component component : components) {
            if(!component.visible()) continue;
            height += component.getHeight();
            if(component instanceof Openable) {
                Openable openable = (Openable) component;
                if(openable.isOpen()) height = doIterationUpdateComponent(openable.getComponents(), height);
            }
        }

        return height;
    }

    private int getLastColorCount() {
        return comps.get(comps.size() - 1).getCount();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(hud && draggable != null) draggable.mouseClicked(mouseX, mouseY, button);
        if(isMouseOnButton(mouseX, mouseY) && button == 0) mod.toggle();
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
        this.x = x;
        this.y = y;
        if(open && !comps.isEmpty()) for(Component comp : comps) if(comp.visible()) comp.updateComponent(x + LayerControllerKt.getXOffset(comp.getLayer()), y);
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(open && !comps.isEmpty()) for(Component comp : comps) if(comp.visible()) comp.keyTyped(typedChar, key);
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public int getHeight() {
        return HalqGui.height + getSize() * HalqGui.height;
    }

    public void setCount(int count) {
        this.count = count;
        description.setCount(count);
    }

    public int getCount() {return count;}

    public int getSize() {
        int i = 0;
        for(Component comp : comps) if(comp.visible()) i++;
        return i;
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y + offset && y < this.y + HalqGui.height + offset;
    }
}
