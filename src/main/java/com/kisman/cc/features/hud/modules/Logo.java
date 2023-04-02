package com.kisman.cc.features.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.ShaderableHudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.ColorPattern;
import com.kisman.cc.util.UtilityKt;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import com.kisman.cc.util.render.objects.screen.Icons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class Logo extends ShaderableHudModule {
    private final ColorPattern color = colors();
    private final Setting mode = register(new Setting("Mode", this, LogoMode.CSGO));
    private final Setting image = register(new Setting("Image Mode", this, LogoImage.Old).setVisible(() -> mode.checkValString("Image")));
    private final Setting bold = register(new Setting("Bold", this, false));
    private final Setting glow = register(new Setting("Glow", this, false));
    private final Setting glowOffset = register(new Setting("Glow Offset", this, 5, 0, 20, true));
    private final Setting csgoVersion = register(new Setting("CSGO Version", this, false));

    public Logo() {
        super("Logo", "lava-hack on top", true, true, false);
    }

    public void draw() {
        String name = Kisman.getName();
        String version = Kisman.getVersion();

        if(mode.checkValString("Simple")) {
            int x = (int) getX();
            int y = (int) getY();

            if(glow.getValBoolean()) {
                int glowOffset = this.glowOffset.getValInt();

                preNormalRender = () -> Render2DUtil.drawGlow(x - glowOffset, y - glowOffset, x + CustomFontUtil.getStringWidth(name + version) + glowOffset, y + CustomFontUtil.getFontHeight() + glowOffset, color.color(1, y).getRGB());
            }

            setW(CustomFontUtil.getStringWidth(name + " " + version));
            setH(CustomFontUtil.getFontHeight());

            shaderRender = () -> drawStringWithShadow((bold.getValBoolean() ? TextFormatting.BOLD : "") + name + " " + TextFormatting.GRAY + version, x, y, color.color(1, y).getRGB());
        } else if(mode.checkValString("CSGO")) {
            setX(getX() + 3);
            setY(getY() + 8);

            String text = name + (csgoVersion.getValBoolean() ? TextFormatting.GRAY + " | " + TextFormatting.RESET + Kisman.getVersion() : "") + TextFormatting.GRAY + " | " + TextFormatting.RESET + mc.player.getName() + TextFormatting.GRAY + " | " + TextFormatting.RESET + UtilityKt.getPing() + " ms" + TextFormatting.GRAY + " | " + TextFormatting.RESET + "FPS " + Minecraft.getDebugFPS();
            int x = (int) getX();
            int y = (int) getY();
            int width = 4 + CustomFontUtil.getStringWidth(text);
            int height = 4 + CustomFontUtil.getFontHeight();

            preNormalRender = () -> {
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
                Gui.drawRect(x - 3, y - 1, x + width + 3, y + height + 3, (ColorUtils.getColor(33, 33, 42)));
                Gui.drawRect(x - 2, y - 2, x + width + 2, y + height + 2, (ColorUtils.getColor(45, 45, 55)));
                Gui.drawRect(x - 1, y - 3, x + width + 1, y + height + 1, (ColorUtils.getColor(60, 60, 70)));
                Gui.drawRect(x, y - 4, x + width, y + height, (ColorUtils.getColor(34, 34, 40)));
            };

            shaderRender = () -> {
                drawStringWithShadow((bold.getValBoolean() ? TextFormatting.BOLD : "") + text, x + 2, y + 1, color.color(1, y + 1).getRGB());
                Gui.drawRect(x, y - 5, x + width, y - 4, color.color(1, y - 4).getRGB());
            };

            setX(getX() - 3);
            setY(getY() - 8);
            setW(width + 6);
            setH(height + 11);
        } else if(mode.checkValString("GishCode")) {
            int x = (int) getX() + 4;
            int y = (int) getY() + 4;

            shaderRender = () -> {
                GL11.glPushMatrix();

                GL11.glScaled(1.5, 1.5, 1.5);
                mc.fontRenderer.drawString("LavaHack", x, y, color.color(1, (int) (y * 1.5)).getRGB(), !shaderSetting.getValBoolean());
                GL11.glScaled(0.6, 0.6, 0.6);
                mc.fontRenderer.drawString(TextFormatting.GRAY + Kisman.getVersion(), x + 80, y, -1, !shaderSetting.getValBoolean());
                mc.fontRenderer.drawString(TextFormatting.GRAY + "1.12.2", x + 80, y + 10, -1, !shaderSetting.getValBoolean());

                GL11.glPopMatrix();
            };

            setW(mc.fontRenderer.getStringWidth("LavaHack") * 1.5 + mc.fontRenderer.getStringWidth("1.12.2"));
            setH(30);
        } else {
            int x = (int) getX();
            int y = (int) getY();

            shaderRender = () -> {
                if (image.checkValString("Old")) Icons.LOGO.render(x, y, 50, 50, color.color(1, y));
                else if (image.checkValString("New")) Icons.LOGO_NEW.render(x, y, 80, 80, color.color(1, y));
            };

            if(image.checkValString("Old")) {
                setW(50);
                setH(50);
            } else {
                setW(80);
                setH(80);
            }
        }
    }

    public enum LogoMode {Simple, CSGO, Image, GishCode}
    public enum LogoImage {Old, New}
}
