package com.kisman.cc.clickgui.components;

import com.kisman.cc.clickgui.Frame;
import com.kisman.cc.module.Module;

import java.util.ArrayList;

public class Button {
    public Module mod;
    public Frame parent;
    public int offsetX;
    public int offsetY;
    private boolean isHovered;
    private ArrayList<Component> subcomponents;
    public boolean open;

    public Button(Module mod, Frame parent, int offsetX, int offsetY) {
        this.mod = mod;
        this.parent = parent;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.subcomponents = new ArrayList<>();
        this.open = false;
    }
}
