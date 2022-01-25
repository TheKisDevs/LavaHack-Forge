package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value = EntityPlayer.class, priority = Integer.MAX_VALUE)
public class MixinEntityPlayer extends MixinEntityLivingBase {
    @Shadow protected void doWaterSplashEffect() {}

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravel(float strafe, float vertical, float forward, CallbackInfo ci) {
        EventPlayerTravel event = new EventPlayerTravel(strafe, vertical, forward);
        Kisman.EVENT_BUS.post(event);

        if(event.isCancelled()) {
            move(MoverType.SELF, motionX, motionY, motionZ);
            ci.cancel();
        }
    }

    @Inject(method = "applyEntityCollision", at = @At("HEAD"), cancellable = true)
    private void applyEntityCollision(Entity entity, CallbackInfo ci) {
        EventPlayerApplyCollision event = new EventPlayerApplyCollision(entity);
        Kisman.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "isPushedByWater()Z", at = @At("HEAD"), cancellable = true)
    private void isPushedByWater(CallbackInfoReturnable<Boolean> cir) {
        EventPlayerPushedByWater event = new EventPlayerPushedByWater();
        Kisman.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }
}
