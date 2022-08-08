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
import com.kisman.cc.gui.halq.components.sub.ColorButton;
import com.kisman.cc.gui.halq.components.sub.ModeButton;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.enums.RectSides;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.ShadowRectObject;
import com.kisman.cc.util.render.objects.screen.Vec4d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Frame {
    //vars
    public final ArrayList<Component> mods = new ArrayList<>();
    public final Category cat;
    public final boolean hud;
    public int x, y, count = 0;
    public boolean reloading = false;

    //logic vars
    public boolean dragging, open = true;
    public int dragX, dragY;

    public Frame(int x, int y) {
        this.cat = null;
        this.hud = true;
        this.x = x;
        this.y = y;

        int offsetY = HalqGui.height;
        int count1 = 0;

        for(HudModule mod : Kisman.instance.hudModuleManager.modules) {
            mods.add(new Button(mod, x, y, offsetY, count1++));
            offsetY += HalqGui.height;
        }
    }

    public Frame(Category cat, int x, int y) {
        this.hud = false;

        int offsetY = HalqGui.height;
        int count1 = 0;

        if(!cat.equals(Category.LUA)) {
            for (Module mod : Kisman.instance.moduleManager.getModulesInCategory(cat)) {
                mods.add(new Button(mod, x, y, offsetY, count1++));
                offsetY += HalqGui.height;
            }
        } else {
            if(!Kisman.instance.scriptManager.scripts.isEmpty()) {
                for (Module script : Kisman.instance.scriptManager.scripts) {
                    mods.add(new Button(script, x, y, offsetY, count1++));
                    offsetY += HalqGui.height;
                }
            }

            for(Module mod : Kisman.instance.moduleManager.getModulesInCategory(cat)) {
                if(mod instanceof ModulePlugin)
                mods.add(new Button(mod, x, y, offsetY, count1++));
                offsetY += HalqGui.height;
            }
        }

        this.cat = cat;
        this.x = x;
        this.y = y;
    }

    public void reload() {
        reloading = true;
        mods.clear();

        int offsetY = HalqGui.height;
        int count1 = 0;

        if(hud) {
            for(HudModule mod : Kisman.instance.hudModuleManager.modules) {
                mods.add(new Button(mod, x, y, offsetY, count1++));
                offsetY += HalqGui.height;
            }
        } else {
            if (!cat.equals(Category.LUA)) {
                for (Module mod : Kisman.instance.moduleManager.getModulesInCategory(cat)) {
                    mods.add(new Button(mod, x, y, offsetY, count1++));
                    offsetY += HalqGui.height;
                }
            } else {
                for (Module script : Kisman.instance.scriptManager.scripts) {
                    mods.add(new Button(script, x, y, offsetY, count1++));
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
        } else {
            Render2DUtil.drawRectWH(x, y, HalqGui.width, HalqGui.height, HalqGui.getGradientColour(count).getRGB());
            if (HalqGui.shadow) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[]{x - HalqGui.headerOffset, y}, new double[]{x, y}, new double[]{x, y + HalqGui.height}, new double[]{x - HalqGui.headerOffset, y + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.getGradientColour(count).getColor(), 30), HalqGui.getGradientColour(count).getColor()));
            if (HalqGui.shadow) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[]{x + HalqGui.width, y}, new double[]{x + HalqGui.width + HalqGui.headerOffset, y}, new double[]{x + HalqGui.width + HalqGui.headerOffset, y + HalqGui.height}, new double[]{x + HalqGui.width, y + HalqGui.height}), HalqGui.getGradientColour(count).getColor(), ColorUtils.injectAlpha(HalqGui.getGradientColour(count).getColor(), 30)));
        }

        HalqGui.drawString((hud ? "Hud Editor" : cat.getName()), x, y, HalqGui.width, HalqGui.height);

        // + (Config.instance.guiRenderSize.getValBoolean() ? " [" + (hud ? Kisman.instance.hudModuleManager.modules.size() : (cat.equals(Category.LUA) ? Kisman.instance.scriptManager.scripts.size() + Kisman.instance.moduleManager.getModulesInCategory(cat).size() : Kisman.instance.moduleManager.getModulesInCategory(cat).size())) + "]": "")
        if(Config.instance.guiRenderSize.getValBoolean()) {
            HalqGui.drawSuffix(
                    "[" + (hud ? Kisman.instance.hudModuleManager.modules.size() : (cat.equals(Category.LUA) ? Kisman.instance.scriptManager.scripts.size() + Kisman.instance.moduleManager.getModulesInCategory(cat).size() : Kisman.instance.moduleManager.getModulesInCategory(cat).size())) + "]",
                    (hud ? "Hud Editor" : cat.getName()),
                    x,
                    y,
                    HalqGui.width,
                    HalqGui.height,
                    new Colour(255, 255, 255, 255),
                    2
            );
        }
    }

    public void renderPost(int mouseX, int mouseY) {
        if(open) {
            if(!HalqGui.line || mods.isEmpty()) return;
            int startY = y + HalqGui.height;
            for(Component comp : mods) if(comp instanceof Button) {
                Button button = (Button) comp;
                if(HalqGui.shadowRects) {
                    new ShadowRectObject(x, startY, x + 1, startY + HalqGui.height, HalqGui.getGradientColour(button.getCount()), HalqGui.getGradientColour(button.getCount()).withAlpha(0), 5, Arrays.asList(RectSides.Top, RectSides.Bottom));
                    new ShadowRectObject(x + HalqGui.width - 1, startY, x + HalqGui.width, startY + HalqGui.height, HalqGui.getGradientColour(button.getCount()), HalqGui.getGradientColour(button.getCount()).withAlpha(0), 5, Arrays.asList(RectSides.Top, RectSides.Bottom));
                } else {
                    Render2DUtil.drawRectWH(x, startY, 1, HalqGui.height, HalqGui.getGradientColour(button.getCount()).getRGB());
                    Render2DUtil.drawRectWH(x + HalqGui.width - 1, startY, 1, HalqGui.height, HalqGui.getGradientColour(button.getCount()).getRGB());
                }
                startY += HalqGui.height;
                if(button.open) for(Component comp1 : button.comps) if(comp1.visible()) {
                    if(HalqGui.shadowRects) {
                        new ShadowRectObject(comp1.getX(), startY, comp1.getX() + 1.5, startY + comp1.getHeight(), HalqGui.getGradientColour(comp1.getCount()), HalqGui.getGradientColour(comp1.getCount()).withAlpha(0), 5, Arrays.asList(RectSides.Top, RectSides.Bottom));
                        double x__ = comp1.getX() + (HalqGui.width - (LayerControllerKt.getXOffset(comp1.getLayer()) * 2)) - 1.5;
                        new ShadowRectObject(x__, startY, x + 1.5, startY + comp1.getHeight(), HalqGui.getGradientColour(comp1.getCount()), HalqGui.getGradientColour(comp1.getCount()).withAlpha(0), 5, Arrays.asList(RectSides.Top, RectSides.Bottom));
                    } else {
                        Render2DUtil.drawRectWH(comp1.getX(), startY, 1.5, comp1.getHeight(), HalqGui.getGradientColour(comp1.getCount()).getRGB());
                        Render2DUtil.drawRectWH(comp1.getX() + (HalqGui.width - (LayerControllerKt.getXOffset(comp1.getLayer()) * 2)) - 1.5, startY, 1.5, comp1.getHeight(), HalqGui.getGradientColour(comp1.getCount()).getRGB());
                    }
                    startY += comp1.getHeight();

                    if(comp1 instanceof Openable) {
                        Openable openable = (Openable) comp1;
                        if(openable.isOpen()) {
                            for(Component comp2 : openable.getComponents()) {
                                if(!comp2.visible()) continue;
                                boolean open1 = (comp2 instanceof ModeButton && ((ModeButton) comp2).open) || (comp2 instanceof ColorButton && ((ColorButton) comp2).open);
                                if(HalqGui.shadowRects) {
                                    new ShadowRectObject(comp2.getX(), startY, comp2.getX() + 1.5 + (open1 ? 0.5 : 0), startY + comp2.getHeight(), HalqGui.getGradientColour(comp2.getCount()), HalqGui.getGradientColour(comp2.getCount()).withAlpha(0), 5, Arrays.asList(RectSides.Top, RectSides.Bottom));
                                    double x__ = comp2.getX() + (HalqGui.width - (LayerControllerKt.getXOffset(comp2.getLayer()) * 2)) - 1.5 - (open1 ? 0.5 : 0);
                                    new ShadowRectObject(x__, startY, x + 1.5 + (open1 ? 0.5 : 0), startY + comp2.getHeight(), HalqGui.getGradientColour(comp2.getCount()), HalqGui.getGradientColour(comp2.getCount()).withAlpha(0), 5, Arrays.asList(RectSides.Top, RectSides.Bottom));
                                } else {
                                    Render2DUtil.drawRectWH(comp2.getX(), startY, 1.5 + (open1 ? 0.5 : 0), comp2.getHeight(), HalqGui.getGradientColour(comp2.getCount()).getRGB());
                                    Render2DUtil.drawRectWH(comp2.getX() + (HalqGui.width - (LayerControllerKt.getXOffset(comp2.getLayer()) * 2)) - 1.5 - (open1 ? 0.5 : 0), startY, 1.5 + (open1 ? 0.5 : 0), comp2.getHeight(), HalqGui.getGradientColour(comp2.getCount()).getRGB());
                                }
                                startY += comp2.getHeight();
                            }
                        }
                    }
                }
            }
        }
    }

    public void veryRenderPost(int mouseX, int mouseY) {
        if(open && Config.instance.guiDesc.getValBoolean()) for(Component comp : mods) if(comp instanceof Button && ((Button) comp).isMouseOnButton(mouseX, mouseY) && !((Button) comp).description.title.isEmpty()) ((Button) comp).description.drawScreen(mouseX, mouseY);
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

        for(Component comp : mods) {
            comp.setOff(offsetY);
            comp.setCount(count1);
            offsetY += HalqGui.height;
            count1++;
            if(comp instanceof Button) {
                Button button = (Button) comp;
                if(button.open) {
                    int[] data = doRefreshIteration(button.comps, new int[] {offsetY, count1});
                    offsetY = data[0];
                    count1 = data[1];
                    /*for (Component comp1 : button.comps) {
                        if(!comp1.visible()) continue;
                        comp1.setCount(count1);
                        comp1.setOff(offsetY);
                        offsetY += comp1.getHeight();
                        count1++;
                        if(comp1 instanceof Openable) {
                            Openable group = (Openable) comp1;
                            if(group.isOpen()) {
                                for (Component comp2 : group.getComponents()) {
                                    if(!comp2.visible()) continue;
                                    comp2.setCount(count1);
                                    comp2.setOff(offsetY);
                                    offsetY += comp2.getHeight();
                                    count1++;
                                    if(comp2 instanceof Openable) {
                                        Openable openable = (Openable) comp2;
                                        if(openable.isOpen() && openable.visible()) {
                                            for(Component comp3 : openable.getComponents()) {
                                                comp3.setCount(count1++);
                                                comp3.setOff(offsetY);
                                                offsetY += comp3.getHeight();
                                                count1++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }*/
                }
            }
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y && y < this.y + HalqGui.height;
    }
}
