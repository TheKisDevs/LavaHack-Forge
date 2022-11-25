package com.kisman.cc.gui.halq.components.sub.plugins;

import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.features.plugins.ModulePlugin;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;

/**
 * @author _kisman_
 * @since 20:07 of 09.06.2022
 */
public class PluginActionButton implements Component {
    private final ModulePlugin plugin;
    private final Action action;
    private int x, y, count, offset;
    private int width = HalqGui.width;
    private int layer;

    public PluginActionButton(ModulePlugin plugin, Action action, int x, int y, int offset, int count, int layer) {
        this.plugin = plugin;
        this.action = action;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
        this.layer = layer;
        this.width = LayerControllerKt.getModifiedWidth(layer, width);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(HalqGui.shadow) {
            Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());
            Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x, y + offset}, new double[] {x + width / 2, y + offset}, new double[] {x + width / 2, y + offset + HalqGui.height}, new double[] {x, y + offset + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.idkJustAlpha.getValInt()), HalqGui.getGradientColour(count).getColor()));
            Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + width / 2, y + offset}, new double[] {x + width, y + offset}, new double[] {x + width, y + offset + HalqGui.height}, new double[] {x + width / 2, y + offset + HalqGui.height}), HalqGui.getGradientColour(count).getColor(), ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.idkJustAlpha.getValInt())));
        } else Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.getGradientColour(count).getRGB());

        HalqGui.drawString(action.name, x, y + offset, width, HalqGui.height);
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
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int getHeight() {
        return HalqGui.height;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean visible() {
        return (plugin.getLoaded() == (action != Action.LOAD)) && HalqGui.visible(action.name);
    }

    public void setWidth(int width) {this.width = width;}
    public void setX(int x) {this.x = x;}
    public int getX() {return x;}
    public void setLayer(int layer) {this.layer = layer;}
    public int getLayer() {return layer;}

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }

    public enum Action {
        LOAD("Load"),
        UNLOAD("Unload"),
        RELOAD("Reload");

        final String name;
        Action(String name) {this.name = name;}
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getY() {
        return y + offset;
    }
}
