package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class TargetESP extends Module {
    private Setting depth = new Setting("Depth", this, true);
    private Setting animSpeed = new Setting("Anim Speed", this, 1, 0.1f, 10, false);
    private Setting orbitSpeed = new Setting("Orbit Speed", this, 1, 0.1f, 10, false);
    private Setting width = new Setting("Width", this, 2.5f, 0.1f, 5, false);
    private Setting orbit = new Setting("Orbit", this, true);
    private Setting trail = new Setting("Trail", this, true);
    private Setting fill = new Setting("Fill", this, false);
    private Setting rainbow = new Setting("Rainbow", this, true);

    private ArrayList<EntityPlayer> targets = new ArrayList<>();
    private Color color = new Color(210, 100, 100);

    public TargetESP() {
        super("TargetESP", Category.RENDER);

        setmgr.rSetting(depth);
        setmgr.rSetting(animSpeed);
        setmgr.rSetting(orbitSpeed);
        setmgr.rSetting(width);
        setmgr.rSetting(orbit);
        setmgr.rSetting(trail);
        setmgr.rSetting(fill);
        setmgr.rSetting(rainbow);
    }

    public void onEnable() {
        targets.clear();
    }

    public void update() {
        if(AutoRer.currentTarget == KillAura.instance.target && AutoRer.currentTarget != null) targets.add(AutoRer.currentTarget);
        else {
            if (AutoRer.currentTarget != null) targets.add(AutoRer.currentTarget);
            if (KillAura.instance.target != null) targets.add(KillAura.instance.target);
        }
//        if(AutoFirework.instance.target != null) targets.add(AutoFirework.instance.target);
//        if(HoleFiller.instance.target != null) targets.add(HoleFiller.instance.target);
//        if(CrystalFiller.instance.target != null) targets.add(CrystalFiller.instance.target);
//        if(AutoCrystal.instance.target != null) targets.add(AutoCrystal.instance.target);
//        if(AutoCrystalRewrite.instance.target != null) targets.add(AutoCrystalRewrite.instance.target);

        /*for(EntityPlayer player : targets) {
            if(toRemove(targets.indexOf(player))) targets.remove(player);
        }*/
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(!targets.isEmpty()) {
            for(EntityPlayer player : targets) {
                int n, n2, n3;
                float f;

                glPushMatrix();
                RenderUtil.Method1386();

                if(depth.getValBoolean()) GlStateManager.enableDepth();

                RenderManager renderManager = mc.renderManager;

                float[] fArray = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
                float f2 = f = (float) (System.currentTimeMillis() % 7200L) / 7200.0f;
                int n4 = Color.getHSBColor(f2, fArray[1], fArray[2]).getRGB();
                ArrayList<Vec3d> arrayList = new ArrayList<>();
                double d = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)event.getPartialTicks() - renderManager.renderPosX;
                double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)event.getPartialTicks() - renderManager.renderPosY;
                double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)event.getPartialTicks() - renderManager.renderPosZ;
                double d4 = -Math.cos((double)System.currentTimeMillis() / 1000.0 * (double) animSpeed.getValFloat()) * ((double) player.height / 2.0) + (double) player.height / 2.0;
                GL11.glLineWidth(width.getValFloat());
                GL11.glBegin(1);
                for (n3 = 0; n3 <= 360; ++n3) {
                    Vec3d vec3d = new Vec3d(d + Math.sin((double)n3 * Math.PI / 180.0) * 0.5, d2 + d4 + 0.01, d3 + Math.cos((double)n3 * Math.PI / 180.0) * 0.5);
                    arrayList.add(vec3d);
                }
                for (n3 = 0; n3 < arrayList.size() - 1; ++n3) {
                    float f3 = (fill.getValBoolean() ? 1.0f : (float) color.getAlpha() / 255.0f);
                    int n5 = n4 >> 16 & 0xFF;
                    n2 = n4 >> 8 & 0xFF;
                    n = n4 & 0xFF;
                    float f4 = orbit.getValBoolean() ? (trail.getValBoolean() ? (float)Math.max(0.0, -0.3183098861837907 * Math.atan(Math.tan(Math.PI * (double)((float)n3 + 1.0f) / (double)arrayList.size() + (double)System.currentTimeMillis() / 1000.0 * (double) orbitSpeed.getValFloat()))) : (float)Math.max(0.0, Math.abs(Math.sin((double)(((float)n3 + 1.0f) / (float)arrayList.size()) * Math.PI + (double)System.currentTimeMillis() / 1000.0 * (double) orbitSpeed.getValFloat())) * 2.0 - 1.0)) : (f3 = fill.getValBoolean() ? 1.0f : (float) color.getAlpha() / 255.0f);
                    if (rainbow.getValBoolean()) {
                        GL11.glColor4f((float)n5 / 255.0f, (float)n2 / 255.0f, (float)n / 255.0f, f3);
                    } else {
                        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, f3);
                    }
                    GL11.glVertex3d(arrayList.get(n3).x, arrayList.get(n3).y, arrayList.get(n3).z);
                    GL11.glVertex3d(arrayList.get(n3 + 1).x, arrayList.get(n3 + 1).y, arrayList.get(n3 + 1).z);
                    n4 = Color.getHSBColor(f2 += 0.0027777778f, fArray[1], fArray[2]).getRGB();
                }
                GL11.glEnd();
                if (fill.getValBoolean()) {
                    f2 = f;
                    GL11.glBegin(9);
                    for (n3 = 0; n3 < arrayList.size() - 1; ++n3) {
                        int n6 = n4 >> 16 & 0xFF;
                        n2 = n4 >> 8 & 0xFF;
                        n = n4 & 0xFF;
                        if (rainbow.getValBoolean()) {
                            GL11.glColor4f((float)n6 / 255.0f, (float)n2 / 255.0f, (float)n / 255.0f, (float) color.getAlpha() / 255.0f);
                        } else {
                            GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255f);
                        }
                        GL11.glVertex3d(arrayList.get(n3).x, arrayList.get(n3).y, arrayList.get(n3).z);
                        GL11.glVertex3d(arrayList.get(n3 + 1).x, arrayList.get(n3 + 1).y, arrayList.get(n3 + 1).z);
                        n4 = Color.getHSBColor(f2 += 0.0027777778f, fArray[1], fArray[2]).getRGB();
                    }
                    GL11.glEnd();
                }
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                RenderUtil.Method1385();
                GlStateManager.popMatrix();
            }
        }
    }

    private boolean toRemove(int index) {
        for(EntityPlayer player : targets) if(targets.indexOf(player) == index) return true;
        return false;
    }
}
