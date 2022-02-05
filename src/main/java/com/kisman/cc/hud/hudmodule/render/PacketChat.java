package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.hud.hudmodule.render.packetchat.Log;
import com.kisman.cc.hud.hudmodule.render.packetchat.Message;
import com.kisman.cc.module.client.CustomFont;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.util.AnimationUtils;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.TimerUtils;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketChat extends HudModule {
    private final TimerUtils timer = new TimerUtils();
    private int sliderWidth = 200;
    private int sliderHeight = 20;

    private final String header = "PacketChat";
    private final String stringWithMaxLength = "Cooldown";
    private double borderOffset = 5;

    private Log logs = new Log();

    public PacketChat() {
        super("PacketChat", "", HudCategory.PLAYER);

    }

    public void update() {
        setX(3);
        setY(HUD.instance.indicY.getValInt() + 8);

        switch (HUD.instance.indicThemeMode.getValString()) {
            case "Default": {
                setW(sliderWidth + 4);
                setH(6 + getHeight() + 4 * (getHeight() + sliderHeight + 3));
                break;
            }
            case "Rewrite": {
                setW(borderOffset * 3 + CustomFontUtil.getStringWidth(stringWithMaxLength) * 2);
                setH(borderOffset * 7 + CustomFontUtil.getFontHeight() * 5);
                break;
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        switch (HUD.instance.indicThemeMode.getValString()) {
            case "Default":
                drawDefault();
                break;
            case "Rewrite":
                drawRewrite();
                break;
        }
    }

    private void drawRewrite() {
        double x = getX();
        double y = getY();
        double width = getW();
        double height = getH();
        double offset = CustomFontUtil.getFontHeight() + borderOffset;
        int count = 0;

        double prevX = mc.player.posX - mc.player.prevPosX;
        double prevZ = mc.player.posZ - mc.player.prevPosZ;
        double lastDist = Math.sqrt(prevX * prevX + prevZ * prevZ);
        double currSpeed = lastDist * 15.3571428571D / 4;


        double healthPercentage = mc.player.getHealth() / mc.player.getMaxHealth();


        //draw background
        Render2DUtil.drawRect(x + 3, y + 3, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x + 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x + 2, y + 2, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x + 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x + 1, y + 1, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x + 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x - 3, y - 8, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 2, y - 7, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 1, y - 6, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x - 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x, y - 5, x + width, y + height, (ColorUtils.astolfoColors(100, 100)));
        Render2DUtil.drawRect(x - 3, y - 1, x + width + 3, y + height + 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 2, y - 2, x + width + 2, y + height + 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 1, y - 3, x + width + 1, y + height + 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x, y - 4, x + width, y + height, (ColorUtils.getColor(34, 34, 40)));

        //draw header
        CustomFontUtil.drawCenteredStringWithShadow(header, x + width / 2, y + borderOffset, ColorUtils.astolfoColors(100, 100));

        //draw cooldown
        CustomFontUtil.drawStringWithShadow("Cooldown", x + borderOffset, y + borderOffset * 3 + CustomFontUtil.getFontHeight(), ColorUtils.astolfoColors(100, 100));
        count++;

        //draw hurttime
        CustomFontUtil.drawStringWithShadow("HurtTime", x + borderOffset, y + borderOffset * 3 + CustomFontUtil.getFontHeight() + (offset * count), ColorUtils.astolfoColors(100, 100));
        count++;

        //draw speed
        CustomFontUtil.drawStringWithShadow("Speed", x + borderOffset, y + CustomFontUtil.getFontHeight() + (offset * count), ColorUtils.astolfoColors(100, 100));
        count++;

        for(Message message : logs.messages)
        {
            CustomFontUtil.drawStringWithShadow(message.message, x + borderOffset, y + CustomFontUtil.getFontHeight() + (offset * count), ColorUtils.astolfoColors(100, 100));
            count++;
        }

    }

    private void drawSlider(double x, double y, double sliderWidth, double sliderHeight) {
        if(HUD.instance.indicShadowSliders.getValBoolean()) Render2DUtil.drawShadowSliders(x, y, sliderWidth, sliderHeight, ColorUtils.astolfoColors(100, 100), 1);
        else Render2DUtil.drawRect(x, y, x + sliderWidth, y + sliderHeight, ColorUtils.astolfoColors(100, 100));
    }

    private void drawDefault() {
        double x = getX();
        double y = getY();
        double width = getW();
        double height = getH();
        int offset = 0;
        int offsetHeight = getHeight() + sliderHeight + 3;

        double prevX = mc.player.posX - mc.player.prevPosX;
        double prevZ = mc.player.posZ - mc.player.prevPosZ;
        double lastDist = Math.sqrt(prevX * prevX + prevZ * prevZ);
        double currSpeed = lastDist * 15.3571428571D / 4;


        //draw background
        Render2DUtil.drawRect(x + 3, y + 3, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x + 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x + 2, y + 2, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x + 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x + 1, y + 1, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x + 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x - 3, y - 8, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 2, y - 7, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 1, y - 6, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x - 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x, y - 5, x + width, y + height, (ColorUtils.astolfoColors(100, 100)));
        Render2DUtil.drawRect(x - 3, y - 1, x + width + 3, y + height + 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 2, y - 2, x + width + 2, y + height + 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 1, y - 3, x + width + 1, y + height + 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x, y - 4, x + width, y + height, (ColorUtils.getColor(34, 34, 40)));

        //draw header
        drawStringWithShadow(header, x + (width / 2 - getStringWidth(header) / 2), y + 2, ColorUtils.astolfoColors(100, 100));
        offset += getHeight() + 6;

        //draw cooldown
        drawStringWithShadow("Cooldown", x + 2, y + offset, ColorUtils.astolfoColors(100, 100));
        offset += offsetHeight;

        //draw hurttime
        drawStringWithShadow("HurtTime", x + 2, y + offset, ColorUtils.astolfoColors(100, 100));
        offset += offsetHeight;

        //draw health
        drawStringWithShadow("Health", x + 2, y + offset, ColorUtils.astolfoColors(100, 100));
    }

    private void drawStringWithShadow(String text, double x, double y, int color) {
        if(CustomFont.turnOn) CustomFontUtil.consolas15.drawStringWithShadow(text, x, y, color);
        else mc.fontRenderer.drawStringWithShadow(text, (int) x, (int) y, color);
    }

    private int getHeight() {
        if(CustomFont.turnOn) return  CustomFontUtil.consolas15.getStringHeight();
        else return mc.fontRenderer.FONT_HEIGHT;
    }

    private int getStringWidth(String text) {
        if(CustomFont.turnOn) return  CustomFontUtil.consolas15.getStringWidth(text);
        else return mc.fontRenderer.getStringWidth(text);
    }
}
