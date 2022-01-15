package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.AnimationUtils;
import com.kisman.cc.util.RenderUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class TargetESP extends Module {
    private Setting deltaTime = new Setting("Delta Time", this, 1, 0.1, 10, false);
    private Setting colorMode = new Setting("Color Mode", this, ColorMode.Astolfo);

    private ArrayList<EntityPlayer> targets = new ArrayList<>();

    private int test;
    private float animtest;
    private boolean anim;
    private double time;

    public TargetESP() {
        super("TargetESP", Category.RENDER);

        setmgr.rSetting(deltaTime);
        setmgr.rSetting(colorMode);
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
            for(EntityPlayer target : targets) {
                if (target.getHealth() > 0 && !target.isDead) {
                    time += .01 * (deltaTime.getValDouble() * .1);
                    double height = 0.8 * (1 + Math.sin(2 * Math.PI * (time * .3)));
                    if (height > 0.995) anim = true;
                    else if (height < 0.05) anim = false;

                    final double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
                    final double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY;
                    final double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

                    GlStateManager.enableBlend();
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GlStateManager.disableDepth();
                    GlStateManager.disableTexture2D();
                    GlStateManager.disableAlpha();
                    GL11.glLineWidth(0.8F);
                    GL11.glShadeModel(GL11.GL_SMOOTH);
                    GL11.glDisable(GL11.GL_CULL_FACE);
                    final double size = target.width * 1.2;
                    if (test <= 10) {
                        if (anim) animtest += 0.01F;
                        else animtest -= 0.01F;
                        test = 10;
                    }
                    test--;
                    double gg = mc.player.onGround ? 0.35 : 0.65;
                    double y2 = 0;
                    y2 += target.getEyeHeight() - (target.isSneaking() ? 0.25D : 0.0D);
                    if (animtest <= y) anim = true;
                    else if (animtest >= y + y2 + gg) anim = false;

                    GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
                    {
                        for (int j = 0; j < 361; j++) {
                            ColorUtils.glColor(colorMode.getValString().equals(ColorMode.Astolfo.name()) ? ColorUtils.astolfoColors(100, 100) : ColorUtils.rainbow().getRGB(),  (int) (255 * (1 - height)));
                            double x1 = x + Math.cos(Math.toRadians(j)) * size;
                            double z1 = z - Math.sin(Math.toRadians(j)) * size;
                            GL11.glVertex3d(x1, y + animtest, z1);
                            ColorUtils.glColor(colorMode.getValString().equals(ColorMode.Astolfo.name()) ? ColorUtils.astolfoColors(100, 100) : ColorUtils.rainbow().getRGB(), 0);
                            GL11.glVertex3d(x1, y + animtest + (.5 * height), z1);
                        }
                    }
                    GL11.glEnd();
                    GL11.glBegin(GL11.GL_LINE_LOOP);
                    {
                        for (int j = 0; j < 361; j++) {
                            ColorUtils.glColor(colorMode.getValString().equals(ColorMode.Astolfo.name()) ? ColorUtils.astolfoColors(100, 100) : ColorUtils.rainbow().getRGB(),  255);
                            GL11.glVertex3d(x + Math.cos(Math.toRadians(j)) * size, y + animtest, z - Math.sin(Math.toRadians(j)) * size);
                        }
                    }
                    GL11.glEnd();
                    GlStateManager.enableAlpha();
                    GL11.glShadeModel(GL11.GL_FLAT);
                    GL11.glDisable(GL11.GL_LINE_SMOOTH);
                    GL11.glEnable(GL11.GL_CULL_FACE);
                    GlStateManager.enableTexture2D();
                    GlStateManager.enableDepth();
                    GlStateManager.disableBlend();
                    GlStateManager.resetColor();
                }
            }
        }
    }

    private boolean toRemove(int index) {
        for(EntityPlayer player : targets) if(targets.indexOf(player) == index) return true;
        return false;
    }

    public enum ColorMode {Astolfo, Rainbow}
}
