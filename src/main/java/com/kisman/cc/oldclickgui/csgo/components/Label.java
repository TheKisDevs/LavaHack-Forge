package com.kisman.cc.oldclickgui.csgo.components;

import com.kisman.cc.oldclickgui.csgo.*;

public class Label extends AbstractComponent {
    private String text;

    public Label(IRenderer renderer, String text) {
        super(renderer);
        setText(text);
    }

    @Override
    public void render() {
        renderer.drawString(x, y + 11 - renderer.getStringHeight(text) / 2, text, Window.FOREGROUND);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        setWidth(renderer.getStringWidth(text));
        setHeight(renderer.getStringHeight(text));

        this.text = text;
    }
}