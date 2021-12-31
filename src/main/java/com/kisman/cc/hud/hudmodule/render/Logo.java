package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.module.client.CustomFont;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.util.LogoMode;
import com.kisman.cc.util.Render2DUtil;

import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Logo extends HudModule {
    public static int height = 0;
    public static int x = 1;
    public static int y = 1;
    public static int x1 = x = CustomFontUtil.getStringWidth(Kisman.NAME + " " + Kisman.VERSION);
    public static int y1 = y + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;

    private String name = Kisman.NAME;
    private String version = Kisman.VERSION;

    public Logo() {
        super("Logo", "lava-hack on top", HudCategory.RENDER);
    }

    public void update() {
        name = Kisman.getName();
        version = Kisman.getVersion();
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if(HUD.instance.logoMode.getValString().equals("Simple")) {
            if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
                int color = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : -1;

                if(HUD.instance.logoGlow.getValBoolean()) {
                    int glowOffset = HUD.instance.glowOffset.getValInt();
                    Render2DUtil.drawGlow(1 - glowOffset, 1 - glowOffset, 1 + CustomFontUtil.getStringWidth(name + version) + glowOffset, 1 + CustomFontUtil.getFontHeight() + glowOffset, color);
                }

                CustomFontUtil.drawStringWithShadow((HUD.instance.logoBold.getValBoolean() ? TextFormatting.BOLD : "") + name + " " + TextFormatting.GRAY + version, 1, 1, color);
                setHeight(1 + CustomFontUtil.getFontHeight() + 2);
            }
        } else if(HUD.instance.logoMode.getValString().equals("CSGO")) {
            String text =((CustomFont.instance.mode.getValString().equals("Verdana") ? TextFormatting.BOLD : "")   +  name) + TextFormatting.GRAY + " | " + TextFormatting.RESET + mc.player.getName() + TextFormatting.GRAY + " | " + TextFormatting.RESET + (mc.isSingleplayer() ? 0 : Kisman.instance.serverManager.getPing()) + " mc" + TextFormatting.GRAY + " | " + TextFormatting.RESET + "FPS " + Minecraft.getDebugFPS();
            int x = 3;
            int y = 8;
            int width = 4 + CustomFontUtil.getStringWidth(text);
            int height = 4 + CustomFontUtil.getFontHeight();

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

            CustomFontUtil.drawStringWithShadow((HUD.instance.logoBold.getValBoolean() ? TextFormatting.BOLD : "") + text, x + 2, y + 2, ColorUtils.astolfoColors(100, 100));
        }
    }

    public int getHeight() {
        return Logo.height;
    }

    public void setHeight(int height) {
        Logo.height = height;
    }
}
