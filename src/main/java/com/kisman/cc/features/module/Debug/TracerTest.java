package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.render.Rendering;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;

public class TracerTest extends Module {

    public TracerTest(){
        super("TracerTest", Category.DEBUG);
    }

    @Override
    public void onEnable(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable(){
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event){
        if(mc.player == null || mc.world == null)
            return;

        if(!isToggled())
            return;

        Entity entity = mc.world.loadedEntityList.stream().min(Comparator.comparingDouble(o -> mc.player.getDistanceSq(o))).orElse(null);

        if(entity == null)
            return;

        Rendering.setup();
        GL11.glLineWidth(2.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buf.pos(Display.getWidth() / 2.0, Display.getHeight() / 2.0, 0.0).endVertex();
        buf.pos(entity.posX - mc.getRenderManager().viewerPosX, entity.posY - mc.getRenderManager().viewerPosY, entity.posZ - mc.getRenderManager().viewerPosZ);
        tessellator.draw();
        Rendering.release();
    }
}
