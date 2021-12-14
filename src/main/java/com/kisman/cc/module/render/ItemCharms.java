package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.shaders.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.awt.*;

public class ItemCharms extends Module {
    public static ItemCharms instance;

    public static final GlShader ITEM_SHADER = new GlShader("item");

    public Setting red = new Setting("Red", this, 1, 0, 1, false);
    public Setting green = new Setting("Green", this, 1, 0, 1, false);
    public Setting blue = new Setting("Blue", this, 1, 0, 1, false);
    public Setting alpha = new Setting("Alpha", this, 1, 0, 1, false);

    public Setting exampleColor = new Setting("ExampleColor", this, red.getValFloat(), green.getValFloat(), blue.getValFloat(), alpha.getValFloat());

    public Setting glintModify = new Setting("GlintModify", this, false);

    public Color color = new Color(red.getRed(), red.getGreen(), red.getBlue(), red.getAlpha());

    public ItemCharms() {
        super("ItemCharms", "", Category.RENDER);

        instance = this;

        setmgr.rSetting(exampleColor);

        setmgr.rSetting(red);
        setmgr.rSetting(green);
        setmgr.rSetting(blue);
        setmgr.rSetting(alpha);

        setmgr.rSetting(glintModify);
    }

    public void onDisable() {
        color = new Color(255, 255,255);
    }

    public void update() {
        exampleColor.updateColor(red.getValFloat(), green.getValFloat(), blue.getValFloat(), alpha.getValFloat());
    }

//    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
/*        ITEM_SHADER.blur = module.blur.getValue();
        ITEM_SHADER.mix = module.mix.getValue();
        ITEM_SHADER.alpha = module.chamColor.getValue().getAlpha() / 255.0f;
        ITEM_SHADER.imageMix = module.imageMix.getValue();
        ITEM_SHADER.useImage = module.useImage.getValue();
        ITEM_SHADER.startDraw(mc.getRenderPartialTicks());
        module.forceRender = true;
        ((IEntityRenderer) mc.entityRenderer).invokeRenderHand(mc.getRenderPartialTicks(), 2);
        module.forceRender = false;
        ITEM_SHADER.stopDraw(module.chamColor.getValue(), module.radius.getValue(), 1.0f);*/
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }
}
