package com.kisman.cc.hud.hudmodule.combat;

import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.TimerUtils;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class TargetHUD extends HudModule {
    private EntityPlayer target = null;
    private TimerUtils timer = new TimerUtils();
    private double hpBarWidth;
    private double cdBarWidth;
    private double borderOffset = 5;

    public TargetHUD() {
        super("TargetHud", "TargetInfo", HudCategory.COMBAT);
    }

    public void update() {
        if(AutoRer.currentTarget != null) target = AutoRer.currentTarget;
        else if(target == null) if(KillAura.instance.target != null) target = KillAura.instance.target;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if(target == null) return;

        switch (HUD.instance.thudTheme.getValString()) {
            case "Vega":
                drawVega();
                break;
            case "Rewrite":
                drawRewrite();
                break;
        }
    }

    private void drawRewrite() {
        boolean isMatrix = false;
        try {
            double x = 503;
            double y = 317;
            double width = 120;
            double maxSlidersWidth = width - borderOffset * 2;
            double offset = 4 + CustomFontUtil.getFontHeight() * 2;
            double height = borderOffset * 4 + CustomFontUtil.getFontHeight() + offset * 2 + 12 + 27;

            int count = 0;

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

            //draw target's name
            CustomFontUtil.drawCenteredStringWithShadow(target.getName(), x + width / 2, y + borderOffset, ColorUtils.astolfoColors(100, 100));

            //draw face background
            Render2DUtil.drawRect(x + borderOffset, y + borderOffset * 3 + CustomFontUtil.getFontHeight(), (x + borderOffset + 27), y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 27, ColorUtils.astolfoColors(100, 100));

            //draw face texture
            GL11.glPushMatrix();
            isMatrix = true;
            mc.getTextureManager().bindTexture(mc.getConnection().getPlayerInfo(target.getName()).getLocationSkin());
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect((int) (x + borderOffset + 1), (int) (y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 1), 8.0F, 8, 8, 8, 25, 25, 64.0F, 64.0F);
            GL11.glPopMatrix();

            //draw health & dist & onGround
            CustomFontUtil.drawString("Health: " + (int) target.getHealth(), x + borderOffset + 27 + 4, y + borderOffset * 3 + CustomFontUtil.getFontHeight(), ColorUtils.astolfoColors(100, 100));
            CustomFontUtil.drawString("Distance: " + (int) mc.player.getDistance(target), x + borderOffset + 27 + 4, y + borderOffset * 3 + CustomFontUtil.getFontHeight() * 2 + 2, ColorUtils.astolfoColors(100, 100));
            CustomFontUtil.drawString("On Ground: " + target.onGround, x + borderOffset + 27 + 4, y + borderOffset * 3 + CustomFontUtil.getFontHeight() * 3 + 4, ColorUtils.astolfoColors(100, 100));

            //draw armor and item in hands
            double posX = x + borderOffset;
            for (final ItemStack item : target.getArmorInventoryList()) {
                if(item.isEmpty) continue;
                GL11.glPushMatrix();
                GL11.glTranslated(posX, y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 27 + 0.5, 0);
                GL11.glScaled(0.8, 0.8, 0.8);
                mc.getRenderItem().renderItemIntoGUI(item, 0, 0);
                GL11.glPopMatrix();
                posX += 12;
            }
            if(!target.getHeldItemMainhand().isEmpty) {
                GL11.glPushMatrix();
                GL11.glTranslated(posX, y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 27 + 0.5, 0);
                GL11.glScaled(0.8, 0.8, 0.8);
                mc.getRenderItem().renderItemIntoGUI(target.getHeldItemMainhand(), 0, 0);
                GL11.glPopMatrix();
                posX += 12;
            }
            if(!target.getHeldItemOffhand().isEmpty){
                GL11.glPushMatrix();
                GL11.glTranslated(posX, y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 27 + 0.5, 0);
                GL11.glScaled(0.8, 0.8, 0.8);
                mc.getRenderItem().renderItemIntoGUI(target.getHeldItemOffhand(), 0, 0);
                GL11.glPopMatrix();
            }

            //draw cooldown slider
            double cooldownPercentage = MathHelper.clamp(mc.player.getCooledAttackStrength(0), 0.1, 1);
            cdBarWidth = AnimationUtils.animate(cooldownPercentage * maxSlidersWidth, cdBarWidth, 0.05);
            CustomFontUtil.drawStringWithShadow("Cooldown", x + borderOffset, y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 27 + 4 + 12, ColorUtils.astolfoColors(100, 100));
            drawSlider(x + borderOffset, y + borderOffset * 3 + CustomFontUtil.getFontHeight() * 2 + 27 + 6 + 12, cdBarWidth, CustomFontUtil.getFontHeight());
            count++;

            //draw health slider
            if(timer.passedMillis(15)) {
                hpBarWidth = AnimationUtils.animate((target.getHealth() / target.getMaxHealth()) * maxSlidersWidth, hpBarWidth, 0.05);
                timer.reset();
            }
            CustomFontUtil.drawStringWithShadow("Health", x + borderOffset, y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 27 + 4 + (count * offset) + 12, ColorUtils.astolfoColors(100, 100));
            drawSlider(x + borderOffset, y + borderOffset * 3 + CustomFontUtil.getFontHeight() * 2 + 27 + 6 + (count * offset) + 12, hpBarWidth, CustomFontUtil.getFontHeight());
        } catch (Exception e) {if(isMatrix) GL11.glPopMatrix();}
    }

    private void drawSlider(double x, double y, double sliderWidth, double sliderHeight) {
        if(HUD.instance.thudShadowSliders.getValBoolean()) Render2DUtil.drawShadowSliders(x, y, sliderWidth, sliderHeight, ColorUtils.astolfoColors(100, 100), 1);
        else Render2DUtil.drawRect(x, y, x + sliderWidth, y + sliderHeight, ColorUtils.astolfoColors(100, 100));
    }

    private void drawVega() {
        try {
            final ScaledResolution scaledResolution = new ScaledResolution(mc);
            double renderX = 503;
            double renderY = 317;
            float maxX = Math.max(40, CustomFontUtil.getStringWidth("HP: " + (int) target.getHealth() + " | Dist: " + mc.player.getDistance(target)) + 70);
            if(timer.passedMillis(15)) {
                hpBarWidth = AnimationUtils.animate((target.getHealth() / target.getMaxHealth()) * maxX, hpBarWidth, 0.05);
                timer.reset();
            }
            int color  = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : ColorUtils.rainbow(1, 1);
            Render2DUtil.drawRect(renderX - 4, renderY - 3, (renderX + 4 + maxX), (int) renderY + 49, ColorUtils.getColor(55, 55, 63));
            Render2DUtil.drawRect(renderX - 3, renderY - 2, (renderX + 3 + maxX), (int) renderY + 48, ColorUtils.getColor(95, 95, 103));
            Render2DUtil.drawRect(renderX - 2, renderY - 1, (renderX + 2 + maxX), (int) renderY + 47, ColorUtils.getColor(65, 65, 73));
            Render2DUtil.drawRect(renderX - 1, renderY, (renderX + 1 + maxX), (int) renderY + 46, ColorUtils.getColor(25, 25, 33));
            Render2DUtil.drawRect(renderX + 2, renderY + 42, (renderX + maxX), (int) renderY + 45, ColorUtils.getColor(48, 48, 58));
            Render2DUtil.drawRect(renderX + 1, renderY + 2, (renderX + 28), (int) renderY + 29, color);
            Render2DUtil.drawRect(renderX + 2, renderY + 3, (int) (renderX + 27), (int) renderY + 28, ColorUtils.getColor(25, 25, 33));
            Gui.drawRect((int) renderX, (int) renderY + 37 + 5, (int) (renderX + hpBarWidth), (int) renderY + 40 + 5, color);
            Gui.drawRect((int) renderX + 1, (int) renderY + 38 + 5, (int) (renderX - 1 + hpBarWidth), (int) renderY + 39 + 5, ColorUtils.getColor(0, 0, 0));

            mc.getTextureManager().bindTexture(mc.getConnection().getPlayerInfo(target.getName()).getLocationSkin());
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect((int) (renderX + 2), (int) (renderY + 3), 8.0F, 8, 8, 8, 25, 25, 64.0F, 64.0F);
            CustomFontUtil.drawString("HP: " + (int) target.getHealth() + " | Dist: " + mc.player.getDistance(target), renderX + 1 + 27 + 5, renderY + 2, -1);
            CustomFontUtil.drawString(target.getName(), renderX + 1 + 27 + 5, renderY + 4 + CustomFontUtil.getFontHeight(), -1);
            int posX = scaledResolution.getScaledWidth() / 2 + 53;
            for (final ItemStack item : target.getArmorInventoryList()) {
                if(item.isEmpty) continue;
                GL11.glPushMatrix();
                GL11.glTranslated(posX - 27, renderY + 29 + 0.5, 0);
                GL11.glScaled(0.8, 0.8, 0.8);
                mc.getRenderItem().renderItemIntoGUI(item, 0, 0);
                GL11.glPopMatrix();
                posX += 12;
            }
            if(!target.getHeldItemMainhand().isEmpty) {
                GL11.glPushMatrix();
                GL11.glTranslated(posX - 27, renderY + 29 + 0.5, 0);
                GL11.glScaled(0.8, 0.8, 0.8);
                mc.getRenderItem().renderItemIntoGUI(target.getHeldItemMainhand(), 0, 0);
                GL11.glPopMatrix();
                posX += 12;
            }
            if(!target.getHeldItemOffhand().isEmpty){
                GL11.glPushMatrix();
                GL11.glTranslated(posX - 27, renderY + 29 + 0.5, 0);
                GL11.glScaled(0.8, 0.8, 0.8);
                mc.getRenderItem().renderItemIntoGUI(target.getHeldItemOffhand(), 0, 0);
                GL11.glPopMatrix();
            }
        } catch (Exception ignored) {}
    }
}
