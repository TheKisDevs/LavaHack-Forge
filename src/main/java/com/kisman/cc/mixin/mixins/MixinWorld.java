package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = World.class, priority = 10000)
public abstract class MixinWorld {

    @Shadow public abstract void calculateInitialSkylight();

    @Inject(method = "onEntityAdded", at = @At("HEAD"))
    public void onEntityAdded(Entity entityIn, CallbackInfo ci) {
        Kisman.EVENT_BUS.post(new EventSpawnEntity(entityIn));
    }

    @Inject(method = "spawnEntity", at = @At("HEAD"), cancellable = true)
    public void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> ci){
        EventEntitySpawn event = new EventEntitySpawn(entity);
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled())
            ci.setReturnValue(false);
    }

    @Inject(method = "onEntityRemoved", at = @At("HEAD"))
    public void onEntityRemovedHook(Entity entityIn, CallbackInfo ci) {
        Kisman.EVENT_BUS.post(new EventRemoveEntity(entityIn.getEntityId()));
    }

    @Inject(method = "updateEntity", at = @At("HEAD"))
    private void updateEntityHook(Entity ent, CallbackInfo ci) {
        EventUpdateEntity event = new EventUpdateEntity(ent);

        Kisman.EVENT_BUS.post(event);
    }

    @Inject(method = "updateEntities", at = @At("HEAD"))
    private void updateEntitiesHeadHook(CallbackInfo ci) {
        EventUpdateEntities event = new EventUpdateEntities.Pre();

        Kisman.EVENT_BUS.post(event);
    }

    @Inject(method = "updateEntities", at = @At("RETURN"))
    private void updateEntitiesReturnHook(CallbackInfo ci) {
        EventUpdateEntities event = new EventUpdateEntities.Post();

        Kisman.EVENT_BUS.post(event);
    }
    /*
    @Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true)
    public void onGetBlockState(BlockPos blockPos, CallbackInfoReturnable<IBlockState> cir){
        EventGetBlockState event = new EventGetBlockState(blockPos);
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled())
            cir.setReturnValue(event.getReturnValue());
    }
     */
}
