package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.RenderEntitiesEvent;
import com.kisman.cc.event.events.RenderEntityEvent;
import com.kisman.cc.features.module.render.NoRender;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderGlobal.class, priority = 10000)
public class MixinRenderGlobal {
    @Inject(method = "drawSelectionBox", at = @At("HEAD"), cancellable = true)
    public void onDrawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks, CallbackInfo ci) {
        if(NoRender.instance.isToggled() && NoRender.instance.defaultBlockHighlight.getValBoolean()) ci.cancel();
    }

    @Inject(method = "renderEntities", at = @At("HEAD"), cancellable = true)
    public void renderEntitiesHead(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo ci) {
        RenderEntityEvent.setRenderingEntities(true);

        RenderEntitiesEvent event = new RenderEntitiesEvent.Start();

        Kisman.EVENT_BUS.post(event);

        if(event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderEntities", at = @At("RETURN"))
    public void renderEntitiesReturn(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo ci) {
        RenderEntitiesEvent event = new RenderEntitiesEvent.End();

        Kisman.EVENT_BUS.post(event);

        RenderEntityEvent.setRenderingEntities(false);
    }

    @Redirect(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;shouldRender(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;DDD)Z"))
    public boolean renderEntitiesRedirectShouldRenderHook(RenderManager instance, Entity entity, ICamera camera, double camX, double camY, double camZ) {
        RenderEntityEvent event = new RenderEntityEvent.Check(entity);

        Kisman.EVENT_BUS.post(event);

        return instance.shouldRender(entity, camera, camX, camY, camZ);
    }
}
