package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.movement.MovementUtil;
import me.zero.alpine.listener.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;

@ModuleInfo(
        name = "Jesus",
        category = Category.MOVEMENT
)
public class Jesus extends Module {
    private final Setting mode = register(new Setting("Mode", this, "Matrix", Arrays.asList("Matrix", "Matrix 6.3", "MatrixPixel", "Solid", "Entity")));
    private final SettingGroup speeds = register(new SettingGroup(new Setting("Speeds", this)));
    private final Setting speedPixel = register(speeds.add(new Setting("Speed Pixel", this, 4, 1, 10, false)));
    private final Setting speedMatrix = register(speeds.add(new Setting("Speed Matrix", this, 0.6f, 0, 1, false)));
    private final Setting speedSolid = register(speeds.add(new Setting("Speed Solid", this, 1, 0, 2, false)));

    public Jesus() {
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        EntityUtil.resetTimer();
        if(mc.player == null || mc.world == null) return;
        mc.player.jumpMovementFactor = 0.02f;
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        if(mode.checkValString("Matrix")) {
            if(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - -0.37f, mc.player.posZ)).getBlock() == Blocks.WATER) {
                mc.player.jump();
                mc.player.jumpMovementFactor = 0;

                mc.player.motionX *= speedMatrix.getValDouble();
                mc.player.motionZ *= speedMatrix.getValDouble();
                mc.player.onGround = false;

                if(mc.player.isInWater() || mc.player.isInLava()) mc.player.onGround = false;
            }
        } else if(mode.checkValString("Solid")) {
            if(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 1, mc.player.posZ)).getBlock() == Block.getBlockById(9)) mc.player.motionY = 0.18f;
            else if(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 0.0000001, mc.player.posZ)).getBlock() == Block.getBlockById(9)) {
                mc.player.fallDistance = 0.0f;
                mc.player.motionX = 0.0;
                mc.player.jumpMovementFactor = speedSolid.getValFloat();
                mc.player.motionY = 0;
            }
        } else if(mode.checkValString("Matrix 6.3")) {
            if (mc.player.isInWater() && (mc.player.collidedHorizontally || mc.gameSettings.keyBindJump.isPressed())) {
                mc.player.motionY = 0.09;
                return;
            }

            if (EntityUtil.isFluid(0.3)) mc.player.motionY = 0.1;
            else if (EntityUtil.isFluid(0.2)) {
                EntityUtil.resetTimer();
                mc.player.motionY = 0.2;
            } else if (EntityUtil.isFluid(0)) {
                EntityUtil.setTimer(0.8f);
                hClip(1.2);
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
            }
        } else if(mode.checkValString("MatrixPixel")) {
            if (EntityUtil.isFluid(-0.1)) {
                double[] motions = MovementUtil.strafe(speedPixel.getValDouble());

                mc.player.motionX = motions[0];
                mc.player.motionZ = motions[1];
            }
            if (EntityUtil.isFluid(0.0000001)) {
                mc.player.fallDistance = 0.0f;
                mc.player.motionX = 0.0;
                mc.player.motionZ = 0.0;
                mc.player.motionY = 0.06f;
                mc.player.jumpMovementFactor = 0.01f;
            }
        } else if(mode.checkValString("Entity") && mc.player.isRiding() && EntityUtil.isInLiquid(false)) {
            mc.player.motionY = 0.08500000238418583F;
            mc.player.ridingEntity.motionY = 0.08500000238418583F;
        }
    }

    private void hClip(double off) {
        double yaw = Math.toRadians(mc.player.rotationYaw);
        mc.player.setPosition(mc.player.posX + (-Math.sin(yaw) * off), mc.player.posY, mc.player.posZ + (Math.cos(yaw) * off));
    }

    @EventHandler private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {if((mode.checkValString("Matrix 6.3") || mode.checkValString("MatrixPixel")) && event.getPacket() instanceof CPacketPlayer && EntityUtil.isFluid(0.3)) ((CPacketPlayer) event.getPacket()).onGround = false;});
}
