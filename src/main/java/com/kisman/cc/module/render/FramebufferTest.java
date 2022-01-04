package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.module.render.shader.shaders.ItemShader;
import com.kisman.cc.oldclickgui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class FramebufferTest extends Module {
    private Setting blur = new Setting("Blur", this, true);
    private Setting radius = new Setting("Radius", this, 2, 0.1f, 10, false);
    private Setting mix = new Setting("Mix", this, 1, 0, 1, false);
    private Setting red = new Setting("Red", this, 1, 0, 1, false);
    private Setting green = new Setting("Green", this, 1, 0, 1, false);
    private Setting blue = new Setting("Blue", this, 1, 0, 1, false);
    private Setting rainbow = new Setting("RainBow", this, true);
    private Setting delay = new Setting("Delay", this, 100, 1, 2000, true);
    private Setting saturation = new Setting("Saturation", this, 36, 0, 100, Slider.NumberType.PERCENT);
    private Setting brightness = new Setting("Brightness", this, 100, 0, 100, Slider.NumberType.PERCENT);

    private ItemShader shader;

    public FramebufferTest() {
        super("FramebufferTest", Category.RENDER);

        shader = ItemShader.ITEM_SHADER;

        setmgr.rSetting(blur);
        setmgr.rSetting(radius);
        setmgr.rSetting(mix);
        setmgr.rSetting(red);
        setmgr.rSetting(green);
        setmgr.rSetting(blue);
        setmgr.rSetting(rainbow);
        setmgr.rSetting(delay);
        setmgr.rSetting(saturation);
        setmgr.rSetting(brightness);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
        shader.red = getColor().getRed() / 255f;
        shader.green = getColor().getGreen() / 255f;
        shader.blue = getColor().getBlue() / 255f;
        shader.blur = blur.getValBoolean();
        shader.mix = mix.getValFloat();
        shader.alpha = 1f;
        shader.useImage = false;
        shader.radius = radius.getValFloat();
        shader.quality = 1;
        shader.startDraw(mc.getRenderPartialTicks());

        Render2DUtil.drawRect(100, 100, 200, 200, getColor().getRGB());
        CustomFontUtil.drawString("Shader on text test", 250, 100, getColor().getRGB());

        shader.stopDraw();
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private Color getColor() {return rainbow.getValBoolean() ? ColorUtils.rainbowRGB(delay.getValInt(), saturation.getValFloat(), brightness.getValFloat()) : new Color(red.getValFloat(), green.getValFloat(), blue.getValFloat());}
}
