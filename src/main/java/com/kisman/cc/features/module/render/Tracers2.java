package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.math.MathUtil;
import com.kisman.cc.util.render.Rendering;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Tracers2 extends Module {

    private final Setting range = register(new Setting("Range", this, 500, 10, 500, true));
    private final Setting lineWidth = register(new Setting("LineWidth", this, 1.5, 0.5, 5, false));
    private final Setting color = register(new Setting("Color", this, new Colour(255, 255, 255, 255)));

    public Tracers2(){
        super("Tracers", Category.RENDER, true);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.player == null || mc.world == null)
            return;

        if(!this.isToggled())
            return;

        for(Entity entity : mc.world.loadedEntityList){
            if(!(entity instanceof EntityPlayer) || entity == mc.player) continue;

            if(mc.player.getDistance(entity) > range.getValDouble()) continue;

            Vec3d eyes = new Vec3d(0, 0, 1).rotatePitch(-((float) Math.toRadians(mc.player.rotationPitch))).rotateYaw(-((float) Math.toRadians(mc.player.rotationYaw)));

            Vec3d entityPos = MathUtil.interpolateEntity(entity, event.getPartialTicks());

            double x = entityPos.x - mc.renderManager.viewerPosX;
            double y = entityPos.y + ((entity.boundingBox.maxY - entity.boundingBox.minY) / 2.0) - mc.renderManager.viewerPosY;
            double z = entityPos.z - mc.renderManager.viewerPosZ;

            boolean viewBobbing = mc.gameSettings.viewBobbing;
            mc.gameSettings.viewBobbing = false;
            mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
            Rendering.setup();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buf = tessellator.getBuffer();
            GL11.glLineWidth(lineWidth.getValFloat());
            buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            buf.pos(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z).color(255, 255, 255, 255).endVertex();
            buf.pos(x, y, z).color(255, 255, 255, 255).endVertex();
            tessellator.draw();
            Rendering.release();
            mc.gameSettings.viewBobbing = viewBobbing;
            mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
        }
    }
}
