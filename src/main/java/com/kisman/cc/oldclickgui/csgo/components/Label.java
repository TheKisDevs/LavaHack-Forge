package com.kisman.cc.oldclickgui.csgo.components;

import com.kisman.cc.oldclickgui.csgo.AbstractComponent;
import com.kisman.cc.oldclickgui.csgo.IRenderer;
import com.kisman.cc.oldclickgui.csgo.Window;

public class Label extends AbstractComponent {
    private String text;

    public Label(IRenderer renderer, String text) {
        super(renderer);
        setText(text);
    }

    @Override
    public void render() {
        renderer.drawString(x, y, text, Window.FOREGROUND);
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