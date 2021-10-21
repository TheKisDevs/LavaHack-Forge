package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.RenderUtil;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class PenisESP extends Module {
    boolean enabled;
    private float spin;
    private float cumsize;
    private float amount;

    public PenisESP() {
        super("PenisESP", "PenisESP", Category.RENDER);
    }

    public void onEnable() {
        this.spin = 0.0f;
        this.cumsize = 0.0f;
        this.amount = 0.0f;
    }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        for (Object o : mc.world.loadedEntityList) {
            if (!(o instanceof EntityPlayer)) continue;
            EntityPlayer player = (EntityPlayer)o;
            double x2 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)mc.getRenderPartialTicks();
            double x = x2 - mc.getRenderManager().viewerPosX;
            double y2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)mc.getRenderPartialTicks();
            double y = y2 - mc.getRenderManager().viewerPosY;
            double z2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)mc.getRenderPartialTicks();
            double z = z2 - mc.getRenderManager().viewerPosZ;
            GL11.glPushMatrix();
            RenderHelper.disableStandardItemLighting();
            RenderUtil.drawPenis(player, x, y, z, this.spin, this.cumsize, this.amount);
            RenderHelper.enableStandardItemLighting();
            GL11.glPopMatrix();
        }
    }
}
