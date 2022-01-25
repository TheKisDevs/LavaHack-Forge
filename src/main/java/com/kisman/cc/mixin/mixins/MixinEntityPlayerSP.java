package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.MovementInput;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value = EntityPlayerSP.class, priority = 10000)
public class MixinEntityPlayerSP extends MixinAbstractClientPlayer {
    @Shadow public MovementInput movementInput;
    @Shadow protected boolean isCurrentViewEntity() {return true;}

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MoverType type, double x, double y, double z, CallbackInfo ci) {
        EventPlayerMove event = new EventPlayerMove(type, x, y, z);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            move(type, event.x, event.y, event.z);
            ci.cancel();
        }
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void onPreUpdateWalkingPlayer(CallbackInfo ci) {
        EventPlayerMotionUpdate event = new EventPlayerMotionUpdate(Event.Era.PRE, rotationYaw, rotationPitch, this.posX, this.getEntityBoundingBox().minY, this.posZ, this.onGround);
        Kisman.EVENT_BUS.post(event);
        this.rotationYaw = event.getYaw();
        this.rotationPitch = event.getPitch();
        if(event.isCancelled()) ci.cancel();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"), cancellable = true)
    public void onPostUpdateWalkingPlayer(CallbackInfo ci) {
        EventPlayerMotionUpdate event = new EventPlayerMotionUpdate(Event.Era.POST, rotationYaw, rotationPitch, this.posX, this.getEntityBoundingBox().minY, this.posZ, this.onGround);
        Kisman.EVENT_BUS.post(event);
        if(event.isCancelled()) ci.cancel();
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    public void onUpdate(CallbackInfo ci) {
        EventPlayerUpdate event = new EventPlayerUpdate();
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "pushOutOfBlocks(DDD)Z", at = @At("HEAD"), cancellable = true)
    public void pushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        EventPlayerPushOutOfBlocks event = new EventPlayerPushOutOfBlocks(x, y, z);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) cir.setReturnValue(false);
    }
}
