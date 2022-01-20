package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.client.Cape;
import i.gishreloaded.gishcode.utils.TimerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractClientPlayer.class, priority = 10000)
public class MixinAbstractClientPlayer extends MixinEntityPlayer {
    Minecraft mc = Minecraft.getMinecraft();
    String str1 = "cape-";
    String str2 = ".png";
    int count = 0;
    TimerUtils timer = new TimerUtils();
    @Shadow public NetworkPlayerInfo playerInfo;
    @Shadow public boolean isSpectator() {return true;}

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        if(Cape.instance.isToggled() && playerInfo == mc.player.getPlayerInfo()) {
            switch( Cape.instance.mode.getValString()) {
                case "Static": {
                    cir.setReturnValue(new ResourceLocation("kismancc:cape/cape1.png"));
                    break;
                }
                case "Gif": {
                    cir.setReturnValue(getCape());
                    break;
                }
                case "xulu+": {
                    cir.setReturnValue(new ResourceLocation("kismancc:cape/xuluplus/xulupluscape.png"));
                    break;
                }
            }
        }
    }

    private ResourceLocation getCape() {
        if(count > 34) count = 0;

        final ResourceLocation cape = new ResourceLocation("kismancc:cape/rainbow/" + str1 + count + str2);

        if(timer.passedMillis(85)) {
            count++;
            timer.reset();
        }

        return cape;
    }
}