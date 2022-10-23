package com.kisman.cc.mixin.mixins;

import com.kisman.cc.event.events.EventLiquidCollideCheck;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLiquid.class)
public class MixinBlockLiquid {

    @Inject(method = "canCollideCheck", at = @At("HEAD"), cancellable = true)
    private void onCanCollideCheck(IBlockState state, boolean hitIfLiquid, CallbackInfoReturnable<Boolean> cir){
        EventLiquidCollideCheck event = new EventLiquidCollideCheck(state, hitIfLiquid);
        if(event.isCancelled())
            cir.setReturnValue(true);
    }
}
