package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import me.zero.alpine.listener.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.*;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.concurrent.*;

import static org.lwjgl.opengl.GL11.*;

public class SpawnsESP extends Module {
    private final Setting color = new Setting("Color", this, "Color", new Colour(255, 255, 255, 255));
    private final Setting crystals = new Setting("Crystals", this, true);
    private final Setting players = new Setting("Players", this, false);
    private final Setting mobs = new Setting("Mobs", this, false);
    private final Setting boats = new Setting("Boats", this, false);
    private final Setting duration = new Setting("Duration", this, 1, 0.1f, 5, false);
    private final Setting width = new Setting("Widtht", this, 2.5f, 0.1, 10, false);

    public CopyOnWriteArrayList<VecCircle> circles = new CopyOnWriteArrayList();
    public ConcurrentHashMap<BlockPos, Long> blocks = new ConcurrentHashMap();

    public SpawnsESP() {
        super("SpawnsESP", "        super(\"SpawnsESP\", )", Category.RENDER);

        setmgr.rSetting(color);
        setmgr.rSetting(crystals);
        setmgr.rSetting(players);
        setmgr.rSetting(mobs);
        setmgr.rSetting(boats);
        setmgr.rSetting(duration);
        setmgr.rSetting(width);
    }

    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        for (VecCircle circle : circles) {
            if ((float)(System.currentTimeMillis() - VecCircle.getTime(circle)) > 1000.0f * duration.getValDouble()) {
                this.circles.remove(circle);
                continue;
            }
            glPushMatrix();
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            glDisable(GL11.GL_LIGHTING);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            float f = (float)(System.currentTimeMillis() % 7200L) / 7200.0f;
            ArrayList<Vec3d> arrayList = new ArrayList<>();
            double deltaX = VecCircle.getVector(circle).x - mc.getRenderManager().renderPosX;
            double deltaY = VecCircle.getVector(circle).y - mc.getRenderManager().renderPosY;
            double deltaZ = VecCircle.getVector(circle).z - mc.getRenderManager().renderPosZ;
            GL11.glLineWidth((float) width.getValDouble());
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glBegin(1);
            for (int i = 0; i <= 360; ++i) {
                Vec3d vec3d = new Vec3d(deltaX + Math.sin((double)i * Math.PI / 180.0) * (double)VecCircle.getPitch(circle), deltaY + (double)(VecCircle.getYaw(circle) * ((float)(System.currentTimeMillis() - VecCircle.getTime(circle)) / (1000.0f * duration.getValDouble()))), deltaZ + Math.cos((double)i * Math.PI / 180.0) * (double)VecCircle.getPitch(circle));
                arrayList.add(vec3d);
            }
            for (int n = 0; n < arrayList.size() - 1; ++n) {
                color.getColour().glColor();
                GL11.glVertex3d(arrayList.get(n).x, arrayList.get(n).y, arrayList.get(n).z);
                GL11.glVertex3d(arrayList.get(n + 1).x, arrayList.get(n + 1).y, arrayList.get(n + 1).z);
            }
            GL11.glEnd();
            glEnable(GL11.GL_LIGHTING);

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glPopMatrix();
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

    public static class VecCircle {
        public Vec3d vec3d;
        public float yaw;
        public float pitch;
        public long time;

        public static Vec3d getVector(VecCircle circle) {
            return circle.vec3d;
        }

        public VecCircle(Vec3d vec3d, float yas, float pitch) {
            this.vec3d = vec3d;
            this.yaw = yas;
            this.pitch = pitch;
            this.time = System.currentTimeMillis();
        }

        public static float getPitch(VecCircle circle) {
            return circle.pitch;
        }

        public static float getYaw(VecCircle circle) {
            return circle.yaw;
        }

        public static long getTime(VecCircle circle) {
            return circle.time;
        }
    }
}