package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.CrystalModifier;
import com.kisman.cc.util.OutlineUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = RenderEnderCrystal.class, priority = 10000)
public abstract class MixinRenderEnderCrystal {
    @Shadow
    public ModelBase modelEnderCrystal;
    @Shadow
    public ModelBase modelEnderCrystalNoBase;
    @Final
    @Shadow
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES;
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    @Shadow
    public abstract void doRender(EntityEnderCrystal entity, double x, double y, double z, float entityYaw, float partialTicks);

    @Redirect(method = "doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void render1(ModelBase var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
        if(!CrystalModifier.instance.isToggled()) {
            var1.render(var2, var3, var4, var5, var6, var7, var8);
        }
    }

    @Redirect(method = "doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V", ordinal = 1))
    private void render2(ModelBase var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
        if (!CrystalModifier.instance.isToggled()) {
            var1.render(var2, var3, var4, var5, var6, var7, var8);
        }
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V", at = @At("RETURN"), cancellable = true)
    public void IdoRender(EntityEnderCrystal entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.gameSettings.fancyGraphics = false;

        if(CrystalModifier.instance.isToggled()) {
            float var14 = (float)entity.innerRotation + partialTicks;

            GL11.glPushMatrix();

            //scale
            if(entity.equals(CrystalModifier.instance.preview.getEntity())) {
                GL11.glScalef(1, 1, 1);
            } else {
                GL11.glScaled(CrystalModifier.instance.scaleX.getValDouble(), CrystalModifier.instance.scaleY.getValDouble(), CrystalModifier.instance.scaleZ.getValDouble());
            }

            //translate
            GL11.glTranslated(x + CrystalModifier.instance.translateX.getValDouble(), y + CrystalModifier.instance.translateY.getValDouble(), z + CrystalModifier.instance.translateZ.getValDouble());

            float var15 = MathHelper.sin(var14 * 0.2f) / 2 + 0.5f;
            var15 += var15 * var15;

            float spinSpeed = (float) CrystalModifier.instance.speed.getValDouble();
            float bounceSpeed = (float) CrystalModifier.instance.bounce.getValDouble();

            if(CrystalModifier.instance.texture.getValBoolean()) {
                if(entity.shouldShowBottom()) {
                    modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                } else {
                    modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                }
            }

            //polygon
            GL11.glPushAttrib(1048575);
            if(CrystalModifier.instance.mode.getValEnum().equals(CrystalModifier.Modes.Wireframe)) {
                GL11.glPolygonMode(1032, 6913);
            }

            //other
            mc.renderManager.renderEngine.bindTexture(ENDER_CRYSTAL_TEXTURES);

            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glEnable(10754);

            //custom color
            if(CrystalModifier.instance.customColor.getValBoolean()) {
                GL11.glColor4f(CrystalModifier.instance.crystalColor.getR() / 255f, CrystalModifier.instance.crystalColor.getG() / 255f, CrystalModifier.instance.crystalColor.getB() / 255f, 1);
            } else {
                GL11.glColor3f(1, 1, 1);
            }

            if(entity.shouldShowBottom()) {
                modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
            } else {
                modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
            }

            //ench texture
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            if(CrystalModifier.instance.enchanted.getValBoolean()) {
                mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
                GL11.glTexCoord3d((double)1.0, (double)1.0, (double)1.0);
                GL11.glEnable((int)3553);
                GL11.glBlendFunc((int)768, (int)771);
                GL11.glColor4f(CrystalModifier.instance.enchColor.getR() / 255f, CrystalModifier.instance.enchColor.getG() / 255f, CrystalModifier.instance.enchColor.getB() / 255f, CrystalModifier.instance.enchColor.getA() / 255f);
                GL11.glColor4f(1, 1, 1, 1);
            }

            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();

            //outline
            if(CrystalModifier.instance.outline.getValBoolean()) {
                if(CrystalModifier.instance.outlineMode.getValEnum().equals(CrystalModifier.OutlineModes.Wire)) {
                    GL11.glPushAttrib(1048575);
                    GL11.glPolygonMode(1032, 6913);
                    GL11.glDisable(3008);
                    GL11.glDisable(3553);
                    GL11.glDisable(2896);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glLineWidth((float) CrystalModifier.instance.lineWidth.getValDouble());
                    GL11.glEnable(2960);
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                    GL11.glEnable(10754);
                    GL11.glColor4f(CrystalModifier.instance.color.getR() / 255, CrystalModifier.instance.color.getG() / 255, CrystalModifier.instance.color.getB() / 255, 1.0f);

                    if(entity.shouldShowBottom()) {
                        modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    } else {
                        modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    }

                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);

                    if(entity.shouldShowBottom()) {
                        modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    } else {
                        modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    }

                    GL11.glEnable(3042);
                    GL11.glEnable(2896);
                    GL11.glEnable(3553);
                    GL11.glEnable(3008);
                    GL11.glPopAttrib();
                } else {
                    OutlineUtils.setColor(CrystalModifier.instance.color);
                    OutlineUtils.renderOne((float) CrystalModifier.instance.lineWidth.getValDouble());

                    if(entity.shouldShowBottom()) {
                        modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    } else {
                        modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    }

                    OutlineUtils.renderTwo();

                    if(entity.shouldShowBottom()) {
                        modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    } else {
                        modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    }

                    OutlineUtils.renderThree();
                    OutlineUtils.renderFour();
                    OutlineUtils.setColor(CrystalModifier.instance.color);

                    if(entity.shouldShowBottom()) {
                        modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    } else {
                        modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    }

                    OutlineUtils.renderFive();
                    OutlineUtils.setColor(Color.WHITE);
                }
            }

            GL11.glPopMatrix();
        }
    }
}
