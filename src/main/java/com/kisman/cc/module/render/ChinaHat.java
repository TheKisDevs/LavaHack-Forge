package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.util.glu.*;

import static org.lwjgl.opengl.GL11.*;

public class ChinaHat extends Module {
    private Setting color = new Setting("Color", this, "Color", new float[] {1, 1, 1, 1}, false);

    public ChinaHat() {
        super("ChinaHat", "ChinaHat", Category.RENDER);

        setmgr.rSetting(color);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {

        glPushMatrix();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
//        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glColor4d(color.getR() / 255f, color.getG() / 255f, color.getB(), color.getA());
        glTranslatef(0f, mc.player.height + 0.4f, 0f);
        glRotatef(90f, 1f, 0f, 0f);

        Cylinder cylinder = new Cylinder();

        cylinder.setDrawStyle(GLU.GLU_FILL);

        cylinder.draw(0f, 0.8f, 0.4f, 30, 1);

        GlStateManager.resetColor();
//        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glPopMatrix();
    }
}
