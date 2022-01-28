package com.kisman.cc.oldclickgui.csgo;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.oldclickgui.csgo.components.*;
import com.kisman.cc.oldclickgui.csgo.components.Button;
import com.kisman.cc.oldclickgui.csgo.components.Label;
import com.kisman.cc.oldclickgui.csgo.components.ScrollPane;
import com.kisman.cc.oldclickgui.csgo.layout.FlowLayout;
import com.kisman.cc.oldclickgui.csgo.layout.GridLayout;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.MathUtil;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.*;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClickGuiNew extends GuiScreen {
    private final HashMap<Category, Pane> categoryPaneMap;
    private final Pane spoilerPane;
    private Window window;
    private IRenderer renderer;
    private List<ActionEventListener> onRenderListeners = new ArrayList<>();

    public ClickGuiNew() {
        categoryPaneMap = new HashMap<>();
        renderer = new ClientBaseRendererImpl();
        spoilerPane = new Pane(renderer, new GridLayout(1));
        window = new Window(Kisman.getName(), 50, 50, 920, 420);

        Pane conentPane = new ScrollPane(renderer, new GridLayout(1));
        Pane buttonPane = new Pane(renderer, new FlowLayout());

        HashMap<Category, Pane> paneMap = new HashMap<>();
        List<Spoiler> spoilers = new ArrayList<>();
        List<Pane> paneList = new ArrayList<>();

        for(Category cat : Category.values()) {
            ArrayList<Module> modules = Kisman.instance.moduleManager.getModulesInCategory(cat);
            Pane spoilerPane = new Pane(renderer, new GridLayout(1));
            Button button = new Button(renderer, cat.getName() + (Config.instance.guiRenderSIze.getValBoolean() ? " [" + modules.size() + "]" : ""));
            buttonPane.addComponent(button);
            button.setOnClickListener(() -> setCurrentCategory(cat));


            for(Module module : modules) {
                Pane settingPane = new Pane(renderer, new GridLayout(4));

                {
                    settingPane.addComponent(new Label(renderer, "Toggle"));
                    CheckBox cb = new CheckBox(renderer, "Toggled");
                    settingPane.addComponent(cb);

                    onRenderListeners.add(() -> cb.setSelected(module.isToggled()));

                    cb.setListener(val -> {
                        module.setToggled(val);
                        return true;
                    });
                }

                {
                    settingPane.addComponent(new Label(renderer, "Keybind"));
                    KeybindButton kb = new KeybindButton(renderer, Keyboard::getKeyName);
                    settingPane.addComponent(kb);
                    onRenderListeners.add(() -> kb.setValue(module.getKey()));

                    kb.setListener(val -> {
                        module.setKey(val);
                        return true;
                    });
                }

                {
                    settingPane.addComponent(new Label(renderer, "Visible"));
                    CheckBox cb = new CheckBox(renderer, "Visibled");
                    settingPane.addComponent(cb);

                    onRenderListeners.add(() -> cb.setSelected(module.visible));

                    cb.setListener(val -> {
                        module.visible = val;
                        return true;
                    });
                }

                {
                    settingPane.addComponent(new Label(renderer, "Bind Mode"));
                    ComboBox cb = new ComboBox(renderer, new String[] {"Toggle", "Hold"}, module.hold ? 1 : 0);
                    settingPane.addComponent(cb);

                    cb.setListener(val -> {
                        module.hold = val != 0;
                        return true;
                    });

                    onRenderListeners.add(() -> cb.setSelectedIndex(module.hold ? 1 : 0));
                }

                {
                    if (Kisman.instance.settingsManager.getSettingsByMod(module) != null) {
                        if(!Kisman.instance.settingsManager.getSettingsByMod(module).isEmpty()) {
                            for (Setting set : Kisman.instance.settingsManager.getSettingsByMod(module)) {
                                if(set.isBind()) {
                                    settingPane.addComponent(new Label(renderer, set.getName()));
                                    KeybindButton kb = new KeybindButton(renderer, Keyboard::getKeyName);
                                    settingPane.addComponent(kb);
                                    onRenderListeners.add(() -> kb.setValue(set.getKey()));

                                    kb.setListener(val -> {
                                        set.setKey(val);
                                        return true;
                                    });
                                }
                                /*if(set.isPreview()) {
                                    settingPane.addComponent(new Label(renderer, set.getTitle()));
                                    settingPane.addComponent(new PreviewButton(renderer, set.getEntity()));
                                }*/
                                if(set.isColorPicker()) {
                                    settingPane.addComponent(new Label(renderer, set.getTitle()));
                                    ColorButton sb = new ColorButton(renderer, set.getColour());
                                    settingPane.addComponent(sb);
                                    sb.setListener(val -> {
                                        set.setColour(val);
                                        set.setR(val.r);
                                        set.setG(val.g);
                                        set.setB(val.b);
                                        set.setA(val.a);
                                        return true;
                                    });

                                    onRenderListeners.add(() -> sb.setValue(set.getColour()));
                                }
                                if(set.isString()) {
                                    settingPane.addComponent(new Label(renderer, set.getName()));
                                    StringButton sb = new StringButton(renderer, set.getdString());
                                    settingPane.addComponent(sb);
                                    sb.setListener(val -> {
                                        set.setValString(val);
                                        return true;
                                    });

                                    onRenderListeners.add(() -> sb.setValue(set.getValString()));
                                }
                                if (set.isCheck()) {
                                    settingPane.addComponent(new Label(renderer, set.getName()));
                                    CheckBox cb = new CheckBox(renderer, "Enabled");
                                    settingPane.addComponent(cb);
                                    cb.setListener(val -> {
                                        set.setValBoolean(val);
                                        return true;
                                    });

                                    onRenderListeners.add(() -> cb.setSelected(set.getValBoolean()));
                                }
                                if (set.isSlider()) {
                                    settingPane.addComponent(new Label(renderer, set.getName()));
                                    Slider.NumberType type = Slider.NumberType.DECIMAL;
                                    Slider sl;

                                    switch (set.getNumberType()) {
                                        case INTEGER: {
                                            if (set.isOnlyint()) type = Slider.NumberType.INTEGER;
                                            break;
                                        }
                                        case PERCENT: {
                                            if (set.getMin() == 0 && set.getMax() == 100)
                                                type = Slider.NumberType.PERCENT;
                                            break;
                                        }
                                        case TIME: {
                                            type = Slider.NumberType.TIME;
                                            break;
                                        }
                                    }

                                    sl = new Slider(renderer, set.getValDouble(), set.getMin(), set.getMax(), type);

                                    settingPane.addComponent(sl);

                                    sl.setListener(val -> {
                                        set.setValDouble(val.doubleValue());
                                        return true;
                                    });

                                    onRenderListeners.add(() -> sl.setValue(set.getValDouble()));
                                }
                                if (set.isCombo()) {
                                    settingPane.addComponent(new Label(renderer, set.getName()));

                                    ComboBox cb = new ComboBox(renderer, set.getStringValues(), set.getSelectedIndex());

                                    settingPane.addComponent(cb);

                                    cb.setListener(val -> {
                                        set.setValString(set.getStringFromIndex(val));
                                        return true;
                                    });

                                    onRenderListeners.add(() -> cb.setSelectedIndex(set.getSelectedIndex()));
                                }
                            }
                        }
                    }
                }

                Spoiler spoiler = new Spoiler(renderer, module.getName(), width, settingPane, module);

                paneList.add(settingPane);
                spoilers.add(spoiler);

                spoilerPane.addComponent(spoiler);

                paneMap.put(cat, spoilerPane);
            }

            categoryPaneMap.put(cat, spoilerPane);
        }

        conentPane.addComponent(buttonPane);

        int maxWidth = Integer.MIN_VALUE;

        for(Pane pane : paneList) maxWidth = Math.max(maxWidth, pane.getWidth());

        window.setWidth(28 + maxWidth);

        for(Spoiler spoiler : spoilers) {
            spoiler.preferredWidth = maxWidth;
            spoiler.setWidth(maxWidth);
        }

        spoilerPane.setWidth(maxWidth);
        buttonPane.setWidth(maxWidth);

        conentPane.addComponent(spoilerPane);

        conentPane.updateLayout();

        window.setContentPane(conentPane);

        if(categoryPaneMap.keySet().size() > 0) setCurrentCategory(categoryPaneMap.keySet().iterator().next());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (ActionEventListener onRenderListener : onRenderListeners) onRenderListener.onActionEvent();

        window.setTitle(Kisman.getName() + " | " + Kisman.getVersion() + (Config.instance.guiRenderSIze.getValBoolean() ? " | " + Kisman.instance.moduleManager.modules.size() + " modules" : ""));

        GL11.glPushMatrix();
        Point point = MathUtil.calculateMouseLocation();
        window.mouseMoved(point.x * 2, point.y * 2);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glLineWidth(1.0f);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        window.render(renderer, mouseX, mouseY);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();

        if(Config.instance.guiOpenAnimation.getValBoolean()) {
            GL11.glPushMatrix();

            float insizer = 0;
            if (Kisman.map.containsKey(this)) {
                Kisman.map.entrySet().forEach(m -> m.setValue(lerp(m.getValue(), 0.5f, 0.06f)));
                insizer = -Kisman.map.get(this);
            }
            GL11.glTranslated(-insizer * 4.75, -insizer * 2.75, 0);
            GL11.glScaled(1 + insizer / 100, 1 + insizer / 100, 1);
            GL11.glTranslated(0, -insizer, 0);

            GL11.glPopMatrix();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        window.mouseMoved(mouseX * 2, mouseY * 2);
        window.mousePressed(mouseButton, mouseX * 2, mouseY * 2);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        window.mouseMoved(mouseX * 2, mouseY * 2);
        window.mouseReleased(state, mouseX * 2, mouseY * 2);

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        window.mouseMoved(mouseX * 2, mouseY * 2);
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int eventDWheel = Mouse.getEventDWheel();

        window.mouseWheel(eventDWheel);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode != -1) window.keyPressed(keyCode, typedChar);
        else mc.displayGuiScreen(null);
        super.keyTyped(typedChar, keyCode);
    }

    private void setCurrentCategory(Category category) {
        spoilerPane.clearComponents();
        spoilerPane.addComponent(categoryPaneMap.get(category));
    }

    private float lerp(float start, float end, float step){
        return start + step * (end - start);
    }
}
