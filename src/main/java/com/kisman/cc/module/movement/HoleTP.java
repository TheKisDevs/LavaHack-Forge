package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.BlockUtil;
import com.kisman.cc.util.EntityUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

public class HoleTP extends Module {
    private double[] oneblockPositions;
    private int packets;
    private boolean jumped;

    public HoleTP() {
        super("HoleTP", "g", Category.MOVEMENT);

        this.oneblockPositions = new double[] { 0.42, 0.75 };
        this.jumped = false;
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {
        if (event.getEra() == Event.Era.PRE && (!Speed.instance.isToggled()) || Speed.instance.speedMode.getValString().equalsIgnoreCase("Sti")) {
            if (!HoleTP.mc.player.onGround) {
                if (HoleTP.mc.gameSettings.keyBindJump.isKeyDown()) {
                    this.jumped = true;
                }
            }
            else {
                this.jumped = false;
            }
            if (!this.jumped && HoleTP.mc.player.fallDistance < 0.5 && BlockUtil.isInHole() && HoleTP.mc.player.posY - BlockUtil.getNearestBlockBelow() <= 1.125 && HoleTP.mc.player.posY - BlockUtil.getNearestBlockBelow() <= 0.95 && !EntityUtil.isOnLiquid() && !EntityUtil.isInLiquid()) {
                if (!HoleTP.mc.player.onGround) {
                    ++this.packets;
                }
                if (!HoleTP.mc.player.onGround && !HoleTP.mc.player.isInsideOfMaterial(Material.WATER) && !HoleTP.mc.player.isInsideOfMaterial(Material.LAVA) && !HoleTP.mc.gameSettings.keyBindJump.isKeyDown() && !HoleTP.mc.player.isOnLadder() && this.packets > 0) {
                    final BlockPos blockPos = new BlockPos(HoleTP.mc.player.posX, HoleTP.mc.player.posY, HoleTP.mc.player.posZ);
                    for (double position : oneblockPositions) {
                        HoleTP.mc.player.connection.sendPacket(new CPacketPlayer.Position((double)(blockPos.getX() + 0.5f), HoleTP.mc.player.posY - position, (double)(blockPos.getZ() + 0.5f), true));
                    }
                    HoleTP.mc.player.setPosition((double)(blockPos.getX() + 0.5f), BlockUtil.getNearestBlockBelow() + 0.1, (double)(blockPos.getZ() + 0.5f));
                    this.packets = 0;
                }
            }
        }
    });
}
