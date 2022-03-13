package com.kisman.cc.util;

import net.minecraft.entity.Entity;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;

public class RenderUtil2 implements Globals {
    public static void drawFadeESP(Entity entity, Colour  color, Colour color2) {
        glPushMatrix ();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBegin(GL_LINE_STRIP);
        for (int i = 0; i <= 360; ++i){
            color.glColor();
            glVertex3d(entity.posX, entity.posY, entity.posZ);
            color2.getColor();
            glVertex3d(entity.posX - sin(toRadians(i)), entity.posY, entity.posZ + cos(toRadians(i)));
        }
        glEnd();
        glEnable(GL_TEXTURE_2D);
        glEnable (GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glColor4f (1,1,1,1);
        glPopMatrix ();
    }
}
