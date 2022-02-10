package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.client.Cape;
import com.mojang.authlib.GameProfile;
import i.gishreloaded.gishcode.utils.TimerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractClientPlayer.class, priority = 10000)
public abstract class MixinAbstractClientPlayer extends EntityPlayer {
    Minecraft mc = Minecraft.getMinecraft();
    String str1 = "cape-";
    String str2 = ".png";
    int count = 0;
    TimerUtils timer = new TimerUtils();
    @Shadow public NetworkPlayerInfo playerInfo;

    public MixinAbstractClientPlayer(World worldIn, GameProfile gameProfileIn) {super(worldIn, gameProfileIn);}

    @Shadow public boolean isSpectator() {return true;}

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        if(Cape.instance.isToggled() && playerInfo == mc.player.getPlayerInfo()) {
            switch(Cape.instance.mode.getValString()) {
                case "Gif":
                    cir.setReturnValue(getCape());
                    break;
                case "Xulu+":
                    cir.setReturnValue(new ResourceLocation("kismancc:cape/xuluplus/xulupluscape.png"));
                    break;
                case "Kuro":
                    cir.setReturnValue(new ResourceLocation("kismancc:cape/kuro/kuro.png"));
                    break;
                case "GentleManMC":
                    cir.setReturnValue(new ResourceLocation("kismancc:cape/gentlemanmc/GentlemanMC.png"));
                    break;
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