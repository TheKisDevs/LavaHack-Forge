package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.VecCircle;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SpawnsESP extends Module {
    private Setting crystals = new Setting("Crystals", this, true);
    private Setting players = new Setting("Players", this, false);
    private Setting mobs = new Setting("Mobs", this, false);
    private Setting boats = new Setting("Boats", this, false);
    private Setting duration = new Setting("Duration", this, 1, 0.1f, 5, false);
    private Setting width = new Setting("Widht", this, 2.5f, 0.1, 10, false);

    public CopyOnWriteArrayList<VecCircle> circles = new CopyOnWriteArrayList();
    public ConcurrentHashMap<BlockPos, Long> blocks = new ConcurrentHashMap();

    public SpawnsESP() {
        super("SpawnsESP", "        super(\"SpawnsESP\", )\n", Category.RENDER);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        for (VecCircle class442 : circles) {
            int n;
            if ((float)(System.currentTimeMillis() - VecCircle.Method722(class442)) > 1000.0f * duration.getValDouble()) {
                this.circles.remove(class442);
                continue;
            }
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            float[] fArray = Color.RGBtoHSB(1, 1, 1, null);
            float f = (float)(System.currentTimeMillis() % 7200L) / 7200.0f;
            int n2 = Color.getHSBColor(f, fArray[1], fArray[2]).getRGB();
            ArrayList<Vec3d> arrayList = new ArrayList<>();
            double d = VecCircle.Method719(class442).x - mc.getRenderManager().renderPosX;
            double d2 = VecCircle.Method719(class442).y - mc.getRenderManager().renderPosY;
            double d3 = VecCircle.Method719(class442).z - mc.getRenderManager().renderPosZ;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glLineWidth((float) width.getValDouble());
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glBegin(1);
            for (n = 0; n <= 360; ++n) {
                Vec3d vec3d = new Vec3d(d + Math.sin((double)n * Math.PI / 180.0) * (double)VecCircle.Method720(class442), d2 + (double)(VecCircle.Method721(class442) * ((float)(System.currentTimeMillis() - VecCircle.Method722(class442)) / (1000.0f * duration.getValDouble()))), d3 + Math.cos((double)n * Math.PI / 180.0) * (double)VecCircle.Method720(class442));
                arrayList.add(vec3d);
            }
            for (n = 0; n < arrayList.size() - 1; ++n) {
                int n3 = n2 >> 24 & 0xFF;
                int n4 = n2 >> 16 & 0xFF;
                int n5 = n2 >> 8 & 0xFF;
                int n6 = n2 & 0xFF;

                GL11.glColor4f((float)n4 / 255.0f, (float)n5 / 255.0f, (float)n6 / 255.0f, (float)n3 / 255.0f);
                GL11.glVertex3d(arrayList.get(n).x, arrayList.get(n).y, arrayList.get(n).z);
                GL11.glVertex3d(arrayList.get(n + 1).x, arrayList.get(n + 1).y, arrayList.get(n + 1).z);
                n2 = Color.getHSBColor(f += 0.0027777778f, fArray[1], fArray[2]).getRGB();
            }
            GL11.glEnd();
            GL11.glDisable(2848);
            GlStateManager.enableLighting();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            if (((SPacketSpawnObject) event.getPacket()).getType() == 51 && crystals.getValBoolean()) {
                this.circles.add(new VecCircle(new Vec3d(((SPacketSpawnObject) event.getPacket()).getX(), ((SPacketSpawnObject) event.getPacket()).getY(), ((SPacketSpawnObject) event.getPacket()).getZ()), 1.5f, 0.5f));
            } else if (((SPacketSpawnObject) event.getPacket()).getType() == 1 && boats.getValBoolean()) {
                this.circles.add(new VecCircle(new Vec3d(((SPacketSpawnObject) event.getPacket()).getX(), ((SPacketSpawnObject) event.getPacket()).getY(), ((SPacketSpawnObject) event.getPacket()).getZ()), 1.0f, 0.75f));
            }
        } else if (event.getPacket() instanceof SPacketSpawnPlayer && players.getValBoolean()) {
            this.circles.add(new VecCircle(new Vec3d(((SPacketSpawnPlayer) event.getPacket()).getX(), ((SPacketSpawnPlayer) event.getPacket()).getY(), ((SPacketSpawnPlayer) event.getPacket()).getZ()), 1.8f, 0.5f));
        } else if (event.getPacket() instanceof SPacketSpawnMob && mobs.getValBoolean()) {
            this.circles.add(new VecCircle(new Vec3d(((SPacketSpawnMob) event.getPacket()).getX(), ((SPacketSpawnMob) event.getPacket()).getY(), ((SPacketSpawnMob) event.getPacket()).getZ()), 1.8f, 0.5f));
        }
    });
}