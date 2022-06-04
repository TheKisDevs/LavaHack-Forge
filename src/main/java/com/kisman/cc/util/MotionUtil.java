package com.kisman.cc.util;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

public class MotionUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static final double WALK_BPT = 0.215;

    public static final double SPRINT_BPT = 0.28;

    public static int getTicksUntil(double distance, boolean sprint){
        double m = sprint ? SPRINT_BPT : WALK_BPT;
        return (int) (distance / m) + 1;
    }

    public static CPacketPlayer.Position[] getPacketsTeleport(int ticks, boolean onGround, boolean smartOnGround, boolean cancelInBlock, boolean cancelOffGround, double x, double y, double z, double x1, double y1, double z1){
        if(ticks < 2){
            return new CPacketPlayer.Position[]{new CPacketPlayer.Position(x1, y1, z1, onGround)};
        }
        double dX = x1 - x;
        double dY = y1 - y;
        double dZ = z1 - z;
        double aX = dX / (double) ticks;
        double aY = dY / (double) ticks;
        double aZ = dZ / (double) ticks;
        CPacketPlayer.Position[] packets = new CPacketPlayer.Position[ticks];
        double cX = x;
        double cY = y;
        double cZ = z;
        for(int i = 0; i < (ticks - 1); i++){
            cX += aX;
            cY += aY;
            cZ += aZ;
            Material material = mc.world.getBlockState(new BlockPos(cX, cY, cZ).down()).getMaterial();
            Material material1 = mc.world.getBlockState(new BlockPos(cX, cY, cZ)).getMaterial();
            if(cancelInBlock && material1.isSolid())
                continue;
            if(cancelOffGround && material.isLiquid() || material.isReplaceable())
                continue;
            boolean og = material.isLiquid() || material.isReplaceable() || material1.isSolid();
            packets[i] = new CPacketPlayer.Position(cX, cY, cZ, smartOnGround ? og : onGround);
        }
        packets[ticks - 1] = new CPacketPlayer.Position(x1, y1, z1, onGround);
        return packets;
    }
}
