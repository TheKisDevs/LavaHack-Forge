package com.kisman.cc.gui.moonlight.windows.components.api;

public abstract class AbstractComponent extends Rect implements IComponent {

    private boolean isVisible = true;

    public AbstractComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
}
