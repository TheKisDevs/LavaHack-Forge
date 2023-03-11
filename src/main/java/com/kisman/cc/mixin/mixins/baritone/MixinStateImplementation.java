package com.kisman.cc.mixin.mixins.baritone;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockStateContainer.StateImplementation.class)
public class MixinStateImplementation {
    @Shadow @Final private ImmutableMap<IProperty<?>, Comparable<?>> properties;
    @Unique private int hashCode;
    @SuppressWarnings("OverwriteAuthorRequired") @Override @Overwrite public int hashCode() {
        return hashCode;
    }
    @Inject(method = "<init>*", at = @At("RETURN")) private void onInit(CallbackInfo ci) {hashCode = properties.hashCode();}
}
