package com.kisman.cc.features.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.features.module.misc.fakeplayer.EntityPoppable;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.TimerUtils;
import com.mojang.authlib.GameProfile;
import me.zero.alpine.listener.Listener;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MoverType;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;

import java.util.Random;
import java.util.UUID;

@ModuleInfo(
        name = "FakePlayer",
        desc = "fake player.",
        category = Category.MISC
)
public class FakePlayer extends Module {
    private final Setting name = register(new Setting("Name", this, "FinLicorice", "FinLicorice", true));
    private final Setting move = register(new Setting("Move", this, false));
    private final Setting damageable = register(new Setting("Damageable", this, false));
    private final Setting gapple = register(new Setting("Gapple", this, false));
    private final Setting gappleDelay = register(new Setting("Gapple Delay", this, 5000, 100, 10000, NumberType.TIME));

    private EntityPoppable player = null;

    private final TimerUtils timer = timer();

    public FakePlayer() {
        super.setDisplayInfo(() -> "[" + name.getValString() + "]");
    }

    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(receive);
        timer.reset();

        if(mc.player == null || mc.world == null) {
            toggle();
            return;
        }

        player = new EntityPoppable(mc.world, new GameProfile(UUID.fromString("dbc45ea7-e8bd-4a3e-8660-ac064ce58216"), name.getValString()));
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
            toggle();
            return;
        }

        if(gapple.getValBoolean() && timer.passedMillis(gappleDelay.getValInt())) {
            player.setAbsorptionAmount(16.0f);
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 1));
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6000, 0));
            player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000, 0));
            player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 3));
            timer.reset();
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
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(receive);

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

    private final Listener<PacketEvent.Receive> receive = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketExplosion && damageable.getValBoolean()) {
            SPacketExplosion packet = (SPacketExplosion) event.getPacket();

            double x = packet.getX();
            double y = packet.getY();
            double z = packet.getZ();
            double distance = player.getDistance(x, y, z) / 12.0;

            if (distance > 1.0) return;

            float size = packet.getStrength();
            double density = mc.world.getBlockDensity(new Vec3d(x, y, z), player.getEntityBoundingBox());
            double densityDistance = distance = (1.0 - distance) * density;
            float damage = (float) ((densityDistance * densityDistance + distance) / 2.0 * 7.0 * size * 2.0f + 1.0);
            float limbSwing = player.limbSwingAmount;

            DamageSource damageSource = DamageSource.causeExplosionDamage(new Explosion(mc.world, mc.player, x, y, z, size, false, true));

            player.attackEntityFrom(damageSource, damage);
            player.limbSwingAmount = limbSwing;
        }
    });
}
