package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.RenderUtil;
import i.gishreloaded.gishcode.utils.TimerUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class Breadcrumbs extends Module {
    TimerUtils timer = new TimerUtils();
    ArrayList<Vec3d> positions = new ArrayList<>();

    public Breadcrumbs() {
        super("Breadcrumbs", ", ", Category.RENDER);
    }

    public void onEnable() {
        positions.clear();
    }

    public void onDisable() {
        positions.clear();
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        if (!timer.hasTimeElapsed(100, true)) {
            return;
        }
        if (mc.player.movementInput.moveForward == 0 && mc.player.moveStrafing == 0) {
            return;
        }
        positions.add(mc.player.getPositionVector());
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(2);
        GL11.glColor4f(0.3f, 1f, 1f, 0.7f);
        GL11.glBegin(3);
        for (Vec3d vec : positions) {
            RenderUtil.putVertex3d(RenderUtil.getRenderPos(vec.x, vec.y + 0.3, vec.z));
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
