package com.kisman.cc.gui.halq.components.sub;

import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.Openable;
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.components.sub.combobox.OptionElement;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.collections.Bind;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ModeButton extends ShaderableImplementation implements Openable {
    public final Setting setting;
    public OptionElement selected;
    private int x, y, offset, count;
    public boolean open;
    private int elements = 0;//0 - closed | 1 - options | 2 - binds
    private int width = HalqGui.width;
    private int layer;

    private final ArrayList<Component> components = new ArrayList<>();

    public ModeButton(Setting setting, int x, int y, int offset, int count, int layer) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
        this.layer = layer;
        this.width = LayerControllerKt.getModifiedWidth(layer, width);

        int i = 0;
        int offsetY = offset + HalqGui.height;
        int count1 = 0;

        for(String option : setting.binders.keySet()) {
            count1++;

            OptionElement element = new OptionElement(
                    this,
                    setting.binders.get(option),
                    option,
                    i,
                    () -> open && (elements == 1),
                    x,
                    y,
                    offsetY,
                    count1,
                    layer + 1
            );

            components.add(element);

            components.add(
                    new BindButton(
                            setting.binders.get(option),
                            x,
                            y,
                            offsetY,
                            count1,
                            layer + 1
                    ).setVisible(() -> open && (elements == 2))
            );

            offsetY += HalqGui.height;

            i++;
        }

        selected = (OptionElement) components.stream().filter(component -> component instanceof OptionElement && ((OptionElement) component).getName().equals(setting.getValString())).findFirst().orElse(components.get(0));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);

        selected = (OptionElement) components.stream().filter(component -> component instanceof OptionElement && ((OptionElement) component).getName().equals(setting.getValString())).findFirst().orElse(components.get(0));

        normalRender = () -> Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());

        Runnable shaderRunnable1 =() -> {
            if (HalqGui.shadow) {
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[]{x + HalqGui.offsetsX, y + offset + HalqGui.offsetsY},
                                        new double[]{x + width / 2, y + offset + HalqGui.offsetsY},
                                        new double[]{x + width / 2, y + offset + HalqGui.height - HalqGui.offsetsY},
                                        new double[]{x + HalqGui.offsetsX, y + offset + HalqGui.height - HalqGui.offsetsY}
                                ),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.idkJustAlpha.getValInt()),
                                HalqGui.getGradientColour(count).getColor()
                        )
                );
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[]{x + width / 2, y + offset + HalqGui.offsetsY},
                                        new double[]{x + width - HalqGui.offsetsX, y + offset + HalqGui.offsetsY},
                                        new double[]{x + width - HalqGui.offsetsX, y + offset + HalqGui.height - HalqGui.offsetsY},
                                        new double[]{x + width / 2, y + offset + HalqGui.height - HalqGui.offsetsY}
                                ),
                                HalqGui.getGradientColour(count).getColor(),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.idkJustAlpha.getValInt())
                        )
                );
            } else Render2DUtil.drawRectWH(x + HalqGui.offsetsX, y + offset + HalqGui.offsetsY, width - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, HalqGui.getGradientColour(count).getRGB());
        };

        Runnable shaderRunnable2 = () -> HalqGui.drawString(setting.getTitle() + ": " + selected.getName(), x, y + offset, width, HalqGui.height);

        shaderRender = new Bind<>(shaderRunnable1, shaderRunnable2);

        if(open) for(Component component : components) if(component.visible()) HalqGui.drawComponent(component);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY)) {
            open = !open;

            if(button == 0) elements = 1;
            else if(button == 1) elements = 2;
        } else if(isMouseOnButton2(mouseX, mouseY) && open) {
            for(Component component : components) {
                if(component.visible()) {
                    component.mouseClicked(
                            mouseX,
                            mouseY,
                            button
                    );
                }
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(elements == 2) {
            for (Component component : components) {
                if (component.visible()) {
                    component.keyTyped(
                            typedChar,
                            key
                    );
                }
            }
        }
    }

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;

        if(open) {
            for(Component component : components) {
                if(component.visible()) {
                    component.updateComponent(
                            (x - LayerControllerKt.getXOffset(layer)) + LayerControllerKt.getXOffset(component.getLayer()),
                            y
                    );
                }
            }
        }
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    private int getHeight1() {
        int height1 = 0;

        if(open) {
            for(Component component : components) {
                if(component.visible()) {
                    height1 += component.getHeight();
                }
            }
        }

        return HalqGui.height + height1;
    }

    public boolean visible() {return setting.isVisible() && HalqGui.visible(setting.getTitle());}

    public void setCount(int count) {this.count = count;}
    public int getCount() {return count;}
    public void setWidth(int width) {this.width = width;}
    public void setX(int x) {this.x = x;}
    public int getX() {return x;}
    public void setLayer(int layer) {this.layer = layer;}
    public int getLayer() {return layer;}

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }

    private boolean isMouseOnButton2(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y + offset && y < this.y + offset + getHeight1();
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getY() {
        return y + offset;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @NotNull
    @Override
    public ArrayList<Component> getComponents() {
        return components;
    }
}
