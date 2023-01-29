package com.kisman.cc.gui.halq;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.plugins.ModulePlugin;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.Openable;
import com.kisman.cc.gui.halq.components.Button;
import com.kisman.cc.gui.halq.components.Header;
import com.kisman.cc.util.client.annotations.FakeThing;

import java.util.ArrayList;

public class Frame {
    //vars
    public final ArrayList<Component> components = new ArrayList<>();
    public final Category cat;
    public final boolean customName;
    public final String name;
    public int x, y, count = 0;
    public boolean reloading = false;

    //logic vars
    public boolean dragging, open = true;
    public int dragX, dragY;

    private final Component headerComponent = new Header(this);

    public Frame(
            Category cat,
            int x,
            int y,
            boolean notFullInit
    ) {
        this.cat = cat;
        this.customName = false;
        this.name = "";
        this.x = x;
        this.y = y;
    }

    public Frame(Category cat, int x, int y, boolean notFullInit, String name) {
        this.cat = cat;
        this.customName = true;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public Frame(int x, int y, String name) {
        this.cat = null;
        this.customName = true;
        this.name = name;
        this.x = x;
        this.y = y;

        int offsetY = HalqGui.height;
        int count1 = 0;

        for(HudModule mod : Kisman.instance.hudModuleManager.modules) {
            if(!mod.getClass().isAnnotationPresent(FakeThing.class)) {
                components.add(new Button(mod, x, y, offsetY, count1++));
                offsetY += HalqGui.height;
            }
        }
    }

    public Frame(Category cat, int x, int y) {
        this.customName = false;
        this.name = "";

        int offsetY = HalqGui.height;
        int count1 = 0;

        if(!cat.equals(Category.LUA)) {
            for (Module mod : Kisman.instance.moduleManager.getModulesInCategory(cat)) {
                if(!mod.getClass().isAnnotationPresent(FakeThing.class)) {
                    components.add(new Button(mod, x, y, offsetY, count1++));
                    offsetY += HalqGui.height;
                }
            }
        } else {
            if(!Kisman.instance.scriptManager.scripts.isEmpty()) {
                for (Module script : Kisman.instance.scriptManager.scripts) {
                    components.add(new Button(script, x, y, offsetY, count1++));
                    offsetY += HalqGui.height;
                }
            }

            for(Module mod : Kisman.instance.moduleManager.getModulesInCategory(cat)) {
                if(mod instanceof ModulePlugin) {
                    components.add(new Button(mod, x, y, offsetY, count1++));
                    offsetY += HalqGui.height;
                }
            }
        }

        this.cat = cat;
        this.x = x;
        this.y = y;
    }

    public void reload() {
        reloading = true;
        components.clear();

        int offsetY = HalqGui.height;
        int count1 = 0;

        if(customName) {
            for(HudModule mod : Kisman.instance.hudModuleManager.modules) {
                components.add(new Button(mod, x, y, offsetY, count1++));
                offsetY += HalqGui.height;
            }
        } else {
            if (!cat.equals(Category.LUA)) {
                for (Module mod : Kisman.instance.moduleManager.getModulesInCategory(cat)) {
                    components.add(new Button(mod, x, y, offsetY, count1++));
                    offsetY += HalqGui.height;
                }
            } else {
                for (Module script : Kisman.instance.scriptManager.scripts) {
                    components.add(new Button(script, x, y, offsetY, count1++));
                    offsetY += HalqGui.height;
                }
            }
        }
        reloading = false;
    }

    public void render(int mouseX, int mouseY) {
        if(dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }

        HalqGui.drawComponent(headerComponent);
    }

    public void veryRenderPost(int mouseX, int mouseY) {
        if(open && Config.instance.guiDesc.getValBoolean()) for(Component comp : components) if(comp.visible() && comp instanceof Button && ((Button) comp).isMouseOnButton(mouseX, mouseY) && !((Button) comp).description.title.isEmpty()) HalqGui.drawComponent(((Button) comp).description);
    }

    private int[] doRefreshIteration(ArrayList<Component> components, int[] data) {
        int offsetY = data[0];
        int count = data[1];

        for(Component component : components) {
            if(!component.visible()) continue;

            component.setOff(offsetY);
            component.setCount(count);

            offsetY += component.getHeight();
            count++;

            if(component instanceof Openable) {
                Openable openable = (Openable) component;
                if(openable.isOpen()) {
                    int[] dataNew = doRefreshIteration(openable.getComponents(), new int[] {offsetY, count});
                    offsetY = dataNew[0];
                    count = dataNew[1];
                }
            }
        }

        return new int[] {offsetY, count};
    }

    public void refresh() {
        int offsetY = HalqGui.height;
        int count1 = count + 1;

        for(Component comp : components) {
            if(!comp.visible()) continue;

            comp.setOff(offsetY);
            comp.setCount(count1);
            offsetY += HalqGui.height;
            count1++;
            if(comp instanceof Openable) {
                Openable button = (Openable) comp;
                if(button.isOpen()) {
                    int[] data = doRefreshIteration(button.getComponents(), new int[] {offsetY, count1});
                    offsetY = data[0];
                    count1 = data[1];
                }
            }
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y && y < this.y + HalqGui.height;
    }
}
