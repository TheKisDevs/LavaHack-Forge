package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerApplyCollision;
import com.kisman.cc.event.events.EventPlayerJump;
import com.kisman.cc.event.events.EventPlayerPushedByWater;
import com.kisman.cc.event.events.EventPlayerTravel;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityPlayer.class, priority = Integer.MAX_VALUE)
public abstract class MixinEntityPlayer extends EntityLivingBase {
    public MixinEntityPlayer(World worldIn) {super(worldIn);}
    @Shadow protected void doWaterSplashEffect() {}
    @Shadow public @NotNull String getName() {return "";}

    @Shadow @Final protected static DataParameter<Byte> MAIN_HAND;

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        if(Minecraft.getMinecraft().player.getName().equals(getName())) {
            EventPlayerJump event = new EventPlayerJump(this);
            Kisman.EVENT_BUS.post(event);

            if(event.isCancelled()) ci.cancel();
        }
    }

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

        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "isPushedByWater()Z", at = @At("HEAD"), cancellable = true)
    private void isPushedByWater(CallbackInfoReturnable<Boolean> cir) {
        EventPlayerPushedByWater event = new EventPlayerPushedByWater();
        Kisman.EVENT_BUS.post(event);

        if (event.isCancelled()) cir.setReturnValue(false);
    }

    /**
     * @author _kisman_
     * @reason fix of crash
     */
    @Overwrite
    @NotNull
    public EnumHandSide getPrimaryHand() {
        try {
            return this.dataManager.get(MAIN_HAND) == 0 ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
        } catch(Exception ignored) {
            return EnumHandSide.RIGHT;
        }
    }
}
