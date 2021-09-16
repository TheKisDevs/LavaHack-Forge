package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {
    @Shadow
    @Nullable
    protected NetworkPlayerInfo playerInfo;

    @Inject(method = {"getLocationSkin()Lnet/minecraft/util/ResourceLocation;"}, at = {@At("HEAD")}, cancellable = true)
    public void getLocationSkin(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        if(Kisman.instance.moduleManager.getModule("Charms").isToggled() && Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule("Charms"), "Texture").getValBoolean())
            callbackInfoReturnable.setReturnValue(new ResourceLocation("kismancc:charms/charms1.png"));
    }
}