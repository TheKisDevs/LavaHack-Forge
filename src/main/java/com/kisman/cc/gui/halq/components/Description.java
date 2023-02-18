package com.kisman.cc.gui.halq.components;

import com.kisman.cc.gui.api.shaderable.ShaderableImplementation;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.util.client.collections.Bind;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import com.kisman.cc.util.render.ColorUtils;

public class Description extends ShaderableImplementation implements Component {
    public final String title;

    public Description(String title, int count) {
        super(0, 0, count, 0, 0);
        this.title = title;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        int width = CustomFontUtil.getStringWidth(title);

        Runnable shaderRunnable1 = () -> {
            Render2DUtil.drawRectWH(mouseX + 5, mouseY, width, HalqGui.height, HalqGui.getGradientColour(getCount()).getRGB());

            if (HalqGui.shadow) {
                Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[]{mouseX - HalqGui.headerOffset + 5, mouseY}, new double[]{mouseX + 5, mouseY}, new double[]{mouseX + 5, mouseY + HalqGui.height}, new double[]{mouseX + 5 - HalqGui.headerOffset, mouseY + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.getGradientColour(getCount()).getColor(), 0), HalqGui.getGradientColour(getCount()).getColor()));
                Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[]{mouseX + 5 + width, mouseY}, new double[]{mouseX + width + 5 + HalqGui.headerOffset, mouseY}, new double[]{mouseX + 5 + width + HalqGui.headerOffset, mouseY + HalqGui.height}, new double[]{mouseX + width + 5, mouseY + HalqGui.height}), HalqGui.getGradientColour(getCount()).getColor(), ColorUtils.injectAlpha(HalqGui.getGradientColour(getCount()).getColor(), 0)));
            }
        };

        Runnable shaderRunnable2 = () -> HalqGui.drawCenteredString(title, mouseX, mouseY, width + HalqGui.headerOffset * 2, HalqGui.height);

        shaderRender = new Bind<>(shaderRunnable1, shaderRunnable2);
    }
}
