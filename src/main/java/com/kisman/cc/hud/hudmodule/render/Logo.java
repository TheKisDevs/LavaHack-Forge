package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.util.LogoMode;
import com.kisman.cc.util.Render2DUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Logo extends HudModule{
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    public static LogoMode logoMode = LogoMode.SIMPLE;

    ResourceLocation LOGO_TEXTURE_1 = new ResourceLocation("kismancc:logo.png");
    ResourceLocation LOGO_TEXTURE_2 = new ResourceLocation("kismancc:bird.png");
    ResourceLocation LOGO_TEXTURE_3 = new ResourceLocation("kismancc:3dBird.png");
    ResourceLocation LOGO_TEXTURE_4 = new ResourceLocation("kismancc:kisman.png");
    ResourceLocation LOGO_TEXTURE_5 = new ResourceLocation("kismancc:nevis.png");

    public static int height = 0;
    public static int x = 1;
    public static int y = 1;
    public static int x1 = x = Minecraft.getMinecraft().fontRenderer.getStringWidth(Kisman.NAME + " " + Kisman.VERSION);
    public static int y1 = y + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;

    String name = Kisman.NAME;
    String version = Kisman.VERSION;

    public Logo() {
        super("Logo", "lava", HudCategory.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        if(logoMode == LogoMode.SIMPLE) {
            if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
                fr.drawStringWithShadow(TextFormatting.WHITE + name + " " + TextFormatting.GRAY + version, 1, 1, -1);
                setHeight(1 + fr.FONT_HEIGHT + 2);
            }
        } else if(logoMode == LogoMode.ADVANCED) {
            if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
                Render2DUtil.drawTexture(LOGO_TEXTURE_1, 0, 0, 50, 50);
                setHeight(51);
            }
        } else if(
            event.getType() == RenderGameOverlayEvent.ElementType.TEXT && 
            logoMode == LogoMode.SIMPLEBIRD
            ) {
            Render2DUtil.drawTexture(LOGO_TEXTURE_2, 0, 0 ,50, 50);
        } else if(
            event.getType() == RenderGameOverlayEvent.ElementType.TEXT && 
            logoMode == LogoMode.BIRD
            ) {
            Render2DUtil.drawTexture(LOGO_TEXTURE_3, 0, 0 ,50, 48);
        } else if(
            event.getType() == RenderGameOverlayEvent.ElementType.TEXT && 
            logoMode == LogoMode.KISMAN
            ) {
            Render2DUtil.drawTexture(LOGO_TEXTURE_4, 0, 0 ,70, 70);
            setHeight(71);
        } else if(
            event.getType() == RenderGameOverlayEvent.ElementType.TEXT && 
            logoMode == LogoMode.NEVIS
            ) {
            Render2DUtil.drawTexture(LOGO_TEXTURE_5, 0, 0 ,50, 50);
        }
    }

    public void setLogoMode(LogoMode logoMode) {
        this.logoMode = logoMode;
    }

    public int getHeight() {
        return Logo.height;
    }

    public void setHeight(int height) {
        Logo.height = height;
    }
}
