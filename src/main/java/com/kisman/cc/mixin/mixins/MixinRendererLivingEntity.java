package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventRenderEntityName;
import com.kisman.cc.module.combat.AutoCrystalBypass;
import com.kisman.cc.module.render.Charms;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import static org.lwjgl.opengl.GL11.*;

import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    @Inject(method = {"doRender"}, at = @At("HEAD"))
    public void doRenderPre(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if(Charms.instance.isToggled() && Charms.instance.render.getValBoolean() && Charms.instance.polygonMode.getValString().equalsIgnoreCase("doRender")) {
            glEnable(32823);
            glPolygonOffset(1, -1100000);

            if(Charms.instance.targetRender.getValBoolean() && entity instanceof EntityPlayer) {
                if(AutoCrystalBypass.instance.target == entity)
                glColor4f(1, 0.03f, 0.9f, 1);
            } else if(Charms.instance.customColor.getValBoolean()) {
                float[] color = new float[] {
                        Charms.instance.color.getR() / 255f,
                        Charms.instance.color.getG() / 255f,
                        Charms.instance.color.getB() / 255f,
                        Charms.instance.color.getA() / 255f
                };
                glColor4f(color[0], color[1], color[2], color[3]);
            }
        }
    }

    @Inject(method = {"doRender"}, at = @At("RETURN"))
    public void doRenderPost(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if(Charms.instance.isToggled() && Charms.instance.render.getValBoolean() && Charms.instance.polygonMode.getValString().equalsIgnoreCase("doRender")) {
            glPolygonOffset(1, 1000000);
            glDisable(32823);
        }
    }

    /**
     * @author kshk
     */
    @Overwrite
    protected void renderModel(T p_renderModel_1_, float p_renderModel_2_, float p_renderModel_3_, float p_renderModel_4_, float p_renderModel_5_, float p_renderModel_6_, float p_renderModel_7_) {
        boolean flag = this.isVisible(p_renderModel_1_);
        boolean flag1 = !flag && !p_renderModel_1_.isInvisibleToPlayer(Minecraft.getMinecraft().player);

        if (flag || flag1) {
            if (!this.bindEntityTexture(p_renderModel_1_)) {
                return;
            }

            if (flag1) {
                GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }

            if(Charms.instance.isToggled() && p_renderModel_1_ instanceof EntityPlayer && Charms.instance.polygonMode.getValString().equalsIgnoreCase("RenderModel")) {
                glPushMatrix();

                glEnable(GL_POLYGON_OFFSET_LINE);

                if(Charms.instance.polygonOffset.getValBoolean()) {
                    glPolygonOffset(1.0f, 1000000);
                }

                glDisable(GL_TEXTURE_2D);
                glDisable(GL_LIGHTING);

                final Setting color = Charms.instance.color;
                if(Charms.instance.customColor.getValBoolean() && Charms.instance.textureMode.getValString().equalsIgnoreCase("GL")) {
                    if(Charms.instance.targetRender.getValBoolean() && p_renderModel_1_ instanceof EntityPlayer) {
                        if(AutoCrystalBypass.instance.target == p_renderModel_1_) {
                            glColor4f(1, 0.03f, 0.9f, 1);
                        } else {
                            GL11.glColor4f(color.getR() / 255f, color.getG() / 255f, color.getB() / 255f, color.getA() / 255f);
                        }
                    } else {
                        GL11.glColor4f(color.getR() / 255f, color.getG() / 255f, color.getB() / 255f, color.getA() / 255f);
                    }
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

            } else {
                this.mainModel.render(p_renderModel_1_, p_renderModel_2_, p_renderModel_3_, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_);
            }

            if (flag1) {
                GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
        }
    }

    protected boolean isVisible(T p_isVisible_1_) {
        return !p_isVisible_1_.isInvisible() || this.renderOutlines;
    }
}
