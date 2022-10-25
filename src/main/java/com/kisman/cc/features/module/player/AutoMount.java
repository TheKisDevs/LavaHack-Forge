package com.kisman.cc.features.module.player;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.AngleUtil;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.*;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

public class AutoMount extends Module {

    private final Setting range = register(new Setting("Range", this, 5, 1, 6, false));
    private final Setting delayTicks = register(new Setting("DelayTicks", this, 1, 0, 100, true));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting onlyTamed = register(new Setting("OnlyTamed", this, false));
    private final Setting horses = register(new Setting("Horses", this, false));
    private final Setting skeletonHorses = register(new Setting("SkeletonHorses", this, false));
    private final Setting donkeys = register(new Setting("Donkeys", this, false));
    private final Setting llamas = register(new Setting("Llamas", this, false));
    private final Setting pigs = register(new Setting("Pigs", this, false));
    private final Setting boats = register(new Setting("Boats", this, false));

    public AutoMount(){
        super("AutoMount", Category.PLAYER);
    }

    private final TimerUtils timer = new TimerUtils();

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }
        timer.reset();
    }

    @Override
    public void update() {
        if(mc.player == null || mc.world == null)
            return;

        if(mc.player.isRiding())
            return;

        if(!timer.passedMillis(delayTicks.getValInt() * 50L))
            return;

        timer.reset();

        Entity entity = mc.world.loadedEntityList.stream()
                .filter(e -> mc.player.getDistance(e) <= range.getValDouble())
                .filter(this::isValidEntity)
                .filter(e -> !onlyTamed.getValBoolean() || isTame(e))
                .min(Comparator.comparing(e -> mc.player.getDistance(e)))
                .orElse(null);

        if(entity == null)
            return;

        Vec3d entityVec = new Vec3d(entity.posX, entity.posY + ((entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) / 2.0), entity.posZ);

        float[] oldRots = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
        if(rotate.getValBoolean()){
            float[] rots = AngleUtil.calculateAngle(BlockUtil.getEyesPos(), entityVec);
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], mc.player.onGround));
        }
        mc.playerController.interactWithEntity(mc.player, entity, EnumHand.MAIN_HAND);
        if(rotate.getValBoolean())
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(oldRots[0], oldRots[1], mc.player.onGround));
    }

    private boolean isValidEntity(Entity entity){
        if(entity instanceof EntityHorse && horses.getValBoolean())
            return !((EntityHorse) entity).isChild();
        if(entity instanceof EntitySkeletonHorse && skeletonHorses.getValBoolean())
            return !((EntitySkeletonHorse) entity).isChild();
        if(entity instanceof EntityDonkey && donkeys.getValBoolean())
            return !((EntityDonkey) entity).isChild();
        if(entity instanceof EntityLlama && llamas.getValBoolean())
            return !((EntityLlama) entity).isChild();
        if(entity instanceof EntityPig && pigs.getValBoolean())
            return ((EntityPig) entity).getSaddled();
        return entity instanceof EntityBoat && boats.getValBoolean();
    }

    private boolean isTame(Entity entity){
        if(entity instanceof AbstractHorse)
            return ((AbstractHorse) entity).isTame();
        return true;
    }
}
