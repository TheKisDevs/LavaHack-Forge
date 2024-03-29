package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerJump;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import org.lwjgl.opengl.GL11;

import com.kisman.cc.util.render.ColorUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

public class JumpCircle extends Module {
    public static JumpCircle instance;

    private final byte MAX_JC_TIME = 20;
    private List<Circle> circles = new ArrayList<>();

    private Setting rainbow = new Setting("RainBow", this, true);
    private Setting color = new Setting("Color", this, "Color", new Colour(255, 0, 0));

    public JumpCircle() {
        super("JumpCircle", Category.RENDER);

        instance = this;

        setmgr.rSetting(rainbow);
        setmgr.rSetting(color);
    }

    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(jump);
    }

    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(jump);
    }

    public void update() {
        try{circles.removeIf(Circle::update);} catch(Exception ignored) {}
    }

    @EventHandler private final Listener<EventPlayerJump> jump = new Listener<>(event -> {
        circles.add(new Circle(event.entity.getPositionVector(), rainbow.getValBoolean() ? new Colour(ColorUtils.rainbow(1, 1)).toVec3d() : color.getColour().toVec3d()));
    });

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        EntityPlayerSP client = mc.player;
        double ix = -(client.lastTickPosX + (client.posX - client.lastTickPosX) * event.getPartialTicks());
        double iy = -(client.lastTickPosY + (client.posY - client.lastTickPosY) * event.getPartialTicks());
        double iz = -(client.lastTickPosZ + (client.posZ - client.lastTickPosZ) * event.getPartialTicks());
        GL11.glPushMatrix();
        GL11.glTranslated(ix, iy, iz);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        glDisable(GL11.GL_LIGHTING);

        GL11.glShadeModel(GL11.GL_SMOOTH);
        Collections.reverse(circles);
        try {
            for (Circle c : circles) {
                float k = (float) c.existed / MAX_JC_TIME;
                double x = c.position().x;
                double y = c.position().y - k * 0.5;
                double z = c.position().z;
                float end = k + 1f - k;
                GL11.glBegin(GL11.GL_QUAD_STRIP);
                for (int i = 0; i <= 360; i = i + 5) {
                    GL11.glColor4f((float) c.color().x, (float) c.color().y, (float) c.color().z, 0.2f * (1 - ((float) c.existed / MAX_JC_TIME)));
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i * 4)) * k, y, z + Math.sin(Math.toRadians(i * 4)) * k);
                    GL11.glColor4f(1, 1, 1, 0.01f * (1 - ((float) c.existed / MAX_JC_TIME)));
                    GL11.glVertex3d(x + Math.cos(Math.toRadians(i)) * end, y + Math.sin(k * 8) * 0.5, z + Math.sin(Math.toRadians(i) * end));
                }
                GL11.glEnd();
            }
        } catch (Exception ignored) {}
        Collections.reverse(circles);

        glEnable(GL11.GL_LIGHTING);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glPopMatrix();
    }

    public class Circle {
        private final Vec3d vec;
        private final Vec3d color;
        byte existed;

        Circle(Vec3d vec, Vec3d color) {
            this.vec = vec;
            this.color = color;
        }

        Vec3d position() {return this.vec;}
        Vec3d color() {return this.color;}
        boolean update() {return ++existed > MAX_JC_TIME;}
    }
}
