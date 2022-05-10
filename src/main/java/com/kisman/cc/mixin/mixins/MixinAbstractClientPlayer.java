package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventCape;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractClientPlayer.class, priority = 10000)
public class MixinAbstractClientPlayer extends EntityPlayer {
    @Shadow public NetworkPlayerInfo playerInfo;

    public MixinAbstractClientPlayer(World worldIn, GameProfile gameProfileIn) {super(worldIn, gameProfileIn);}

    @Shadow public boolean isSpectator() {return true;}
    @Override public boolean isCreative() {return false;}

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        EventCape event = new EventCape(playerInfo);
        event.setResLoc(playerInfo == null ? null : playerInfo.getLocationCape());
        Kisman.EVENT_BUS.post(event);
        cir.setReturnValue(event.getResLoc());
    }
}