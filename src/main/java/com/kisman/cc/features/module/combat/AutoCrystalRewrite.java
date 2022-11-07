package com.kisman.cc.features.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.enums.dynamic.SwapEnum2;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.world.CrystalUtils;
import com.mojang.authlib.GameProfile;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutoCrystalRewrite extends Module {

    private final SettingEnum<Safety> safety = new SettingEnum<>("Safety", this, Safety.None).register();
    private final Setting safetyBalance = register(new Setting("SafetyBalance", this, 2, 0, 10, false));

    private final SettingEnum<SwapEnum2.Swap> swap = new SettingEnum<>("Switch", this, SwapEnum2.Swap.None).register();
    private final Setting swapDelay = register(new Setting("SwapDelay", this, 0, 0, 10, false));
    private final SettingEnum<SwapEnum2.Swap> antiWeakness = new SettingEnum<>("AntiWeakness", this, SwapEnum2.Swap.None);

    private final SettingEnum<TargetMode> targetMode = new SettingEnum<>("TargetMode", this, TargetMode.Closest).register();
    private final Setting targetRange = register(new Setting("TargetRange", this, 12, 1, 16, false));
    private final Setting popFocus = register(new Setting("PopFocus", this, false));

    private final Setting predict = register(new Setting("Predict", this, false));
    private final Setting predictTicks = register(new Setting("PredictTicks", this, 2, 0, 20, true));

    private final Setting placeRange = register(new Setting("PlaceRange", this, 5, 0, 6, false));
    private final Setting placeWallRange = register(new Setting("PlaceWallRange", this, 3, 0, 6, false));
    private final Setting breakRange = register(new Setting("BreakRange", this, 5, 0, 6, false));
    private final Setting breakWallRange = register(new Setting("BreakWallRange", this, 3, 0, 6, false));
    private final Setting firePlace = register(new Setting("FirePlace", this, false));
    private final Setting terrain = register(new Setting("Terrain", this, false));

    private final Setting minPlaceDamage = register(new Setting("MinPlaceDamage", this, 5, 0, 36, false));
    private final Setting maxSelfPlace = register(new Setting("MaxSelfPlace", this, 8, 0, 36, false));

    public AutoCrystalRewrite(){
        super("Kys+", Category.COMBAT);
    }

    private EntityPlayer target;

    private Set<EntityEnderCrystal> placedCrystals = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }

        Kisman.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        Kisman.EVENT_BUS.unsubscribe(this);

        placedCrystals.clear();
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> packetListener = new Listener<>(event -> {

        if(event.getPacket() instanceof SPacketSoundEffect){
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if(packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE){
                Set<EntityEnderCrystal> remove = new HashSet<>();
                placedCrystals.forEach(crystal -> {
                    if(crystal.getDistance(packet.getX(), packet.getY(), packet.getZ()) >= 6)
                        return;
                    remove.add(crystal);
                    removeCrystal(crystal);
                });
                placedCrystals.removeAll(remove);
            }
        }

        if(event.getPacket() instanceof SPacketExplosion){
            SPacketExplosion packet = (SPacketExplosion) event.getPacket();
            for(
                    EntityEnderCrystal crystal :
                    mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(new BlockPos(packet.getX(), packet.getY(), packet.getZ())))
            ){
                placedCrystals.remove(crystal);
                removeCrystal(crystal);
            }
        }

        if(event.getPacket() instanceof SPacketDestroyEntities){
            SPacketDestroyEntities packet = (SPacketDestroyEntities) event.getPacket();
            for(int entityId : packet.getEntityIDs()){
                Entity entity = mc.world.getEntityByID(entityId);
                if(!(entity instanceof EntityEnderCrystal))
                    return;
                EntityEnderCrystal crystal = (EntityEnderCrystal) entity;
                removeCrystal(crystal);
            }
        }

        if(event.getPacket() instanceof SPacketEntityStatus){
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if(packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer && popFocus.getValBoolean())
                target = (EntityPlayer) packet.getEntity(mc.world);
        }
    });

    private void removeCrystal(EntityEnderCrystal crystal){
        crystal.setDead();
        mc.addScheduledTask(() -> {
            mc.world.removeEntity(crystal);
            mc.world.removeEntityDangerously(crystal);
        });
    }

    private void updateTarget(){
        target = getTarget();
        if(predict.getValBoolean())
            target = predict(target, predictTicks.getValInt());
    }

    private CrystalInfo getOptimalBreak(){
        if(target == null)
            return null;

        CrystalInfo crystalInfo = new CrystalInfo(null, -1 , -1);

        for(EntityEnderCrystal crystal : mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, playerRelativeAABB(breakRange.getValDouble()))){

            if(!isEntityInRange(crystal, breakRange.getValDouble(), breakWallRange.getValDouble()))
                continue;

            double damage = CrystalUtils.calculateDamage(
                    mc.world,
                    crystal.posX,
                    crystal.posY,
                    crystal.posZ,
                    target,
                    terrain.getValBoolean()
            );

            double selfDamage = CrystalUtils.calculateDamage(
                    mc.world,
                    crystal.posX,
                    crystal.posY,
                    crystal.posZ,
                    mc.player,
                    terrain.getValBoolean()
            );

            damage = getSafetyDamage(damage, selfDamage);

            CrystalInfo info = new CrystalInfo(crystal, damage, selfDamage);

            crystalInfo = crystalInfo.max(info);
        }

        return crystalInfo.getTargetDamage() < 0 ? null : crystalInfo;
    }

    private PositionInfo calculatePlace(){
        if(target == null)
            return null;

        PositionInfo positionInfo = new PositionInfo(BlockPos.ORIGIN, -1, -1);

        for(BlockPos pos : CrystalUtils.getSphere(placeRange.getValFloat(), true, false)){

            if(
                    !isPosInRange(pos, placeRange.getValDouble(), placeWallRange.getValDouble())
                    || !CrystalUtils.canPlaceCrystal(
                            pos,
                            true,
                            true,
                            false,
                            firePlace.getValBoolean()
                    )
            ){
                continue;
            }

            double damage = CrystalUtils.calculateDamage(
                    mc.world,
                    pos.getX() + 0.5,
                    pos.getY() + 1,
                    pos.getZ() + 0.5,
                    target,
                    terrain.getValBoolean()
            );

            double selfDamage = CrystalUtils.calculateDamage(
                    mc.world,
                    pos.getX() + 0.5,
                    pos.getY() + 1,
                    pos.getZ() + 0.5,
                    mc.player,
                    terrain.getValBoolean()
            );

            damage = getSafetyDamage(damage, selfDamage);

            if(damage < minPlaceDamage.getValDouble())
                continue;

            if(selfDamage > maxSelfPlace.getValDouble())
                continue;

            PositionInfo place = new PositionInfo(pos, damage, selfDamage);

            positionInfo = positionInfo.max(place);
        }

        return positionInfo.targetDamage < 0 ? null : positionInfo;
    }

    private double getSafetyDamage(double damage, double selfDamage){
        switch(safety.getValEnum()){
            case None:
                return damage;
            case MinMax:
                return damage - selfDamage;
            case Balance:
                return (damage + (safetyBalance.getValDouble() * 0.5)) - (selfDamage + safetyBalance.getValDouble());
        }
        return damage;
    }

    private EntityPlayer getTarget(){
        EntityPlayer currentTarget = null;
        double minHealth = 36;
        double maxDamage = 0.5;
        double minDistance = targetRange.getValDouble() + 1;

        for(EntityPlayer player : mc.world.getEntitiesWithinAABB(EntityPlayer.class, playerRelativeAABB(targetRange.getValDouble()))){
            if(isTargetNotValid(player, targetRange.getValDouble()))
                continue;

            double health = player.getHealth() + player.getAbsorptionAmount();
            double damage = getDamageForPlayer(player);
            double distance = mc.player.getDistance(player);

            if(
                    targetMode.getValEnum() == TargetMode.Closest
                    && distance < minDistance
            ){
                currentTarget = player;
                minDistance = distance;
                continue;
            }


            if(
                    targetMode.getValEnum() == TargetMode.Health
                    && health < minHealth
            ){
                currentTarget = player;
                minDistance = health;
                continue;
            }

            if(
                    targetMode.getValEnum() == TargetMode.Damage
                    && damage > maxDamage
            ){
                currentTarget = player;
                maxDamage = damage;
            }
        }

        return currentTarget;
    }

    private AxisAlignedBB playerRelativeAABB(double range){
        return new AxisAlignedBB(
                mc.player.posX - range,
                mc.player.posY - range,
                mc.player.posZ - range,
                mc.player.posX + range,
                mc.player.posY + range,
                mc.player.posZ + range
        );
    }

    private boolean isTargetNotValid(EntityPlayer player, double range){
        return mc.player.getDistanceSq(player) > range
                ||  player.equals(mc.player)
                || player.getHealth() <= 0.0f
                || player.isDead
                || FriendManager.instance.isFriend(player.getName());
    }

    private float getDamageForPlayer(EntityPlayer player){
        float maxDamage = 0.5f;
        for(BlockPos pos : CrystalUtils.getSphere(placeRange.getValFloat(), true, false)){
            if(
                    isPosInRange(pos, placeRange.getValDouble(), placeWallRange.getValDouble())
                    && CrystalUtils.canPlaceCrystal(
                            pos,
                            true,
                            true,
                            false,
                            firePlace.getValBoolean()
                    )
            ){
                maxDamage = Math.max(
                        maxDamage,
                        CrystalUtils.calculateDamage(
                                mc.world,
                                pos.getX() + 0.5,
                                pos.getY() + 1,
                                pos.getZ() + 0.5,
                                player,
                                terrain.getValBoolean()
                        )
                );
            }
        }
        return maxDamage;
    }

    private boolean isPosInRange(BlockPos pos, double range, double wallRange){
        return mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= (EntityUtil.canSee(pos) ? range : wallRange);
    }

    private boolean isEntityInRange(Entity entity, double range, double wallRange){
        return mc.player.getDistance(entity) <= (mc.player.canEntityBeSeen(entity) ? range : wallRange);
    }

    private static EntityPlayer predict(EntityPlayer entity, int ticks){
        AxisAlignedBB aabb = entity.getEntityBoundingBox();
        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;
        double mX = entity.motionX;
        double mY = entity.motionY;
        double mZ = entity.motionZ;
        boolean doVertical = true;
        boolean doHorizontal = true;
        for(int i = 0; i < ticks; i++){
            if(doHorizontal){
                x += mX;
                z += mZ;
            }
            if(doVertical)
                y += mY;
            aabb.offset(mX, mY, mZ);
            if(checkHorizontalCollision(aabb, new Vec3d(x, y, z), mX, mY, mZ)){
                x -= mX;
                y -= mY;
                z -= mZ;
                aabb.offset(-mX, -mY, -mZ);
                doHorizontal = false;
                continue;
            }
            if(checkVerticalCollision(aabb, new Vec3d(x, y, z), mX, mY, mZ)){
                x -= mX;
                y -= mY;
                z -= mZ;
                aabb.offset(-mX, -mY, -mZ);
                doVertical = false;
                continue;
            }
            if(doHorizontal){
                mX += 0.8;
                mZ *= 0.8;
            }
            if(doVertical)
                mY *= mY < 0.0 ? 1.15 : 0.7;
        }
        EntityOtherPlayerMP player = new EntityOtherPlayerMP(mc.world, new GameProfile(entity.getUniqueID(), entity.getName()));
        player.setPosition(x, y, z);
        player.inventory.copyInventory(entity.inventory);
        player.setHealth(entity.getHealth());
        player.prevPosX = entity.prevPosX;
        player.prevPosY = entity.prevPosY;
        player.prevPosZ = entity.prevPosZ;
        for(PotionEffect effect : entity.getActivePotionEffects()) {
            player.addPotionEffect(effect);
        }
        return player;
    }

    private static boolean checkVerticalCollision(AxisAlignedBB aabb, Vec3d vec3d, double mX, double mY, double mZ){
        boolean result;
        result = wouldCollide(mc.world, new Vec3d(aabb.minX, aabb.maxY, aabb.minZ), vec3d);
        result |= wouldCollide(mc.world, new Vec3d(aabb.minX, aabb.maxY, aabb.maxZ), vec3d);
        result |= wouldCollide(mc.world, new Vec3d(aabb.maxX, aabb.maxY, aabb.minZ), vec3d);
        result |= wouldCollide(mc.world, new Vec3d(aabb.maxX, aabb.maxY, aabb.maxZ), vec3d);
        result &= (mY * mY) > (mX * mX + mZ * mZ);
        return result;
    }

    private static boolean checkHorizontalCollision(AxisAlignedBB aabb, Vec3d vec3d, double mX, double mY, double mZ){
        boolean result;
        result = wouldCollide(mc.world, new Vec3d(aabb.minX, aabb.minY, aabb.minZ), vec3d);
        result |= wouldCollide(mc.world, new Vec3d(aabb.minX, aabb.minY, aabb.maxZ), vec3d);
        result |= wouldCollide(mc.world, new Vec3d(aabb.maxX, aabb.minY, aabb.minZ), vec3d);
        result |= wouldCollide(mc.world, new Vec3d(aabb.maxX, aabb.minY, aabb.maxZ), vec3d);
        result |= wouldCollide(mc.world, new Vec3d(aabb.minX, aabb.maxY, aabb.minZ), vec3d);
        result |= wouldCollide(mc.world, new Vec3d(aabb.minX, aabb.maxY, aabb.maxZ), vec3d);
        result |= wouldCollide(mc.world, new Vec3d(aabb.maxX, aabb.maxY, aabb.minZ), vec3d);
        result |= wouldCollide(mc.world, new Vec3d(aabb.maxX, aabb.maxY, aabb.maxZ), vec3d);
        result &= (mX * mX + mZ  * mZ) > (mY * mY);
        return result;
    }

    private static boolean wouldCollide(WorldClient world, Vec3d vec3d1, Vec3d vec3d2){
        RayTraceResult result = world.rayTraceBlocks(vec3d1, vec3d2, false, true, false);
        if(result == null)
            return true;
        return world.getBlockState(result.getBlockPos()).getBlock() != Blocks.AIR;
    }

    private enum Safety {
        None,
        MinMax,
        Balance
    }

    private enum TargetMode {
        Closest,
        Health,
        Damage
    }

    private static class PositionInfo {

        private final BlockPos blockPos;

        private final double targetDamage;

        private final double selfDamage;

        public PositionInfo(BlockPos blockPos, double targetDamage, double selfDamage) {
            this.blockPos = blockPos;
            this.targetDamage = targetDamage;
            this.selfDamage = selfDamage;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public double getTargetDamage() {
            return targetDamage;
        }

        public double getSelfDamage() {
            return selfDamage;
        }

        public PositionInfo max(PositionInfo other){
            return other.targetDamage > targetDamage ? other : this;
        }
    }

    private static class CrystalInfo {

        private final EntityEnderCrystal crystal;

        private final double targetDamage;

        private final double selfDamage;

        public CrystalInfo(EntityEnderCrystal crystal, double targetDamage, double selfDamage) {
            this.crystal = crystal;
            this.targetDamage = targetDamage;
            this.selfDamage = selfDamage;
        }

        public EntityEnderCrystal getCrystal() {
            return crystal;
        }

        public double getTargetDamage() {
            return targetDamage;
        }

        public double getSelfDamage() {
            return selfDamage;
        }

        public CrystalInfo max(CrystalInfo other){
            return other.targetDamage > targetDamage ? other : this;
        }
    }
}
