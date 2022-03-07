package com.kisman.cc.gui.moonlight.windows.components.api;

public interface IComponent {

    void click(int mouseX, int mouseY, int mouseButton);

    void release(int mouseX, int mouseY, int mouseButton);

    void draw(int mouseX, int mouseY, float partialTicks);

    void typed(char keyChar, int keyCode);

    boolean isVisible();
}
