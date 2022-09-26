package com.kisman.cc.mixin.mixins;

import com.kisman.cc.features.module.exploit.PreciseStrongholdFinder;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.structure.MapGenStronghold;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGeneratorOverworld.class)
public class MixinChunkGeneratorOverworld {

    @Shadow private MapGenStronghold strongholdGenerator;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(World worldIn, long seed, boolean mapFeaturesEnabledIn, String generatorOptions, CallbackInfo ci){
        PreciseStrongholdFinder.MAP_GEN_STRONGHOLD = strongholdGenerator;
    }
}
