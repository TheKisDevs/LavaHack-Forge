package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.render.Rendering;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Tracers2 extends Module {

    public Tracers2(){
        super("Tracers2", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.player == null || mc.world == null)
            return;

        if(!this.isToggled())
            return;

        Entity entity = mc.world.entityList.stream().min((o1, o2) -> Float.compare(mc.player.getDistance(o1), mc.player.getDistance(o2))).orElse(null);

        if(entity == mc.player || entity == null)
            return;

        double x = entity.posX - mc.renderManager.viewerPosX;
        double y = entity.posY + (entity.boundingBox.maxY / 2.0) - mc.renderManager.viewerPosY;
        double z = entity.posZ - mc.renderManager.viewerPosZ;

        Rendering.setup();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        GL11.glLineWidth(3.0f);
        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        buf.pos(0.0, 0.0, 0.0).color(255, 255, 255, 255).endVertex();
        buf.pos(x, y, z).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        Rendering.release();
    }
}
