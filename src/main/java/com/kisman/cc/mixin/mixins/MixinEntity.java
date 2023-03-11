package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.TurnEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(value = Entity.class, priority = 10000)
public abstract class MixinEntity {
    @Shadow public boolean isAirBorne;
    @Shadow public double posX;
    @Shadow public double posY;
    @Shadow public double posZ;
    @Shadow public double prevPosX;
    @Shadow public double prevPosY;
    @Shadow public double prevPosZ;
    @Shadow public double lastTickPosX;
    @Shadow public double lastTickPosY;
    @Shadow public double lastTickPosZ;
    @Shadow public float prevRotationYaw;
    @Shadow public float prevRotationPitch;
    @Shadow public float rotationPitch;
    @Shadow public float rotationYaw;
    @Shadow public boolean onGround;
    @Shadow public double motionX;
    @Shadow public double motionY;
    @Shadow public double motionZ;
    @Shadow public World world;
    @Shadow public void move(final MoverType type, final double x, final double y, final double z) {}
    @Shadow public AxisAlignedBB getEntityBoundingBox() {return null;}
    @Shadow protected boolean getFlag(final int p0) {return true;}
    @Shadow public Entity getLowestRidingEntity() {return null;}

    @Shadow public abstract void moveRelative(float strafe, float up, float forward, float friction);

    @Shadow public abstract String getName();

    @Shadow public float fallDistance;

    @Shadow public abstract boolean isSprinting();

    @Shadow public abstract boolean isRiding();

    @Shadow public abstract boolean isInWater();

    @Shadow public EntityDataManager dataManager;

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    public void onTurn(float yaw, float pitch, CallbackInfo ci) {
        TurnEvent event = new TurnEvent(
                yaw,
                pitch,
                rotationYaw,
                rotationPitch,
                prevRotationYaw,
                prevRotationPitch
        );
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();

            rotationYaw = event.rotationYaw;
            rotationPitch = event.rotationPitch;
            prevRotationYaw = event.prevYaw;
            prevRotationPitch = event.prevPitch;
        }
    }
}
