package com.kisman.cc.mixin.mixins;

import com.kisman.cc.features.module.render.CharmsRewrite;
import com.kisman.cc.features.module.render.crystalmodifier.CrystalModelHandler;
import com.kisman.cc.mixin.mixins.accessor.AccessorRenderEnderCrystal;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderEnderCrystal.class, priority = 10000)
public class MixinRenderEnderCrystal extends Render<EntityEnderCrystal> {
    @Final @Shadow private ModelBase modelEnderCrystal;//field_76995_b
    @Final @Shadow private ModelBase modelEnderCrystalNoBase;//field_188316_g
    @Final @Shadow private static ResourceLocation ENDER_CRYSTAL_TEXTURES;
    @Shadow @Nullable @Override protected ResourceLocation getEntityTexture(@NotNull EntityEnderCrystal entityEnderCrystal) {return null;}
    protected MixinRenderEnderCrystal(RenderManager renderManager) {super(renderManager);}

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(RenderManager renderManagerIn, CallbackInfo ci) {
        ((AccessorRenderEnderCrystal) this).modelEnderCrystal(new CrystalModelHandler(true));
        ((AccessorRenderEnderCrystal) this).modelEnderCrystalNoBase(new CrystalModelHandler(false));
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void IdoRender(EntityEnderCrystal entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if(CharmsRewrite.INSTANCE.isToggled()) {
            float defaultSpinSpeed = entity.innerRotation + partialTicks;
            float defaultBounceSpeed = MathHelper.sin(defaultSpinSpeed * 0.2F) / 2.0F + 0.5F;
            defaultBounceSpeed += defaultBounceSpeed * defaultBounceSpeed;

            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            bindTexture(ENDER_CRYSTAL_TEXTURES);

            CharmsRewrite.INSTANCE.getPattern().doRender(
                    entity,
                    (entity.shouldShowBottom() ? modelEnderCrystal : modelEnderCrystalNoBase),
                    0,
                    defaultSpinSpeed * 3,
                    defaultBounceSpeed * 0.2f,
                    0,
                    0,
                    0.0625f
            );

            GL11.glPopMatrix();

            ci.cancel();
        }
    }
}
