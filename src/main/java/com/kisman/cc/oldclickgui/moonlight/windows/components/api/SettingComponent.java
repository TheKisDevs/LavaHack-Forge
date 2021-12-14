package com.kisman.cc.oldclickgui.moonlight.windows.components.api;


public abstract class SettingComponent extends AbstractComponent {
    public SettingComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public abstract boolean isVisible();
}
