package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Trails extends Module {
    private Setting lineWidth = new Setting("LineWidth", this, Double.longBitsToDouble(Double.doubleToLongBits(17.27955007873751) ^ 0x7FC14790980DC597L), Double.longBitsToDouble(Double.doubleToLongBits(93.95772593779462) ^ 0x7FB77D4B61BB56F7L), Double.longBitsToDouble(Double.doubleToLongBits(0.1654465991727702) ^ 0x7FD12D5AAA573A5DL), false);
    private Setting color = new Setting("Color", this, "Color", new float[] {1, 1, 1, 1});

    public Color color1;
    public Map<Entity, List<Vec3d>> renderMap = new HashMap<Entity, List<Vec3d>>();

    public Trails() {
        super("Trails", "", Category.RENDER);

        setmgr.rSetting(lineWidth);
        setmgr.rSetting(color);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        for (Entity entity : mc.world.loadedEntityList) {
            int[] counter = new int[]{1};
            if (!this.renderMap.containsKey(entity)) continue;
            GlStateManager.pushMatrix();
            RenderUtil.GLPre((float) lineWidth.getValDouble());
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            GL11.glLineWidth((float) lineWidth.getValDouble());
            GL11.glBegin((int)1);
            for (int i = 0; i < this.renderMap.get(entity).size() - 1; ++i) {
                GL11.glColor4f(this.color.getR() / 255, this.color.getG() / 255, (float)((float)this.color.getB() / 255), 1);
                Vec3d pos = updateToCamera(this.renderMap.get(entity).get(i));
                Vec3d pos2 = updateToCamera(this.renderMap.get(entity).get(i + 1));
                GL11.glVertex3d((double)pos.x, (double)pos.y, (double)pos.z);
                GL11.glVertex3d((double)pos2.x, (double)pos2.y, (double)pos2.z);
                counter[0] = counter[0] + 1;
            }
            GL11.glEnd();
            GlStateManager.resetColor();
            GlStateManager.enableDepth();
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            RenderUtil.GlPost();
            GlStateManager.popMatrix();
        }
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {
        if (mc.player == null || mc.world == null) {
            return;
        }
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityThrowable) && !(entity instanceof EntityArrow) || entity instanceof EntityExpBottle) continue;
            ArrayList<Vec3d> vectors = this.renderMap.get(entity) != null ? (ArrayList<Vec3d>) this.renderMap.get(entity) : new ArrayList<Vec3d>();
            vectors.add(new Vec3d(entity.posX, entity.posY, entity.posZ));
            this.renderMap.put(entity, vectors);
        }
    });

    public static Vec3d updateToCamera(Vec3d vec) {
        return new Vec3d(vec.x - mc.getRenderManager().viewerPosX, vec.y - mc.getRenderManager().viewerPosY, vec.z - mc.getRenderManager().viewerPosZ);
    }
}
