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
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.enums.RectSides;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.ShadowRectObject;

import java.util.ArrayList;
import java.util.Collections;

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

    private final Component headerComponent = new Component() {
        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public int getLayer() {
            return 0;
        }
    };

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
            components.add(new Button(mod, x, y, offsetY, count1++));
            offsetY += HalqGui.height;
        }
    }

    public Frame(Category cat, int x, int y) {
        this.customName = false;
        this.name = "";

        int offsetY = HalqGui.height;
        int count1 = 0;

        if(!cat.equals(Category.LUA)) {
            for (Module mod : Kisman.instance.moduleManager.getModulesInCategory(cat)) {
                components.add(new Button(mod, x, y, offsetY, count1++));
                offsetY += HalqGui.height;
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

        if(HalqGui.shadowRects) {
            ShadowRectObject obj = new ShadowRectObject(x, y, x + HalqGui.width, y + HalqGui.height, HalqGui.getGradientColour(count), HalqGui.getGradientColour(count).withAlpha(0), 5, Collections.singletonList(RectSides.Bottom));
            obj.draw();
        } else Render2DUtil.drawRectWH(x, y, HalqGui.width, HalqGui.height, HalqGui.getGradientColour(count).getRGB());

        HalqGui.drawString((customName ? name : cat.getName()), x, y, HalqGui.width, HalqGui.height);

        if(Config.instance.guiRenderSize.getValBoolean()) {
            HalqGui.drawSuffix(
                    "[" + components.size() + "]",
                    (customName ? name : cat.getName()),
                    x,
                    y,
                    HalqGui.width,
                    HalqGui.height,
                    new Colour(255, 255, 255, 255),
                    2
            );
        }
    }

    private void doIterationRenderPost(
            Component component,
            int mouseX,
            int mouseY
    ) {
        component.drawScreenPost(
                mouseX,
                mouseY
        );

        if(component instanceof Openable) {
            Openable openable = (Openable) component;

            if(openable.isOpen()) {
                for(Component comp : openable.getComponents()) {
                    if(comp.visible()) {
                        doIterationRenderPost(
                                comp,
                                mouseX,
                                mouseY
                        );
                    }
                }
            }
        }
    }

    public void renderPost(int mouseX, int mouseY) {
        if(HalqGui.outlineHeaders) HalqGui.drawComponentOutline(headerComponent, false, !HalqGui.outlineTest2, HalqGui.outlineTest2);

        if(open) {
            for(Component comp : components) {
                if(!comp.visible()) continue;

                doIterationRenderPost(
                        comp,
                        mouseX,
                        mouseY
                );
            }
        }
    }

    public void veryRenderPost(int mouseX, int mouseY) {
        if(open && Config.instance.guiDesc.getValBoolean()) for(Component comp : components) if(comp.visible() && comp instanceof Button && ((Button) comp).isMouseOnButton(mouseX, mouseY) && !((Button) comp).description.title.isEmpty()) ((Button) comp).description.drawScreen(mouseX, mouseY);
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
                    int[] dataNew = doRefreshIteration(openable.getComponents(), new int[]{offsetY, count});
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
