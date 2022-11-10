package com.kisman.cc.features.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.util.RenderingRewritePattern;
import com.kisman.cc.util.AngleUtil;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.world.BlockUtil;
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
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Cubic
 * @since 5.11.2022
 */
public class AutoCrystalRewrite extends Module {

    private final SettingEnum<Safety> safety = new SettingEnum<>("Safety", this, Safety.None).register();
    private final Setting safetyBalance = register(new Setting("SafetyBalance", this, 2, 0, 10, false));

    //private final SettingEnum<SwapEnum2.Swap> swap = new SettingEnum<>("Switch", this, SwapEnum2.Swap.None).register();
    //private final Setting swapDelay = register(new Setting("SwapDelay", this, 0, 0, 10, false));
    //private final SettingEnum<SwapEnum2.Swap> antiWeakness = new SettingEnum<>("AntiWeakness", this, SwapEnum2.Swap.None);

    private final SettingEnum<TargetMode> targetMode = new SettingEnum<>("TargetMode", this, TargetMode.Closest).register();
    private final Setting targetRange = register(new Setting("TargetRange", this, 12, 1, 16, false));
    private final Setting popFocus = register(new Setting("PopFocus", this, false));
    private final Setting popFocusTimeOut = register(new Setting("PopFocusTimeOut", this, 30, 0, 120, true));

    private final Setting predict = register(new Setting("Predict", this, false));
    private final Setting predictTicks = register(new Setting("PredictTicks", this, 2, 0, 20, true));

    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting yawStep = register(new Setting("YawStep", this, 55, 1, 180, true));

    private final Setting swing = register(new Setting("Swing", this, true));
    private final SettingEnum<SwingHand> swingingHand = new SettingEnum<>("SwingingHand", this, SwingHand.MainHand).register();

    private final SettingEnum<Timings> timings = new SettingEnum<>("Timings", this, Timings.Adaptive).register();
    private final SettingEnum<Logic> logic = new SettingEnum<>("Logic", this, Logic.BreakPlace).register();

    private final Setting placeSpeed = register(new Setting("PlaceSpeed", this, 20, 0, 20, false));
    private final Setting packetPlace = register(new Setting("PacketPlace", this, true));
    private final Setting placeRaytrace = register(new Setting("Raytrace", this, false));
    private final Setting strictFacing = register(new Setting("StrictFacing", this, false));
    private final Setting antiStuck = register(new Setting("AntiStuck", this, false));
    private final Setting noPlaceSuicide = register(new Setting("NoPlaceSuicide", this, true));

    private final Setting breakSpeed = register(new Setting("BreakSpeed", this, 18.4, 0, 20, false));
    private final Setting inhibit = register(new Setting("Inhibit", this, false));
    private final Setting inhibitTimeOut = register(new Setting("InhibitTimeOut", this, 30, 1, 60, true));
    private final Setting packetBreak = register(new Setting("PacketBreak", this, true));
    private final Setting breakRaytrace = register(new Setting("BreakRaytrace", this, true));
    private final Setting noBreakSuicide = register(new Setting("NoBreakSuicide", this, true));

    private final SettingEnum<Sync> sync = new SettingEnum<>("Sync", this, Sync.Confirm).register();

    private final Setting placeRange = register(new Setting("PlaceRange", this, 5, 0, 6, false));
    private final Setting placeWallRange = register(new Setting("PlaceWallRange", this, 3, 0, 6, false));
    private final Setting breakRange = register(new Setting("BreakRange", this, 5, 0, 6, false));
    private final Setting breakWallRange = register(new Setting("BreakWallRange", this, 3, 0, 6, false));
    private final Setting firePlace = register(new Setting("FirePlace", this, false));
    private final Setting terrain = register(new Setting("Terrain", this, false));

    private final Setting minPlaceDamage = register(new Setting("MinPlaceDamage", this, 5, 0, 36, false));
    private final Setting maxSelfPlace = register(new Setting("MaxSelfPlace", this, 8, 0, 36, false));
    private final Setting minBreakDamage = register(new Setting("MinBreakDamage", this, 5, 0, 36, false));
    private final Setting maxSelfBreak = register(new Setting("MaxSelfBreak", this, 8, 0, 36, false));

    private final RenderingRewritePattern renderer = new RenderingRewritePattern(this).preInit().init();

    private static AutoCrystalRewrite INSTANCE;

    public AutoCrystalRewrite(){
        super("Kys+", Category.COMBAT, true);
        INSTANCE = this;
    }

    private Thread thread = null;

    private TimerUtils placeTimer = new TimerUtils();

    private TimerUtils breakTimer = new TimerUtils();

    private final TimerUtils popFocusTimer = new TimerUtils();

    private EntityPlayer target = null;

    private Map<EntityEnderCrystal, Long> inhibitCrystals = new ConcurrentHashMap<>();

    private BlockPos lastPlacePos = null;

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null){
            setToggled(false);
            return;
        }

        placeTimer.reset();
        breakTimer.reset();
        popFocusTimer.reset();

        updateTarget();

        doAutoCrystal();

        Kisman.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        Kisman.EVENT_BUS.unsubscribe(this);

        thread.interrupt();
        thread = null;
        placeTimer.reset();
        breakTimer.reset();
        popFocusTimer.reset();
        target = null;
        inhibitCrystals.clear();
        lastPlacePos = null;
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null){
            setToggled(false);
            return;
        }
        mc.addScheduledTask(() -> {
            long timeOut = inhibitTimeOut.getValInt() * 50L;
            inhibitCrystals.forEach((crystal, time) -> {
                if((System.currentTimeMillis() - time) >= timeOut)
                    inhibitCrystals.remove(crystal);
            });
        });
        mc.addScheduledTask(() -> {
            if(popFocus.getValBoolean() && !popFocusTimer.passedMillis(popFocusTimeOut.getValInt() * 50L))
                return;
            updateTarget();
        });
    }

    private void doAutoCrystal(){
        AtomicBoolean started = new AtomicBoolean(true);
        thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()){
                if(getCrystalHand() == null){
                    started.set(true);
                    continue;
                }
                try {
                    handleLogic(logic.getValEnum(), started.get());
                    started.set(false);
                } catch (InterruptedException e){
                    setToggled(false);
                    return;
                }
            }
        });
        thread.start();
    }

    private void handleLogic(Logic logic, boolean justDoIt) throws InterruptedException {
        if(logic == Logic.PlaceBreak){
            if(timings.getValEnum() == Timings.Sequential){
                if (!justDoIt)
                    Thread.sleep(Math.round(1000L - (50L * placeSpeed.getValDouble())));
                handlePlace(false);
                Thread.sleep(Math.round(1000L - (50L * breakSpeed.getValDouble())));
                handleBreak(false);
                return;
            }
            handlePlace(!justDoIt);
            handleBreak(true);
            return;
        }
        if(timings.getValEnum() == Timings.Sequential){
            if(!justDoIt)
                Thread.sleep(Math.round(1000L - (50L * breakSpeed.getValDouble())));
            handleBreak(false);
            Thread.sleep(Math.round(1000L - (50L * placeSpeed.getValDouble())));
            handlePlace(false);
            return;
        }
        handleBreak(true);
        handlePlace(true);
    }

    private void handleBreak(boolean handleDelay){
        if(handleDelay && !breakTimer.passedMillis(Math.round(1000L - (50L * breakSpeed.getValDouble()))))
            return;
        CrystalInfo info = getOptimalBreak();
        if(info == null)
            return;
        attackCrystal(info.getCrystal());
        inhibitCrystals.put(info.getCrystal(), System.currentTimeMillis());
        if(sync.getValEnum() == Sync.Attack)
            removeCrystal(info.getCrystal());
        if(handleDelay)
            breakTimer.reset();
    }

    private void handlePlace(boolean handleDelay){
        if(handleDelay && !placeTimer.passedMillis(Math.round(1000L - (50L * placeSpeed.getValDouble()))))
            return;
        PositionInfo info = calculatePlace();
        if(info == null){
            lastPlacePos = null;
            return;
        }
        lastPlacePos = info.getBlockPos();
        placeCrystal(info.getBlockPos());
        if(handleDelay)
            placeTimer.reset();
    }

    private void placeCrystal(BlockPos pos){
        RayTraceResult result = mc.world.rayTraceBlocks(BlockUtil.getEyesPos(), new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
        EnumFacing facing = result == null ? (strictFacing.getValBoolean() ? EnumFacing.UP : EnumFacing.DOWN) : (placeRaytrace.getValBoolean() ? result.sideHit : (strictFacing.getValBoolean() ? EnumFacing.UP : EnumFacing.DOWN));
        float[] oldRots = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
        float[] rots = AngleUtil.calculateAngles(pos);
        if(rotate.getValBoolean())
            handleRotate(rots, oldRots);
        if(packetPlace.getValBoolean())
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, getCrystalHand(), 0, 0, 0));
        else
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, facing, new Vec3d(0, 0, 0), getCrystalHand());
        if(rotate.getValBoolean())
            handleRotate(oldRots, rots);
    }

    public EnumHand getCrystalHand(){
        if(mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL){
            mc.playerController.syncCurrentPlayItem();
            return EnumHand.MAIN_HAND;
        }
        if(mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL){
            mc.playerController.syncCurrentPlayItem();
            return EnumHand.OFF_HAND;
        }
        return null;
    }

    private void attackCrystal(EntityEnderCrystal crystal){
        float[] oldRots = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
        float[] rots = AngleUtil.calculateAngles(crystal);
        if(rotate.getValBoolean())
            handleRotate(rots, oldRots);
        if(packetBreak.getValBoolean())
            mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        else
            mc.playerController.attackEntity(mc.player, crystal);
        swing();
        if(rotate.getValBoolean())
            handleRotate(oldRots, rots);
    }

    private void swing(){
        int armSwingAnimationEnd;
        if(mc.player.isPotionActive(MobEffects.HASTE))
            armSwingAnimationEnd = 6 - (1 + mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier());
        else
            armSwingAnimationEnd = mc.player.isPotionActive(MobEffects.MINING_FATIGUE) ? 6 + (1 + mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;

        if(swing.getValBoolean() && (!mc.player.isSwingInProgress || mc.player.swingProgressInt >= armSwingAnimationEnd / 2 || mc.player.swingProgressInt < 0)){
            mc.player.swingProgressInt = -1;
            mc.player.isSwingInProgress = true;
            mc.player.swingingHand = swingingHand.getValEnum().getHand();

            if(mc.player.world instanceof WorldServer)
                ((WorldServer) mc.player.world).getEntityTracker().sendToTracking(mc.player, new SPacketAnimation(mc.player, swingingHand.getValEnum().getHand() == EnumHand.OFF_HAND ? 3 : 0));
        }

        mc.player.connection.sendPacket(new CPacketAnimation(swingingHand.getValEnum().getHand()));
    }

    // let's pray this actually works
    private void handleRotate(float[] rots, float[] oldRots){
        if(yawStep.getValInt() >= 360){
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], mc.player.onGround));
            mc.player.lastReportedYaw = rots[0];
            mc.player.lastReportedPitch = rots[1];
            return;
        }

        float yD1 = MathHelper.wrapDegrees(oldRots[0] - rots[0]);
        float yD2 = MathHelper.wrapDegrees(rots[0] - oldRots[0]);

        if(yD1 < yD2){
            float step = Math.abs(yD1) / yawStep.getValFloat();
            float total = 0;
            for(int i = 0;;i++){
                if(total < Math.abs(yD1))
                    break;
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0] - (i * step), rots[1], mc.player.onGround));
                total += step;
            }
            mc.player.lastReportedYaw = rots[0];
            mc.player.lastReportedPitch = rots[1];
            return;
        }

        float step = Math.abs(yD2) / yawStep.getValFloat();
        float total = 0;
        for(int i = 0;;i++){
            if(total < Math.abs(yD2))
                break;
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0] + (i * step), rots[1], mc.player.onGround));
            total += step;
        }
        mc.player.lastReportedYaw = rots[0];
        mc.player.lastReportedPitch = rots[1];
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> packetListener = new Listener<>(event -> {

        if(event.getPacket() instanceof SPacketSoundEffect){
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if(packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE){
                Set<EntityEnderCrystal> remove = new HashSet<>();
                inhibitCrystals.forEach((crystal, time) -> {
                    if(crystal.getDistance(packet.getX(), packet.getY(), packet.getZ()) >= 6)
                        return;
                    remove.add(crystal);
                    if(sync.getValEnum() == Sync.Confirm)
                        removeCrystal(crystal);
                });
                for(EntityEnderCrystal crystal : remove)
                    inhibitCrystals.remove(crystal);
            }
        }

        if(event.getPacket() instanceof SPacketExplosion){
            SPacketExplosion packet = (SPacketExplosion) event.getPacket();
            for(
                    EntityEnderCrystal crystal :
                    mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(new BlockPos(packet.getX(), packet.getY(), packet.getZ())))
            ){
                inhibitCrystals.remove(crystal);
                if(sync.getValEnum() == Sync.Confirm)
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
                inhibitCrystals.remove(crystal);
                if(sync.getValEnum() == Sync.Confirm)
                    removeCrystal(crystal);
            }
        }

        if(event.getPacket() instanceof SPacketEntityStatus){
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if(packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer && popFocus.getValBoolean()) {
                if (mc.player.getDistance(packet.getEntity(mc.world)) <= targetRange.getValDouble()) {
                    target = (EntityPlayer) packet.getEntity(mc.world);
                    popFocusTimer.reset();
                }
            }
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

            if(breakRaytrace.getValBoolean() && !mc.player.canEntityBeSeen(crystal))
                continue;

            if(inhibit.getValBoolean() && inhibitCrystals.containsKey(crystal))
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

            if(noBreakSuicide.getValBoolean() && selfDamage >= (mc.player.getHealth() + mc.player.getAbsorptionAmount()))
                continue;

            damage = getSafetyDamage(damage, selfDamage);

            if(damage < minBreakDamage.getValDouble())
                continue;

            if(selfDamage > maxSelfBreak.getValDouble())
                continue;

            CrystalInfo info = new CrystalInfo(crystal, damage, selfDamage);

            crystalInfo = crystalInfo.max(info);
        }

        return crystalInfo.getTargetDamage() < 0 ? null : crystalInfo;
    }

    private PositionInfo calculatePlace(){

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



            if(placeRaytrace.getValBoolean() && !EntityUtil.canSee(pos))
                continue;

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

            if(antiStuck.getValBoolean()){
                AxisAlignedBB aabb = new AxisAlignedBB(
                        pos.getX() -1,
                        pos.getY() - 0.5,
                        pos.getZ() - 1,
                        pos.getX() + 2,
                        pos.getY() + 1,
                        pos.getZ() + 2
                );

                if(!mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, aabb).isEmpty())
                    continue;
            }

            if(noPlaceSuicide.getValBoolean() && selfDamage >= (mc.player.getHealth() - mc.player.getAbsorptionAmount()))
                continue;

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

        for(EntityPlayer player : mc.world.playerEntities){
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
        return mc.player.getDistance(player) > range
                || player.equals(mc.player)
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

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.player == null || mc.world == null)
            return;

        if(!isToggled())
            return;

        if(lastPlacePos == null)
            return;

        renderer.draw(lastPlacePos);
    }

    private enum Timings {
        Adaptive,
        Sequential
    }

    private enum Logic {
        PlaceBreak,
        BreakPlace
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

    private enum Sync {
        Attack,
        Confirm
    }

    private enum SwingHand {
        MainHand(EnumHand.MAIN_HAND),
        OffHand(EnumHand.OFF_HAND),
        CurrentHand(null);

        private final EnumHand hand;

        SwingHand(EnumHand hand) {
            this.hand = hand;
        }

        public EnumHand getHand() {
            return hand == null ? INSTANCE.getCrystalHand() : hand;
        }
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
