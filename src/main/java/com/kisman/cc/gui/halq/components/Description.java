package com.kisman.cc.gui.halq.components;

import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.util.client.collections.Bind;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.CustomFontUtil;

public class Description extends ShaderableImplementation implements Component {
    public final String title;

    public Description(String title, int count) {
        super(0, 0, count, 0, 0);
        this.title = title;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        int width = CustomFontUtil.getStringWidth(title);

        Runnable shaderRunnable1 = () -> Render2DUtil.drawRectWH(mouseX + 10, mouseY + 10, width + 10, HalqGui.height, HalqGui.getGradientColour(getCount()).getRGB());
        Runnable shaderRunnable2 = () -> CustomFontUtil.drawStringWithShadow(title, mouseX + 15, mouseY + 15, -1);

        shaderRender = new Bind<>(shaderRunnable1, shaderRunnable2);
    }
}
