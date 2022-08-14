package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventEntitySpawn;
import com.kisman.cc.event.events.EventSpawnEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = World.class, priority = 10000)
public class MixinWorld {
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
}
