package com.kisman.cc.hud.hudmodule.player;

import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.client.*;
import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.TimerUtils;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Indicators extends HudModule {
    private final  TimerUtils timer = new TimerUtils();
    private int sliderWidth = 51;
    private int sliderHeight = 2;

    private final String header = "Indicators";
    private double cooldownBarWidth = 0;
    private double hurttimeBarWidth = 0;
    private double speedBarWidth = 0;
    private double healthBarWidth = 0;

    public Indicators() {
        super("Indicators", "", HudCategory.PLAYER);
    }

    public void update() {
        setX(3);
        setY(HUD.instance.indicY.getValInt() + 8);
        setW(sliderWidth + 4);
        setH(6 + getHeight() + 4 * (getHeight() + sliderHeight + 3));
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        int x = getX();
        int y = getY();
        int width = getW();
        int height = getH();
        int offset = 0;
        int offsetHeight = getHeight() + sliderHeight + 3;

        double prevX = mc.player.posX - mc.player.prevPosX;
        double prevZ = mc.player.posZ - mc.player.prevPosZ;
        double lastDist = Math.sqrt(prevX * prevX + prevZ * prevZ);
        double currSpeed = lastDist * 15.3571428571D / 4;

        //math
        double cooldownPercentage = MathHelper.clamp(mc.player.getCooledAttackStrength(0), 0.1, 1);
        cooldownBarWidth = AnimationUtils.animate(cooldownPercentage * 51, cooldownBarWidth, 0.05);

        double hurttimePercentage = MathHelper.clamp(mc.player.hurtTime, 0, 1);
        hurttimeBarWidth = AnimationUtils.animate(hurttimePercentage * 51, hurttimeBarWidth, 0.05);
        if(hurttimeBarWidth < 0) hurttimeBarWidth = 0;

        double speedPercentage = MathHelper.clamp(currSpeed / 2.4, 0.1, 1);
        speedBarWidth = AnimationUtils.animate(speedPercentage * 51, speedBarWidth, 0.05);

        double healthPercentage = mc.player.getHealth() / mc.player.getMaxHealth();

        if(timer.passedMillis(15L)) {
            healthBarWidth = AnimationUtils.animate(healthPercentage * 51, healthBarWidth, 0.05);
            timer.reset();
        }

        //render background
        Gui.drawRect(x + 3, y + 3, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(x + 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(x + 2, y + 2, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(x + 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(x + 1, y + 1, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(x + 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(x - 3, y - 8, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(x - 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(x - 2, y - 7, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(x - 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(x - 1, y - 6, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(x - 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(x, y - 5, x + width, y + height, (ColorUtils.astolfoColors(100, 100)));
        Gui.drawRect(x - 3, y - 1, x + width + 3, y + height + 3, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(x - 2, y - 2, x + width + 2, y + height + 2, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(x - 1, y - 3, x + width + 1, y + height + 1, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(x, y - 4, x + width, y + height, (ColorUtils.getColor(34, 34, 40)));

        //render header
        drawStringWithShadow(header, x + (width / 2 - getStringWidth(header) / 2), y + 2, ColorUtils.astolfoColors(100, 100));
        offset += getHeight() + 6;

        //render cooldown
        drawStringWithShadow("Cooldown", x + 2, y + offset, ColorUtils.astolfoColors(100, 100));
        Render2DUtil.drawRect(x + 2, y + offset + getHeight() + 1, x + cooldownBarWidth, y + offset + getHeight() + 3, ColorUtils.astolfoColors(100, 100));
        offset += offsetHeight;

        //render hurttime
        drawStringWithShadow("HurtTime", x + 2, y + offset, ColorUtils.astolfoColors(100, 100));
        if(hurttimeBarWidth > 0) Render2DUtil.drawRect(x + 2, y + offset + getHeight() + 1, x + hurttimeBarWidth, y + offset + getHeight() + 3, ColorUtils.astolfoColors(100, 100));
        offset += offsetHeight;

        //render speed
        drawStringWithShadow("Speed", x + 2, y + offset, ColorUtils.astolfoColors(100, 100));
        Render2DUtil.drawRect(x + 2, y + offset + getHeight() + 1, x + speedBarWidth, y + offset + getHeight() + 3, ColorUtils.astolfoColors(100, 100));
        offset += offsetHeight;

        //render health
        drawStringWithShadow("Health", x + 2, y + offset, ColorUtils.astolfoColors(100, 100));
        Render2DUtil.drawRect(x + 2, y + offset + getHeight() + 1, x + healthBarWidth, y + offset + getHeight() + 3, ColorUtils.astolfoColors(100, 100));
    }

    private void drawStringWithShadow(String text, int x, int y, int color) {
        if(CustomFont.turnOn) CustomFontUtil.consolas15.drawStringWithShadow(text, x, y, color);
        else mc.fontRenderer.drawStringWithShadow(text, x, y, color);
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
