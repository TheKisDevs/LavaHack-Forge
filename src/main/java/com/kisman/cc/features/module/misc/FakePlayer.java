package com.kisman.cc.features.module.misc;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MoverType;
import net.minecraft.world.GameType;

import java.util.Random;
import java.util.UUID;

public class FakePlayer extends Module {
    private final Setting name = register(new Setting("Name", this, "FinLicorice", "FinLicorice", true));
    private final Setting move = register(new Setting("Move", this, false));

    private EntityOtherPlayerMP player = null;

    public FakePlayer() {
        super("FakePlayer", "fake player.", Category.MISC);
        super.setDisplayInfo(() -> "[" + name.getValString() + "]");
    }

    public void onEnable() {
        if(mc.player == null || mc.world == null) {
            toggle();
            return;
        }

        player = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("dbc45ea7-e8bd-4a3e-8660-ac064ce58216"), name.getValString()));
        player.copyLocationAndAnglesFrom(mc.player);
        player.rotationYawHead = mc.player.rotationYawHead;
        player.rotationYaw = mc.player.rotationYaw;
        player.rotationPitch = mc.player.rotationPitch;
        player.setGameType(GameType.SURVIVAL);
        player.setHealth(20);
        mc.world.addEntityToWorld(-1337, player);
        player.onLivingUpdate();
    }

    public void update() {
        if(mc.player == null || mc.world == null || player == null) {
            super.setToggled(false);
            return;
        }

        if(move.getValBoolean()) {
            try {
                player.moveForward = mc.player.moveForward + (new Random().nextInt(5) / 10F);
                player.moveStrafing = mc.player.moveStrafing + (new Random().nextInt(5) / 10F);

                travel(player.moveStrafing, player.moveVertical, player.moveForward);
            } catch(Exception ignored) { }
        }
    }

    public void onDisable() {
        if(mc.world == null || mc.player == null || player == null) return;
        mc.world.removeEntityFromWorld(-1337);
    }

    public void travel(float strafe, float vertical, float forward) {
        double posY = player.posY;
        float speedXZ = 0.8F;
        float friction = 0.02F;
        float striderModifier = (float) EnchantmentHelper.getDepthStriderModifier(player);

        if (striderModifier > 3.0F) striderModifier = 3.0F;
        if (!player.onGround) striderModifier *= 0.5F;

        if (striderModifier > 0.0F) {
            speedXZ += (0.54600006F - speedXZ) * striderModifier / 3.0F;
            friction += (player.getAIMoveSpeed() - friction) * striderModifier / 4.0F;
        }

        player.moveRelative(strafe, vertical, forward, friction);
        player.move(MoverType.SELF, player.motionX, player.motionY, player.motionZ);
        player.motionX *= speedXZ;
        player.motionY *= 0.800000011920929D;
        player.motionZ *= speedXZ;

        if (!player.hasNoGravity()) {
            player.motionY -= 0.02D;
        }

        if (player.collidedHorizontally && player.isOffsetPositionInLiquid(player.motionX, player.motionY + 0.6000000238418579D - player.posY + posY, player.motionZ)) {
            player.motionY = 0.30000001192092896D;
        }
    }
}
