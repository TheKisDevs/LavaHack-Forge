package com.kisman.cc.oldclickgui.csgo.components;

import com.kisman.cc.oldclickgui.csgo.*;

public class EmptyButton extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 22;

    public EmptyButton(IRenderer renderer) {
        super(renderer);
        setWidth(PREFERRED_WIDTH);
        setHeight(PREFERRED_HEIGHT);
    }

    @Override
    public void render() {}

    @Override
    public void postRender() {}
    
}
