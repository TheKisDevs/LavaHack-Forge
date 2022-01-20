package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.Animation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.*;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityLivingBase.class, priority = 10000)
public class MixinEntityLivingBase extends MixinEntity {
    @Shadow public EnumHand swingingHand;
    @Shadow public ItemStack activeItemStack;
    @Shadow public float moveStrafing;
    @Shadow public float moveVertical;
    @Shadow public float moveForward;
    @Shadow protected void jump() {}
    @Shadow public boolean isElytraFlying() {return true;}
    @Shadow public  boolean isPotionActive(Potion potionIn) {return false;}
    @Shadow public  PotionEffect getActivePotionEffect(Potion potionIn) {return null;}

    @Inject(method = "getArmSwingAnimationEnd", at = @At("HEAD"), cancellable = true)
    private void yesido(CallbackInfoReturnable<Integer> cir) {
        if(Animation.instance.isToggled()) {
            if(isPotionActive(MobEffects.HASTE)) {
                cir.setReturnValue(Animation.instance.speed.getValInt() - (getActivePotionEffect(MobEffects.HASTE).getAmplifier()));
            } else {
                cir.setReturnValue(isPotionActive(MobEffects.MINING_FATIGUE) ? Animation.instance.speed.getValInt() + (getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier() + 1) * 2 : Animation.instance.speed.getValInt());
            }
        }
    }
}
