package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.combat.AutoCrystal;
import com.kisman.cc.module.render.Charms;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import static org.lwjgl.opengl.GL11.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLivingBase.class)
public abstract class MixinRendererLivingEntity<T extends EntityLivingBase> extends Render<T> {
    protected ModelBase entityModel;

    protected MixinRendererLivingEntity() {
        super((RenderManager) null);
    }

    @Inject(method = {"doRender"}, at = @At("HEAD"))
    public void doRenderPre(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if(Charms.instance.isToggled()) {
            glEnable(32823);
            glPolygonOffset(1f, -1100000);

            if(Charms.instance.targetRender.getValBoolean()) {
                if(AutoCrystal.instance.e_target == (EntityLivingBase) entity)
                glColor4f(1, 0.03f, 0.9f, 0.5f);
            }
        }
    }

    @Inject(method = {"doRender"}, at = @At("RETURN"))
    public void doRenderPost(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if(Charms.instance.isToggled()) {
            glPolygonOffset(1, 1000000);
            glDisable(32823);
        }
    }
}
