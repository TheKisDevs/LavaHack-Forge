package com.kisman.cc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class TargetUtil {
    static float minrange = 100;
    static EntityPlayer targetPlayer;

    static Minecraft mc = Minecraft.getMinecraft();

    public static EntityPlayer getTarget() {
        mc.world.playerEntities.stream().filter(entityPlayer -> entityPlayer.getDistance(entityPlayer) <= minrange).filter(entityPlayer -> entityPlayer != mc.player).forEach(entityPlayer -> {
            targetPlayer = entityPlayer;
            minrange = entityPlayer.getDistance(entityPlayer);
        });

        try {
            return targetPlayer;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
