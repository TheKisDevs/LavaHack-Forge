package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventGetBlockState;
import com.kisman.cc.event.events.EventRemoveEntity;
import com.kisman.cc.event.events.EventEntitySpawn;
import com.kisman.cc.event.events.EventSpawnEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
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
