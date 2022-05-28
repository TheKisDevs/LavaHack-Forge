package com.kisman.cc.gui.api;

public interface Component {
    default void drawScreen(int mouseX, int mouseY) {}
    default void mouseClicked(int mouseX, int mouseY, int button) {}
    default void mouseReleased(int mouseX, int mouseY, int mouseButton) {}
    default void updateComponent(int x, int y) {}
    default void keyTyped(char typedChar, int key) {}
    default void setOff(int newOff) {}
    default void setCount(int count) {}
    default int getHeight() {return 0;}
    default int getCount() {return 0;}
    default void setWidth(int width) {}
    default void setX(int x) {}
    default int getX() {return 0;}
    default void setLayer(int layer) {}
    default int getLayer() {return 0;}
    default boolean visible() {return true;}
}