package com.kisman.cc.mixin.mixins;

import com.kisman.cc.features.module.client.Optimizer;
import com.kisman.cc.features.module.render.CharmsRewrite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@SuppressWarnings({"ConstantConditions", "NullableProblems"})
@Mixin(value = RenderLivingBase.class, priority = 10)
public class MixinRendererLivingEntity<T extends EntityLivingBase> extends Render<T> {
    @Shadow protected ModelBase mainModel;

    protected MixinRendererLivingEntity() {
        super(null);
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At("HEAD"), cancellable = true)
    private void doDoRender(T f3, double flag1, double flag, double f, float f1, float f2, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.world != null && mc.player != null && Optimizer.instance.isToggled() && Optimizer.instance.customEntityRenderRange.getValBoolean() && mc.player.getDistance(f3) > Optimizer.instance.entityRenderRange.getValInt()) ci.cancel();
    }

    /**
     * @author _kisman_
     * @reason pon
     */
    @Overwrite
    protected void renderModel(T p_renderModel_1_, float p_renderModel_2_, float p_renderModel_3_, float p_renderModel_4_, float p_renderModel_5_, float p_renderModel_6_, float p_renderModel_7_) {
        boolean flag = this.isVisible(p_renderModel_1_);
        boolean flag1 = !flag && !p_renderModel_1_.isInvisibleToPlayer(Minecraft.getMinecraft().player);

        if (flag || flag1) {
            if (!this.bindEntityTexture(p_renderModel_1_)) return;
            if(CharmsRewrite.INSTANCE.isToggled()) {
                CharmsRewrite.INSTANCE.getPattern().doRender(
                        p_renderModel_1_,
                        mainModel,
                        p_renderModel_2_,
                        p_renderModel_3_,
                        p_renderModel_4_,
                        p_renderModel_5_,
                        p_renderModel_6_,
                        p_renderModel_7_
                );
            } else {
                if (flag1) GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);

                mainModel.render(p_renderModel_1_, p_renderModel_2_, p_renderModel_3_, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_);

                if (flag1) GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
        }
    }

    protected boolean isVisible(T p_isVisible_1_) {
        return !p_isVisible_1_.isInvisible() || this.renderOutlines;
    }

    @Nullable @Override protected ResourceLocation getEntityTexture(T t) {return null;}
}
