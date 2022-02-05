package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.module.render.shader.shaders.ItemShader;
import com.kisman.cc.oldclickgui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class FramebufferTest extends Module {
    private Setting degrees = new Setting("Degrees", this, 200, 0, 360, true);
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

        setmgr.rSetting(degrees);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        ScaledResolution sr = event.getResolution();
        Render2DUtil.drawProgressCircle(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 10, Color.GREEN, 3f, 200d, (int) 360);
        Render2DUtil.drawProgressCircle(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 + 30, 10, Color.GREEN, 3f, 200d, (int) 4);
    }

    private Color getColor() {return rainbow.getValBoolean() ? ColorUtils.rainbowRGB(delay.getValInt(), saturation.getValFloat(), brightness.getValFloat()) : new Color(red.getValFloat(), green.getValFloat(), blue.getValFloat());}
}
