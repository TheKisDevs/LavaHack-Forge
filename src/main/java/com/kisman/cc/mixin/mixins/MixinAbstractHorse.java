package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventEntityControl;
import net.minecraft.entity.passive.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author _kisman_
 * @since 23:55 of 13.06.2022
 */
@Mixin(AbstractHorse.class)
public class MixinAbstractHorse {
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    private void camBeStired(CallbackInfoReturnable<Boolean> cir) {
        EventEntityControl event = new EventEntityControl();
        Kisman.EVENT_BUS.post(event);

        if(event.cancelled) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isHorseSaddled", at = @At("HEAD"), cancellable = true)
    private void isHrseSaldeled(CallbackInfoReturnable<Boolean> cir) {
        EventEntityControl event = new EventEntityControl();
        Kisman.EVENT_BUS.post(event);

        if(event.cancelled) {
            cir.setReturnValue(true);
        }
    }
}
