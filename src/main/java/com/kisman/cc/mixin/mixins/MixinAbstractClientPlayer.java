package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.client.Cape;
import i.gishreloaded.gishcode.utils.TimerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {
    Minecraft mc = Minecraft.getMinecraft();
    String str1 = "cape-";
    String str2 = ".png";
    int count = 0;
    TimerUtils timer = new TimerUtils();

    @Shadow
    public NetworkPlayerInfo playerInfo;

    @Shadow
    protected abstract boolean isSpectator();

    @Inject(method = "getLocationSkin()Lnet/minecraft/util/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getLocationSkin(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        if(Kisman.instance.moduleManager.getModule("Charms").isToggled() && Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule("Charms"), "Texture").getValBoolean())
            callbackInfoReturnable.setReturnValue(new ResourceLocation("kismancc:charms/charms1.png"));
    }

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        if(Cape.instance.isToggled() && playerInfo == mc.player.getPlayerInfo()) {
            if(!Cape.instance.gif.getValBoolean()) {
                cir.setReturnValue(new ResourceLocation("kismancc:cape/cape1.png"));
            } else {
                cir.setReturnValue(getCape());
            }
        }
    }

    private ResourceLocation getCape() {
        if(count > 34) count = 0;

        final ResourceLocation cape = new ResourceLocation("kismancc:cape/rainbow/" + str1 + count + str2);

        if(timer.passedMillis(100)) {
            count++;
            timer.reset();
        }

        return cape;
    }
}