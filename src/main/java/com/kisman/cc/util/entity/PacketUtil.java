package com.kisman.cc.util.entity;

import com.kisman.cc.mixin.mixins.accessor.AccessorNetHandlerPlayClient;
import com.kisman.cc.util.Globals;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

public class PacketUtil implements Globals {
    public static void handlePosLook(SPacketPlayerPosLook packetIn,
                                     Entity entity,
                                     boolean noRotate) {
        handlePosLook(packetIn, entity, noRotate, false);
    }

    public static void handlePosLook(SPacketPlayerPosLook packet,
                                     Entity entity,
                                     boolean noRotate,
                                     boolean event) {
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        float yaw = packet.getYaw();
        float pitch = packet.getPitch();

        if ( packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X) ) {
            x += entity.posX;
        } else {
            entity.motionX = 0.0D;
        }

        if ( packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y) ) {
            y += entity.posY;
        } else {
            entity.motionY = 0.0D;
        }

        if ( packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z) ) {
            z += entity.posZ;
        } else {
            entity.motionZ = 0.0D;
        }

        if ( packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X_ROT) ) {
            pitch += entity.rotationPitch;
        }

        if ( packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT) ) {
            yaw += entity.rotationYaw;
        }

        entity.setPositionAndRotation(x, y, z,
                noRotate ? entity.rotationYaw : yaw,
                noRotate ? entity.rotationPitch : pitch);

        Packet<?> confirm = new CPacketConfirmTeleport(packet.getTeleportId());
        CPacketPlayer posRot = positionRotation(entity.posX,
                entity.getEntityBoundingBox()
                        .minY,
                entity.posZ,
                yaw,
                pitch,
                false);

        if ( event ) {
            mc.player.connection.sendPacket(confirm);
//            Kisman.instance.rotationUtils.setBlocking(true);
            mc.player.connection.sendPacket(posRot);
//            Kisman.instance.rotationUtils.setBlocking(false);
        } else {
            mc.player.connection.sendPacket(confirm);
            mc.player.connection.sendPacket(posRot);
        }

        // might be called async
        mc.addScheduledTask(PacketUtil::loadTerrain);
    }

    public static CPacketPlayer positionRotation(double x,
                                                 double y,
                                                 double z,
                                                 float yaw,
                                                 float pitch,
                                                 boolean onGround) {
        return new CPacketPlayer.PositionRotation(x, y, z, yaw, pitch, onGround);
    }
    public static void loadTerrain()
    {
        // This might get called asynchronously so better be safe
        mc.addScheduledTask(() ->
        {
            if (!((AccessorNetHandlerPlayClient) mc.player.connection)
                    .isDoneLoadingTerrain())
            {
                mc.player.prevPosX = mc.player.posX;
                mc.player.prevPosY = mc.player.posY;
                mc.player.prevPosZ = mc.player.posZ;
                ((AccessorNetHandlerPlayClient) mc.player.connection)
                        .setDoneLoadingTerrain(true);

                mc.displayGuiScreen(null);
            }
        });
    }
}