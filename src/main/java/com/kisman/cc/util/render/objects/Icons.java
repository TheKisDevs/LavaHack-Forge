package com.kisman.cc.util.render.objects;

import com.kisman.cc.util.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public enum Icons implements Globals {
    CHECKED_CHECKBOX(new ResourceLocation("kismancc:icons/enabled1.png")),
    LOGO(new ResourceLocation("kismancc:icons/logo.png"));

    public final ResourceLocation resourceLocation;

    Icons(ResourceLocation resourceLocation) {this.resourceLocation = resourceLocation;}

    public void render(double x, double y, double width, double height) {
        GL11.glPushMatrix();
//        GL11.glEnable(2848);
        mc.getTextureManager().bindTexture(resourceLocation);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Render2DUtil.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
//        Render2DUtil.disableGL2D();
        GL11.glPopMatrix();
    }
}
