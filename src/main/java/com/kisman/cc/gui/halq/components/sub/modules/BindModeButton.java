package com.kisman.cc.gui.halq.components.sub.modules;

import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.util.collections.Bind;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;

public class BindModeButton extends ShaderableImplementation implements Component {
    private final Module module;
    private int x, y, offset, count, index;
    private final String[] values;
    private boolean open = false;
    private int width = HalqGui.width;
    private int layer;

    public BindModeButton(Module module, int x, int y, int offset, int count, int layer) {
        this.module = module;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
        this.values = new String[] {"Toggle", "Hold"};
        this.index = module.hold ? 1 : 0;
        this.layer = layer;
        this.width = LayerControllerKt.getModifiedWidth(layer, width);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        this.index = module.hold ? 1 : 0;

        normalRender = () -> Render2DUtil.drawRectWH(x, y + offset, width, getHeight(), HalqGui.backgroundColor.getRGB());

        Runnable shaderRunnable1 = () -> {
            if(HalqGui.shadow) {
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[] {x + HalqGui.offsetsX, y + offset + HalqGui.offsetsY},
                                        new double[] {x + width / 2, y + offset + HalqGui.offsetsY},
                                        new double[] {x + width / 2, y + offset + HalqGui.height - HalqGui.offsetsY},
                                        new double[] {x + HalqGui.offsetsX, y + offset + HalqGui.height - HalqGui.offsetsY}
                                ),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.idkJustAlpha.getValInt()),
                                HalqGui.getGradientColour(count).getColor()
                        )
                );
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[] {x + width / 2, y + offset + HalqGui.offsetsY},
                                        new double[] {x + width - HalqGui.offsetsX, y + offset + HalqGui.offsetsY},
                                        new double[] {x + width - HalqGui.offsetsX, y + offset + HalqGui.height - HalqGui.offsetsY},
                                        new double[] {x + width / 2, y + offset + HalqGui.height - HalqGui.offsetsY}
                                ),
                                HalqGui.getGradientColour(count).getColor(),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.idkJustAlpha.getValInt())
                        )
                );
            } else Render2DUtil.drawRectWH(x + HalqGui.offsetsX, y + offset + HalqGui.offsetsY, width - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, HalqGui.getGradientColour(count).getRGB());
        };

        Runnable shaderRunnable2 = () -> {
            HalqGui.drawString("Bind Mode: " + values[index], x, y + offset, width, HalqGui.height);

            if (open) {
                int offsetY = offset + HalqGui.height;
                for (int i = 0; i < values.length; i++) {
                    if (i == index) continue;
                    HalqGui.drawCenteredString(values[i], x, y + offsetY, width, HalqGui.height);
                    offsetY += HalqGui.height;
                }
            }
        };

        shaderRender = new Bind<>(shaderRunnable1, shaderRunnable2);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) open = !open;
        else if(isMouseOnButton2(mouseX, mouseY) && button == 0 && open) {
            int offsetY = y +  offset + HalqGui.height;
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
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
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
        return HalqGui.height + (open ? (values.length - 1) * HalqGui.height : 0);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean visible() {
        return true;
    }

    public void setWidth(int width) {this.width = width;}
    public void setX(int x) {this.x = x;}
    public int getX() {return x;}
    public void setLayer(int layer) {this.layer = layer;}
    public int getLayer() {return layer;}

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }

    private boolean isMouseOnButton2(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y + offset && y < this.y + offset + getHeight();
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
