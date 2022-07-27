package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.Timer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glLineWidth;

public class Breadcrumbs extends Module {

    private final Setting smooth = register(new Setting("Smooth", this, false));
    private final Setting lineWidth = register(new Setting("LineWidth", this, 1.0, 0.5, 5.0, false));
    private final Setting fadeOut = register(new Setting("FadeOut", this, false));
    private final Setting fadeAfterTicks = register(new Setting("FadeAfterTicks", this, 100, 1, 400, true).setVisible(fadeOut::isVisible));
    private final Setting fadeOutTicks = register(new Setting("FadeOutTicks", this, 10, 1, 20, true));
    private final Setting color = register(new Setting("Color", this, new Colour(255, 255, 255, 255)));

    public Breadcrumbs(){
        super("Breadcrumbs", Category.RENDER);
    }

    private final Queue<AxisAlignedBB> lines = new ConcurrentLinkedQueue<>();

    private final Timer smoothTimer = new Timer();

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.player == null || mc.world == null){
            smoothTimer.reset();
            return;
        }

        if(smooth.getValBoolean()){
            if(!smoothTimer.passedMs(50))
                return;
            smoothTimer.reset();
            draw();
            return;
        }

        drawSmooth(event.getPartialTicks());
    }

    @Override
    public void onDisable(){
        super.onDisable();
        smoothTimer.reset();
    }

    private void draw(){
        AxisAlignedBB aabb = new AxisAlignedBB(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ, mc.player.posX, mc.player.posY, mc.player.posZ);
        lines.offer(aabb);

        int r = color.getColour().getRed();
        int g = color.getColour().getGreen();
        int b = color.getColour().getBlue();
        int a = color.getColour().getAlpha();

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(lineWidth.getValFloat());
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        for(AxisAlignedBB bb : lines){
            buf.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            buf.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        }

        tessellator.draw();

        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(GL_FLAT);
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawSmooth(float partialTicks){

    }
}
