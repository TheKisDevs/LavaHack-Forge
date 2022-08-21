package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.client.entity.EntityPlayerSP;

import java.util.function.Consumer;

public class EventPlayerMotionUpdate extends Event {
    protected float yaw;
    protected float pitch;
    protected double x;
    protected double y;
    protected double z;
    protected boolean onGround;
    private Consumer<EntityPlayerSP> funcToCall;
    private boolean isForceCancelled;

    public boolean modified = false;

    public EventPlayerMotionUpdate(Era era, float yaw, float pitch, double posX, double posY, double posZ, boolean OnGround) {
        super(era);
        this.funcToCall = null;
        this.yaw = yaw;
        this.pitch = pitch;
        this.x = posX;
        this.y = posY;
        this.z = posZ;
        this.onGround = OnGround;
    }

    public EventPlayerMotionUpdate(Era stage, EventPlayerMotionUpdate event) {
        this(
                stage,
                event.yaw,
                event.pitch,
                event.x,
                event.y,
                event.z,
                event.onGround
        );
    }

    public Consumer<EntityPlayerSP> getFunc() {
        return this.funcToCall;
    }

    public void setFunct(final Consumer<EntityPlayerSP> post) {
        modified = true;
        this.funcToCall = post;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(final float yaw) {
        modified = true;
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(final float pitch) {
        modified = true;
        this.pitch = pitch;
    }

    public void setYaw(final double yaw) {
        modified = true;
        this.yaw = (float)yaw;
    }

    public void setPitch(final double pitch) {
        modified = true;
        this.pitch = (float)pitch;
    }

    public void forceCancel() {
        this.isForceCancelled = true;
    }

    public boolean isForceCancelled() {
        return this.isForceCancelled;
    }

    public void setX(final double posX) {
        modified = true;
        this.x = posX;
    }

    public void setY(final double d) {
        modified = true;
        this.y = d;
    }

    public void setZ(final double posZ) {
        modified = true;
        this.z = posZ;
    }

    public void setOnGround(final boolean b) {
        modified = true;
        this.onGround = b;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public String getName() {
        return "player_motion";
    }

    public static class Riding extends EventPlayerMotionUpdate {
        private float moveStrafing;
        private float moveForward;
        private boolean jump;
        private boolean sneak;

        public Riding(
                Era era,
                float yaw,
                float pitch,
                double posX,
                double posY,
                double posZ,
                boolean OnGround,
                float moveStrafing,
                float moveForward,
                boolean jump,
                boolean sneak
        ) {
            super(era, yaw, pitch, posX, posY, posZ, OnGround);
            this.moveStrafing = moveStrafing;
            this.moveForward = moveForward;
            this.jump = jump;
            this.sneak = sneak;
        }

        public Riding(Era era, EventPlayerMotionUpdate.Riding event) {
            this(
                    era,
                    event.getYaw(),
                    event.getPitch(),
                    event.getX(),
                    event.getY(),
                    event.getZ(),
                    event.isOnGround(),
                    event.moveStrafing,
                    event.moveForward,
                    event.jump,
                    event.sneak
            );
        }

        public float getMoveStrafing() {
            return moveStrafing;
        }

        public void setMoveStrafing(float moveStrafing) {
            modified = true;
            this.moveStrafing = moveStrafing;
        }

        public float getMoveForward() {
            return moveForward;
        }

        public void setMoveForward(float moveForward) {
            modified = true;
            this.moveForward = moveForward;
        }

        public boolean isJump() {
            return jump;
        }

        public void setJump(boolean jump) {
            modified = true;
            this.jump = jump;
        }

        public boolean isSneak() {
            return sneak;
        }

        public void setSneak(boolean sneak) {
            modified = true;
            this.sneak = sneak;
        }
    }
}
