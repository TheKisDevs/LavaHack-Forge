package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.Rendering;
import me.zero.alpine.listener.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class SpawnsESP extends Module {
    private final Setting color = register(new Setting("Color", this, new Colour(255, 255, 255, 255)));
    private final Setting crystals = register(new Setting("Crystals", this, true));
    private final Setting players = register(new Setting("Players", this, false));
    private final Setting mobs = register(new Setting("Mobs", this, false));
    private final Setting boats = register(new Setting("Boats", this, false));
    private final Setting duration = register(new Setting("Duration", this, 1, 0.1f, 5, false));
    private final Setting width = register(new Setting("Width", this, 2.5f, 0.1, 10, false));
    private final MultiThreaddableModulePattern threads = threads();

    private final ArrayList<VecCircle> circles = new ArrayList<>();

    public SpawnsESP() {
        super("SpawnsESP", "        super(\"SpawnsESP\", )", Category.RENDER);
    }

    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(listener);
        threads.reset();
    }

    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(listener);
    }
    
    private void doSpawnsESP() {
        for (VecCircle circle : circles) {
            if ((float)(System.currentTimeMillis() - VecCircle.getTime(circle)) > 1000.0f * duration.getValDouble()) {
                this.circles.remove(circle);
                continue;
            }

            mc.addScheduledTask(() -> {
                Rendering.setup();

//                ArrayList<Vec3d> vertexes = new ArrayList<>();
                double deltaX = VecCircle.getVector(circle).x - mc.getRenderManager().renderPosX;
                double deltaY = VecCircle.getVector(circle).y - mc.getRenderManager().renderPosY;
                double deltaZ = VecCircle.getVector(circle).z - mc.getRenderManager().renderPosZ;
                GL11.glLineWidth(width.getValFloat());
                GL11.glBegin(1);
                color.getColour().glColor();
                for (int i = 0; i <= 360; ++i) {
                    Vec3d vec3d = new Vec3d(deltaX + Math.sin((double)i * Math.PI / 180.0) * (double)VecCircle.getPitch(circle), deltaY + (double)(VecCircle.getYaw(circle) * ((float)(System.currentTimeMillis() - VecCircle.getTime(circle)) / (1000.0f * duration.getValDouble()))), deltaZ + Math.cos((double)i * Math.PI / 180.0) * (double)VecCircle.getPitch(circle));
                    GL11.glVertex3d(vec3d.x, vec3d.y, vec3d.z);
//                    GL11.glVertex3d(vertexes.get(n + 1).x, vertexes.get(n + 1).y, vertexes.get(n + 1).z);
                }
                /*for (int n = 0; n < vertexes.size() - 1; ++n) {
                    color.getColour().glColor();
                    GL11.glVertex3d(vertexes.get(n).x, vertexes.get(n).y, vertexes.get(n).z);
                    GL11.glVertex3d(vertexes.get(n + 1).x, vertexes.get(n + 1).y, vertexes.get(n + 1).z);
                }*/
                GL11.glEnd();

                Rendering.release();
            });
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        threads.update(() -> mc.addScheduledTask(this::doSpawnsESP));
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