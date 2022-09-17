package com.kisman.cc.features.module.combat.autorer;

import com.kisman.cc.util.world.BlockUtil2;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.UUID;

public class MotionPredictor extends EntityOtherPlayerMP {
    public double extraPosX;
    public double extraPosY;
    public double extraPosZ;

    public double lastExtraPosX;
    public double lastExtraPosY;
    public double lastExtraPosZ;

    public boolean outOfBlocks;

    public EntityPlayer player;
    public MovementInput movementInput;
    public boolean safe;
    public boolean active;
    public boolean wasPhasing;
    public boolean shrink;

    public MotionPredictor(World worldIn, EntityPlayer from) {
        super(worldIn, new GameProfile(from.getGameProfile().getId(), "MotionPredictor-" + from.getName()));
        player = from;
        setEntityId(from.getEntityId() * -2);
        copyLocationAndAnglesFrom(from);
    }

    @SuppressWarnings("unused")
    private MotionPredictor(World worldIn) {
        super(worldIn, new GameProfile(UUID.randomUUID(), "MotionPredictor"));
    }

    public void resetMotion() {
        motionX = 0.0;
        motionY = 0.0;
        motionZ = 0.0;
    }

    public void pushOutOfBlocks() {
        AxisAlignedBB axisalignedbb = shrink ? getEntityBoundingBox().expand(-0.0625, -0.0625, -0.0625) : getEntityBoundingBox();

        BlockUtil2.pushOutOfBlocks(this, posX - width * 0.35D, axisalignedbb.minY + 0.5D, posZ + width * 0.35D);
        BlockUtil2.pushOutOfBlocks(this, posX - width * 0.35D, axisalignedbb.minY + 0.5D, posZ - width * 0.35D);
        BlockUtil2.pushOutOfBlocks(this, posX + width * 0.35D, axisalignedbb.minY + 0.5D, posZ - width * 0.35D);
        BlockUtil2.pushOutOfBlocks(this, posX + width * 0.35D, axisalignedbb.minY + 0.5D, posZ + width * 0.35D);
    }

    public void detectWasPhasing() {
        wasPhasing = false;
        if (outOfBlocks) {
            resetMotion();
            pushOutOfBlocks();
            wasPhasing = motionX != 0.0 || motionY != 0.0 || motionZ != 0.0;
        }
    }

    public void updateFromTrackedEntity() {
        motionX = player.motionX;
        motionY = player.motionY;
        motionZ = player.motionZ;

        posX += Math.abs(motionX) >= 0.1 ? motionX : 0.0;
        posY += Math.abs(motionY) >= 0.1 ? motionY : 0.0;
        posZ += Math.abs(motionZ) >= 0.1 ? motionZ : 0.0;
        setPosition(posX, posY, posZ);

        if (outOfBlocks && !wasPhasing) {
            resetMotion();
            pushOutOfBlocks();
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            motionX = player.motionX;
            motionY = player.motionY;
            motionZ = player.motionZ;
            setPosition(posX, posY, posZ);
        }

        onGround = player.onGround;
        prevPosX = player.prevPosX;
        prevPosY = player.prevPosY;
        prevPosZ = player.prevPosZ;
        collided = player.collided;
        collidedHorizontally = player.collidedHorizontally;
        collidedVertically = player.collidedVertically;
        moveForward = player.moveForward;
        moveStrafing = player.moveStrafing;
        moveVertical = player.moveVertical;
        lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;
        lastExtraPosX = extraPosX;
        lastExtraPosY = extraPosY;
        lastExtraPosZ = extraPosZ;
    }

    @Override public void onUpdate() {}
    @Override public void onLivingUpdate() {}

    @Override
    public void setDead() {
        isDead = true;
        dead = true;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }
}
