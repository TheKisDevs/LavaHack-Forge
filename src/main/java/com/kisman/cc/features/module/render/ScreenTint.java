package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Rendering;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(
        name = "ScreenTint",
        category = Category.RENDER,
        wip = true
)
public class ScreenTint extends Module {

    private final Setting renderMode = register(new Setting("RenderMode", this, RenderModes.Static));

    private final Setting speed = register(new Setting("Speed", this, 1.0, 0.25, 5.0, false));
    private final Setting saturation = register(new Setting("Saturation", this, 100, 10, 100, true));
    private final Setting brightness = register(new Setting("Brightness", this, 50, 0, 100, true));

    private final Setting color1 = register(new Setting("Color 1", this, new Colour(255, 255, 255, 0)));
    private final Setting color2 = register(new Setting("Color 2", this, new Colour(255, 255, 255, 0)).setVisible(() -> renderMode.getValEnum() == RenderModes.Gradient || renderMode.getValEnum() == RenderModes.Chroma));
    private final Setting color3 = register(new Setting("Color 3", this, new Colour(255, 255, 255, 0)).setVisible(() -> renderMode.getValEnum() == RenderModes.Chroma));
    private final Setting color4 = register(new Setting("Color 4", this, new Colour(255, 255, 255, 0)).setVisible(() -> renderMode.getValEnum() == RenderModes.Chroma));

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text.Pre event){
        if(mc.player == null || mc.world == null)
            return;

        ScaledResolution sr = new ScaledResolution(mc);

        int x = 0;
        int y = 0;
        int w = sr.getScaledWidth();
        int h = sr.getScaledHeight();

        Color[] colors = getColor();
        Color c1 = colors[0];
        Color c2 = colors[1];
        Color c3 = colors[2];
        Color c4 = colors[3];

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        Rendering.start();
//        GlStateManager.pushMatrix();
//        GlStateManager.enableBlend();
//        GlStateManager.disableTexture2D();
//        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//        GlStateManager.shadeModel(7425);
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y, 0.0D).color(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha()).endVertex();
        bufferbuilder.pos(w, y, 0.0D).color(c2.getRed(), c2.getGreen(), c2.getBlue(), c2.getAlpha()).endVertex();
        bufferbuilder.pos(w, h, 0.0D).color(c3.getRed(), c3.getGreen(), c3.getBlue(), c3.getAlpha()).endVertex();
        bufferbuilder.pos(x, h, 0.0D).color(c4.getRed(), c4.getGreen(), c4.getBlue(), c4.getAlpha()).endVertex();
        tessellator.draw();
//        GlStateManager.shadeModel(7424);
//        GlStateManager.enableTexture2D();
//        GlStateManager.disableBlend();
//        GlStateManager.popMatrix();
        Rendering.end();
    }

    private Color[] getColor(){
        if(renderMode.getValEnum() == RenderModes.Static){
            return new Color[]{color1.getColour().getColor(), color1.getColour().getColor(), color1.getColour().getColor(), color1.getColour().getColor()};
        }
        if(renderMode.getValEnum() == RenderModes.Gradient){
            return new Color[]{color1.getColour().getColor(), color1.getColour().getColor(), color2.getColour().getColor(), color2.getColour().getColor()};
        }
        if(renderMode.getValEnum() == RenderModes.Rainbow){
            Color color = ColorUtils.rainbow2(0, saturation.getValInt(), brightness.getValInt(), color1.getColour().getAlpha(), speed.getValDouble()).getColor();
            return new Color[]{color, color, color, color};
        }
        if(renderMode.getValEnum() == RenderModes.Chroma){
            return new Color[]{color1.getColour().getColor(), color2.getColour().getColor(), color3.getColour().getColor(), color4.getColour().getColor()};
        }
        if(renderMode.getValEnum() == RenderModes.ChromaRainbow){
            long millis = System.currentTimeMillis();
            Color c1 = ColorUtils.rainbow3(millis, 0, saturation.getValInt(), brightness.getValInt(), color1.getColour().getAlpha(), speed.getValDouble()).getColor();
            Color c2 = ColorUtils.rainbow3(millis, 90, saturation.getValInt(), brightness.getValInt(), color1.getColour().getAlpha(), speed.getValDouble()).getColor();
            Color c3 = ColorUtils.rainbow3(millis, 180, saturation.getValInt(), brightness.getValInt(), color1.getColour().getAlpha(), speed.getValDouble()).getColor();
            Color c4 = ColorUtils.rainbow3(millis, 270, saturation.getValInt(), brightness.getValInt(), color1.getColour().getAlpha(), speed.getValDouble()).getColor();
            return new Color[]{c1, c2, c3, c4};
        }
        return new Color[]{Rendering.DUMMY_COLOR.getColor(), Rendering.DUMMY_COLOR.getColor(), Rendering.DUMMY_COLOR.getColor(), Rendering.DUMMY_COLOR.getColor()};
    }

    private enum RenderModes {
        Static,
        Gradient,
        Rainbow,
        Chroma,
        ChromaRainbow
    }
}
