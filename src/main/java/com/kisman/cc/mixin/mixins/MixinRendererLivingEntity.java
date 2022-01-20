package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventRenderEntityName;
import com.kisman.cc.module.combat.AutoRer;
import com.kisman.cc.module.render.Charms;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.*;
import static org.lwjgl.opengl.GL11.*;

import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = RenderLivingBase.class, priority = 10000)
public abstract class MixinRendererLivingEntity<T extends EntityLivingBase> extends Render<T> {
    @Shadow protected ModelBase mainModel;
    protected ModelBase entityModel;

    protected MixinRendererLivingEntity() {
        super(null);
    }

    @Inject(method = "renderName(Lnet/minecraft/entity/Entity;DDD)V", at = @At("HEAD"), cancellable = true)
    private void doRenderName(Entity par1, double par2, double par3, double par4, CallbackInfo ci) {
        EventRenderEntityName event = new EventRenderEntityName(par1, par2, par2, par4, "", 0);
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled()) ci.cancel();
    }

    /**
     * @author kshk
     */
    @Overwrite
    protected void renderModel(T p_renderModel_1_, float p_renderModel_2_, float p_renderModel_3_, float p_renderModel_4_, float p_renderModel_5_, float p_renderModel_6_, float p_renderModel_7_) {
        boolean flag = this.isVisible(p_renderModel_1_);
        boolean flag1 = !flag && !p_renderModel_1_.isInvisibleToPlayer(Minecraft.getMinecraft().player);

        if (flag || flag1) {
            if (!this.bindEntityTexture(p_renderModel_1_)) return;
            if (flag1) GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            if(Charms.instance.isToggled() && p_renderModel_1_ instanceof EntityPlayer) {
                glPushMatrix();

                glEnable(GL_POLYGON_OFFSET_LINE);

                if(Charms.instance.polygonOffset.getValBoolean()) glPolygonOffset(1.0f, 1000000);

                glDisable(GL_TEXTURE_2D);
                glDisable(GL_LIGHTING);

                final Setting color = Charms.instance.color;
                if(Charms.instance.customColor.getValBoolean()) {
                    if(Charms.instance.targetRender.getValBoolean()) {
                        if(AutoRer.currentTarget == p_renderModel_1_) glColor4f(0.6f, 0, 1, color.getA());
                        else GL11.glColor4f(color.getR() / 255f, color.getG() / 255f, color.getB() / 255f, color.getA() / 255f);
                    } else GL11.glColor4f(color.getR() / 255f, color.getG() / 255f, color.getB() / 255f, color.getA() / 255f);
                }

                glDisable(GL_DEPTH_TEST);
                this.mainModel.render(p_renderModel_1_, p_renderModel_2_, p_renderModel_3_, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_);
                glEnable(GL_DEPTH_TEST);
                this.mainModel.render(p_renderModel_1_, p_renderModel_2_, p_renderModel_3_, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_);

                RenderUtil.setupColor(new Color(0xFFFFFFF).hashCode());

                glEnable(GL_TEXTURE_2D);
                glEnable(GL_LIGHTING);
                glDisable(GL_POLYGON_OFFSET_LINE);
                glPopMatrix();

            } else this.mainModel.render(p_renderModel_1_, p_renderModel_2_, p_renderModel_3_, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_);

            if (flag1) GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
    }

    protected boolean isVisible(T p_isVisible_1_) {
        return !p_isVisible_1_.isInvisible() || this.renderOutlines;
    }
}
