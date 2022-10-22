package com.kisman.cc.features.module.movement;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;

public class CornerClip extends Module {
    public Setting timeout = register(new Setting("Timeout", this, 5,1,10, false));
    public Setting disableSetting = register(new Setting("Auto Disable",this, false));

    public int disableThingy;

    public CornerClip() {
        super("CornerClip", "Phases slightly into the corner of a your surrounding to prevent crystal damage", Category.MOVEMENT);
    }

    @Override
    public void update() {
        if (mc.player == null || mc.world == null) return;

        if (movingByKeys()) {
            this.disable();
            return;
        }

        if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().grow(0.01, 0, 0.01)).size() < 2) mc.player.setPosition(roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.301, Math.floor(mc.player.posX) + 0.699), mc.player.posY, roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.301, Math.floor(mc.player.posZ) + 0.699));
        else if (mc.player.ticksExisted % this.timeout.getValInt() == 0) {
            mc.player.setPosition(mc.player.posX + MathHelper.clamp(roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.241, Math.floor(mc.player.posX) + 0.759) - mc.player.posX, -0.03, 0.03), mc.player.posY, mc.player.posZ + MathHelper.clamp(roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.241, Math.floor(mc.player.posZ) + 0.759) - mc.player.posZ, -0.03, 0.03));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.23, Math.floor(mc.player.posX) + 0.77), mc.player.posY, roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.23, Math.floor(mc.player.posZ) + 0.77), true));

            if (this.disableSetting.getValBoolean()) disableThingy++;
            else disableThingy = 0;
        }

        if (disableThingy >= 2 && this.disableSetting.getValBoolean()) {
            disableThingy = 0;
            this.disable();
        }
    }

    private boolean movingByKeys() {
        return mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown();
    }

    public static double roundToClosest(double num, double low, double high) {
        double d1 = num - low;
        double d2 = high - num;

        if (d2 > d1) return low;
        else return high;
    }
}