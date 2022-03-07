package com.kisman.cc.gui.moonlight.windows.components.api;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ContentPane<T extends AbstractComponent> extends AbstractComponent {

    private final CopyOnWriteArrayList<T> components;
    public final HashMap<String, Object> metaTags;

    public ContentPane(int x, int y, int width, int height) {
        super(x, y, width, height);
        components = new CopyOnWriteArrayList<>();
        this.metaTags = new HashMap<>();
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton) {
        for (T component : components) {
            if (component.isVisible()) {
                component.click(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void release(int mouseX, int mouseY, int mouseButton) {
        for (T component : components) {
            if (component.isVisible()) {
                component.release(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        int yVal = 0;
        for (T component : components) {
            if (component.isVisible()) {
                component.x = x;
                component.y = y + yVal;
                yVal += component.height;
                component.draw(mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public void typed(char keyChar, int keyCode) {
        for (T component : components) {
            if (component.isVisible()) {
                component.typed(keyChar, keyCode);
            }
        }
    }


    public void clear() {
        for (T component : components) {
            components.remove(component);
        }
    }

    public CopyOnWriteArrayList<T> getComponents() {
        return components;
    }
}