package com.kisman.cc.features.module.movement;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;

public class CornerClip extends Module {
    public Setting timeout = register(new Setting("Timeout", this, 5,1,10, false));
    public Setting disableSetting = register(new Setting("Auto Disable",this, false));

    public int disableTicks;

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

        if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().grow(0.01, 0, 0.01)).size() < 2){
            double x = roundToClosest(
                    mc.player.posX,
                    Math.floor(mc.player.posX) + 0.301,
                    Math.floor(mc.player.posX) + 0.699
            );
            double y = mc.player.posY;
            double z = roundToClosest(
                    mc.player.posZ,
                    Math.floor(mc.player.posZ) + 0.301,
                    Math.floor(mc.player.posZ) + 0.699
            );
            mc.player.setPosition(x, y, z);
            checkDisable();
            return;
        }

        if (mc.player.ticksExisted % this.timeout.getValInt() == 0) {
            double x1 = mc.player.posX +
                    MathHelper.clamp(
                    roundToClosest(
                            mc.player.posX, Math.floor(mc.player.posX) + 0.241,
                            Math.floor(mc.player.posX) + 0.759
                    ) - mc.player.posX,
                    -0.03,
                    0.03
            );
            double y1 = mc.player.posY;
            double z1 = mc.player.posZ +
                    MathHelper.clamp(
                            roundToClosest(
                                    mc.player.posZ, Math.floor(mc.player.posZ) + 0.241,
                                    Math.floor(mc.player.posZ) + 0.759
                            ) - mc.player.posZ,
                    -0.03,
                    0.03
            );
            mc.player.setPosition(x1, y1, z1);

            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));

            double x2 = roundToClosest(
                    mc.player.posX,
                    Math.floor(mc.player.posX) + 0.23,
                    Math.floor(mc.player.posX) + 0.77
            );
            double y2 = mc.player.posY;
            double z2 = roundToClosest(
                    mc.player.posZ,
                    Math.floor(mc.player.posZ) + 0.23,
                    Math.floor(mc.player.posZ) + 0.77
            );

            mc.player.connection.sendPacket(new CPacketPlayer.Position(x2, y2, z2, true));

            if (this.disableSetting.getValBoolean())
                disableTicks++;
            else
                disableTicks = 0;

            checkDisable();
        }
    }

    private void checkDisable(){
        if (disableTicks >= 2 && this.disableSetting.getValBoolean()) {
            disableTicks = 0;
            this.disable();
        }
    }

    private boolean movingByKeys() {
        return mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown();
    }

    public static double roundToClosest(double num, double low, double high) {
        // lD = low difference, hD = high difference - Cubic
        double lD = num - low;
        double hD = high - num;
        // >= because we want to round up if hD is 0.5 - Cubic
        return hD >= lD ? low : high;
    }
}