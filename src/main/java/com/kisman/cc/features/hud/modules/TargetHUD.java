package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.features.subsystem.subsystems.EnemyManager;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.AnimationUtils;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.UtilityKt;
import com.kisman.cc.util.math.MathUtil;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.AbstractFontRenderer;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

public class TargetHUD extends HudModule {
    private final TimerUtils timer = new TimerUtils();
    private double hpBarWidth;
    private double cdBarWidth;
    private double borderOffset = 5;

    private final Setting astolfo = register(new Setting("Astolfo", this, true));
    private final Setting theme = register(new Setting("Theme", this, TargetHudThemeMode.Simple));

    private final SettingGroup rewriteGroup = register(new SettingGroup(new Setting("Rewrite", this)));
    private final Setting shadow = register(rewriteGroup.add(new Setting("Rewrite Shadow", this, true).setTitle("Shadow")));

    private final SettingGroup noatGroup = register(new SettingGroup(new Setting("Noat", this)));
    private final SettingGroup noatColorsGroup = register(noatGroup.add(new SettingGroup(new Setting("Colors", this))));
    private final SettingGroup noatSidewayColorsGroup = register(noatColorsGroup.add(new SettingGroup(new Setting("Sideway", this))));
    private final Setting noatBackgroundColor = register(noatColorsGroup.add(new Setting("Noat Background Color", this, new Colour(30, 30, 30, 150)).setTitle("Background")));
    private final Setting noatSidewayFirstColor = register(noatSidewayColorsGroup.add(new Setting("Noat Sideway First Color", this, new Colour(218, 186, 255, 255)).setTitle("First")));
    private final Setting noatSidewaySecondColor = register(noatSidewayColorsGroup.add(new Setting("Noat Sideway Second Color", this, new Colour(255, 208, 143, 255)).setTitle("Second")));
    private final Setting noatFontMode = register(noatGroup.add(new Setting("Noat Font Mode", this, NoatTargetHudFontMode.Regular).setTitle("Font Mode")));

    public TargetHUD() {
        super("TargetHud", true);

        setX(500);
        setY(300);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        EntityPlayer target = EnemyManager.INSTANCE.nearestEnemy();

        if(target == null) return;

        switch (theme.getValString()) {
            case "Rewrite":
                drawRewrite(target);
                break;
            case "NoRules":
                drawNoRules(target, getX(), getY(), 150, 45);
                break;
            case "Simple":
                drawSimple(target);
                break;
            case "Astolfo":
                drawAstolfo(target);
                break;
            case "Noat":
                drawNoat(target);
                break;
        }
    }

    private void drawNoat(EntityPlayer player) {
        try {
            GlStateManager.pushMatrix();

            Render2DUtil.drawRectWH(
                    getX(),
                    getY(),
                    150,
                    50,
                    noatBackgroundColor.getColour().getRGB()
            );
            Render2DUtil.drawFace(
                    player,
                    (int) getX() + 5,
                    (int) getY() + 5,
                    40,
                    40
            );
            Render2DUtil.drawGradientSidewaysWH(
                    getX() + 50,
                    getY() + 40,
                    player.getHealth() * 3.9f,
                    5,
                    noatSidewayFirstColor.getColour().getRGB(),
                    noatSidewaySecondColor.getColour().getRGB()
            );
            noatFont(10).drawStringWithShadow(
                    String.format("%.1f", player.getHealth() + player.getAbsorptionAmount()),
                    (int) getX() + 50 + (int) (player.getHealth() * 3.9f) + 5,
                    (int) getY() + 40,
                    -1
            );
            noatFont(30).drawStringWithShadow(
                    player.getName(),
                    (int) getX() + 50,
                    (int) getY() + 5,
                    -1
            );

            int iteration = 0;
            for (ItemStack is : player.inventory.armorInventory) {
                ++iteration;
                try {
                    if (is.isEmpty()) continue;
                    int x = (int) ((getX() + 50) - 90 + (9 - iteration) * 20 + 2);
                    GlStateManager.pushMatrix();
                    Render2DUtil.instance.setZLevel(200);
                    mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, (int) (getY() + 17));
//                    Render2DUtil.renderItemOverlayIntoGUI(x, (int) (getY() + 17) , ""));
                    Render2DUtil.instance.setZLevel(0);
                    GlStateManager.popMatrix();
                } catch(Exception e) {
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.popMatrix();
                }
            }

            GlStateManager.popMatrix();
        } catch (Exception e) {
            GlStateManager.popMatrix();
        }

        setW(150);
        setH(50);
    }

    private AbstractFontRenderer noatFont(
            int size
    ) {
        if(size == 10) {
            return getFont(
                    CustomFontUtil.comfortaa10,
                    CustomFontUtil.comfortaal10,
                    CustomFontUtil.comfortaab10
            );
        } else {
            return getFont(
                    CustomFontUtil.comfortaa30,
                    CustomFontUtil.comfortaal30,
                    CustomFontUtil.comfortaab30
            );
        }
    }

    private AbstractFontRenderer getFont(
            AbstractFontRenderer regular,
            AbstractFontRenderer light,
            AbstractFontRenderer bold
    ) {
        if(noatFontMode.getValEnum() == NoatTargetHudFontMode.Regular) {
            return regular;
        } else if(noatFontMode.getValEnum() == NoatTargetHudFontMode.Light) {
            return light;
        } else {
            return bold;
        }
    }

    private void drawAstolfo(EntityPlayer target) {
        Color color = astolfo.getValBoolean() ? ColorUtils.astolfoColorsToColorObj(100, 100) : new Color(255, 0, 89);

        float x = (float) getX(), y = (float) getY();
        setW(155);
        setH(60);
        double healthWid = (target.getHealth() / target.getMaxHealth() * 120);
        healthWid = MathHelper.clamp(healthWid, 0.0D, 120.0D);
        double check = target.getHealth() < 18 && target.getHealth() > 1 ? 8 : 0;
        hpBarWidth = AnimationUtils.animate(healthWid, hpBarWidth, 0.05);
        Render2DUtil.drawRectWH(x, y, 155, 60, new Color(20, 20, 20, 200).getRGB());
        mc.fontRenderer.drawStringWithShadow(target.getName(), x + 30, y + 4, color.getRGB());
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 1);
        GlStateManager.scale(2.5f, 2.5f, 2.5f);
        GlStateManager.translate(-x - 3, -y - 2, 1);
        mc.fontRenderer.drawStringWithShadow(Math.round((target.getHealth() / 2.0f)) + " \u2764", x + 16, y + 10, color.getRGB());
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);

        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, target.getHeldItemOffhand(), (int) x + 137, (int) y + 7);
        mc.getRenderItem().renderItemIntoGUI(target.getHeldItemOffhand(), (int) x + 137, (int) y + 1);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        try {GuiInventory.drawEntityOnScreen( (int) x + 16, (int) y + 55, 25, target.rotationYaw, -target.rotationPitch, target);} catch (Exception ignored) {}
        Render2DUtil.drawRectWH(x + 30, y + 48, 120, 8, color.darker().darker().darker().getRGB());
        Render2DUtil.drawRectWH(x + 30, y + 48, (float) (hpBarWidth + check), 8, color.darker().getRGB());
        Render2DUtil.drawRectWH(x + 30, y + 48, (float) healthWid, 8, color.getRGB());
    }

    private void drawSimple(EntityPlayer target) {
        int x = (int) getX();
        int y = (int) getY();
        setW(25 + borderOffset * 2 + CustomFontUtil.getStringWidth(target.getName()));
        setH(25);
        int width = (int) (getW());
        int height = (int) getH();

        Render2DUtil.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 170).getRGB());

        try {
            GL11.glPushMatrix();
            mc.getTextureManager().bindTexture(Objects.requireNonNull(mc.player.connection.getPlayerInfo(target.getName())).getLocationSkin());
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect(x, y, 8.0F, 8, 8, 8, 25, 25, 64.0F, 64.0F);
            GL11.glPopMatrix();
        } catch (Exception e) {
            GL11.glPopMatrix();
        }

        CustomFontUtil.drawStringWithShadow(target.getName(), x + borderOffset + 25, y + borderOffset, -1);

        Render2DUtil.drawRectWH(x + borderOffset + 25, y + height - borderOffset - 7, (target.getHealth() / target.getMaxHealth()) * CustomFontUtil.getStringWidth(target.getName()), 7, -1);
    }

    private void drawNoRules(EntityPlayer target, double x, double y, double w, double h) {
        setW(w);
        setH(h);
        double healthOffset = ((target.getHealth() + target.getAbsorptionAmount()) - 0) / (target.getMaxHealth() - 0);
        hpBarWidth += (healthOffset - hpBarWidth) / 4;
        Render2DUtil.drawRoundedRect2(x, y, w, h, 6, new Colour(20, 20, 20, 210).getRGB());
        Render2DUtil.drawRoundedRect2(x + 2, y + (h / 2 - 34 / 2) - 3, 40, 40, 6, 0x40575656);
        Render2DUtil.drawRoundedRect2(x + 45, y + 4, w - 49, 30, 6, 0x40575656);
        CustomFontUtil.drawStringWithShadow("Name: " + ChatFormatting.GRAY + target.getName(), x + 47, y + 4, -1);
        CustomFontUtil.drawStringWithShadow("Distance: " + ChatFormatting.GRAY + MathUtil.round(mc.player.getDistance(target), 2), x + 47, y + 13, -1);
        CustomFontUtil.drawStringWithShadow("Ping: " + ChatFormatting.GRAY + UtilityKt.getPing() + " ms", x + 47, y + 22.5, -1);
        Render2DUtil.drawRoundedRect2(x + 45, y + h - 16, w - 49, 10, 6, 0x40575656);
        Render2DUtil.drawRoundedRect2(x + 47, y + h - 12, 95 * hpBarWidth, 3, 4, ColorUtils.healthColor(target.getHealth() + target.getAbsorptionAmount(), target.getMaxHealth()).getRGB());
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        try {GuiInventory.drawEntityOnScreen((int) x + 21, (int) (y + 44), 18, -30, -target.rotationPitch, target);} catch (Exception ignored) {}
    }

    private void drawRewrite(EntityPlayer target) {
        double x = getX();
        double y = getY();
        setW(120);
        double width = getW();
        double maxSlidersWidth = width - borderOffset * 2;
        double offset = 4 + CustomFontUtil.getFontHeight() * 2;
        setH(borderOffset * 4 + CustomFontUtil.getFontHeight() + offset * 2 + 12 + 27);
        double height = getH();

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
        try {
            GL11.glPushMatrix();
            mc.getTextureManager().bindTexture(mc.getConnection().getPlayerInfo(target.getName()).getLocationSkin());
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect((int) (x + borderOffset + 1), (int) (y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 1), 8.0F, 8, 8, 8, 25, 25, 64.0F, 64.0F);
            GL11.glPopMatrix();
        } catch (Exception e){
            GL11.glPopMatrix();
        }

        //draw health & dist & onGround
        CustomFontUtil.drawString("Health: " + (int) target.getHealth(), x + borderOffset + 27 + 4, y + borderOffset * 3 + CustomFontUtil.getFontHeight(), ColorUtils.astolfoColors(100, 100));
        CustomFontUtil.drawString("Distance: " + (int) mc.player.getDistance(target), x + borderOffset + 27 + 4, y + borderOffset * 3 + CustomFontUtil.getFontHeight() * 2 + 2, ColorUtils.astolfoColors(100, 100));
        CustomFontUtil.drawString("On Ground: " + target.onGround, x + borderOffset + 27 + 4, y + borderOffset * 3 + CustomFontUtil.getFontHeight() * 3 + 4, ColorUtils.astolfoColors(100, 100));

        //draw armor and items in hands
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
        double cooldownPercentage = MathHelper.clamp(target.getCooledAttackStrength(0), 0.1, 1);
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
    }

    private void drawSlider(double x, double y, double sliderWidth, double sliderHeight) {
        if(shadow.getValBoolean()) Render2DUtil.drawShadowSliders(x, y, sliderWidth, sliderHeight, ColorUtils.astolfoColors(100, 100), 1);
        else Render2DUtil.drawRect(x, y, x + sliderWidth, y + sliderHeight, ColorUtils.astolfoColors(100, 100));
    }

    public enum TargetHudThemeMode { Rewrite, NoRules, Simple, Astolfo, Noat }
    public enum NoatTargetHudFontMode { Regular, Light, Bold }
}
