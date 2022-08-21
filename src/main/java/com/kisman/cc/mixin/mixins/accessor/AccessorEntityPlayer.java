package com.kisman.cc.mixin.mixins.accessor;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPlayer.class)
public interface AccessorEntityPlayer {
    @Accessor(value = "speedInAir") void setSpeedInAir(float value);
    @Accessor("gameProfile") void profile(GameProfile profile);
}
