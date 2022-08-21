package com.kisman.cc.mixin.mixins.accessor;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.NetHandlerPlayClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NetHandlerPlayClient.class)
public interface AccessorNetHandlerPlayClient {
    @Accessor("doneLoadingTerrain") boolean isDoneLoadingTerrain();
    @Accessor("doneLoadingTerrain") void setDoneLoadingTerrain(boolean loaded);
    @Accessor("profile") void profile(GameProfile profile);
}
