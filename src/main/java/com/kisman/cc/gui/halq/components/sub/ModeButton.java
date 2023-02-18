package com.kisman.cc.gui.halq.components.sub;

import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.Openable;
import com.kisman.cc.gui.api.SettingComponent;
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.components.sub.combobox.OptionElement;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.client.collections.Bind;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@SuppressWarnings("NullableProblems")
public class ModeButton extends ShaderableImplementation implements Openable, SettingComponent {
    public final Setting setting;
    public OptionElement selected;
    public boolean open;
    private int elements = 0;//0 - closed | 1 - options | 2 - binds

    private final ArrayList<Component> components = new ArrayList<>();

    public ModeButton(Setting setting, int x, int y, int offset, int count, int layer) {
        super(x, y, count, offset, layer);
        this.setting = setting;

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

        normalRender = () -> Render2DUtil.drawRectWH(getX(), getY(), getWidth(), HalqGui.height, HalqGui.backgroundColor.getRGB());

        Runnable shaderRunnable1 = () -> {
            if (HalqGui.shadow) {
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[]{getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                        new double[]{getX() + getWidth() / 2.0, getY() + HalqGui.offsetsY},
                                        new double[]{getX() + getWidth() / 2.0, getY() + HalqGui.height - HalqGui.offsetsY},
                                        new double[]{getX() + HalqGui.offsetsX, getY() + HalqGui.height - HalqGui.offsetsY}
                                ),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.minPrimaryAlpha.getValInt()),
                                HalqGui.getGradientColour(getCount()).getColor()
                        )
                );
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[]{getX() + getWidth() / 2.0, getY() + HalqGui.offsetsY},
                                        new double[]{getX() + getWidth() - HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                        new double[]{getX() + getWidth() - HalqGui.offsetsX, getY() + HalqGui.height - HalqGui.offsetsY},
                                        new double[]{getX() + getWidth() / 2.0, getY() + HalqGui.height - HalqGui.offsetsY}
                                ),
                                HalqGui.getGradientColour(getCount()).getColor(),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.minPrimaryAlpha.getValInt())
                        )
                );
            } else Render2DUtil.drawRectWH(getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY, getWidth() - HalqGui.offsetsX * 2, HalqGui.height - HalqGui.offsetsY * 2, HalqGui.getGradientColour(getCount()).getRGB());
        };

        Runnable shaderRunnable2 = () -> HalqGui.drawString(setting.getTitle() + ": " + selected.getName(), getX(), getY(), getWidth(), HalqGui.height);

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
        super.updateComponent(x, y);

        if(open) {
            for(Component component : components) {
                if(component.visible()) {
                    component.updateComponent(
                            (x - LayerControllerKt.getXOffset(getLayer())) + LayerControllerKt.getXOffset(component.getLayer()),
                            y
                    );
                }
            }
        }
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

    public boolean isMouseOnButton(int x, int y) {
        return x > getX() && x < getX() + getWidth() && y > getY() && y < getY() + HalqGui.height;
    }

    private boolean isMouseOnButton2(int x, int y) {
        return x > getX() && x < getX() + getWidth() && y > getY() && y < getY() + getHeight1();
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public ArrayList<Component> getComponents() {
        return components;
    }

    @NotNull
    @Override
    public Setting setting() {
        return setting;
    }
}
