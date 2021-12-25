package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import com.kisman.cc.mixin.mixins.accessor.*;
import com.kisman.cc.module.*;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.kisman.cc.util.Rotation;
import com.kisman.cc.util.manager.RotationManager;
import i.gishreloaded.gishcode.utils.TimerUtils;
import me.zero.alpine.listener.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class AutoCrystalRewrite extends Module {
    public static AutoCrystalRewrite instance;

    private Setting placeRange = new Setting("PlaceRange", this, 0, 4.5f, 6, false);
    private Setting breakRange = new Setting("BreakRange", this, 0, 4.5f, 6, false);
    private Setting wallPlaceRange = new Setting("WallPlaceRange", this, 0, 4, 6, false);
    private Setting wallBreakRange = new Setting("WallBreakRange", this, 0, 4, 6, false);
    private Setting targetRange = new Setting("TargetRange", this, 0, 30, 50, false);
    private Setting rayTrace = new Setting("RayTrace", this, RayTrace.None);
    private Setting swing = new Setting("Swing", this, SwingMode.Mainhand);
    private Setting packetSwing = new Setting("PacketSwing", this, false);
    private Setting instant = new Setting("Instant", this, true);
    private Setting inhibit = new Setting("Inhibit", this, true);

    private Setting delayLine = new Setting("DelayLine", this, "Delay");
    private Setting placeDelay = new Setting("PlaceDelay", this, 20, 0, 200, true);
    private Setting breakDelay = new Setting("BreakDelay", this, 40, 0, 200, true);
    private Setting clearDelay = new Setting("ClearDelay", this, 10, 1, 60, true);

    private Setting placeLine = new Setting("PlaceLine", this, "Place");
    private Setting place = new Setting("Place", this, true);
    private Setting newVersionPlace = new Setting("1.13", this, false);
    private Setting multiPlace = new Setting("MultiPlace", this, false);
    private Setting terrainIgnore = new Setting("Terrain Ignore", this, true);
    private Setting syns = new Setting("Syns", this, true);

    private Setting breakLine = new Setting("BreakLine", this, "Break");
    private Setting _break = new Setting("Break", this, true);
    private Setting breakMode = new Setting("BreakMode", this, BreakMode.Always);
    private Setting breakCalc = new Setting("BreakCalc", this, true);

    private Setting rotateLine = new Setting("RotateLine", this, "Rotate");
    private Setting rotate = new Setting("Rotate", this, Rotations.None);
    private Setting clientSide = new Setting("ClientSize", this, false);
    private Setting randomize = new Setting("Ramdomize", this, true);
    private Setting yawStep = new Setting("YawStep", this, YawStep.None);
    private Setting steps = new Setting("Steps", this, 0.3f, 0, 1, false);

    private Setting damage = new Setting("DamageLine", this, "Damage");
    private Setting minDMG = new Setting("MinDMG", this, 6, 0.1f, 36, false);
    private Setting maxSelfDMG = new Setting("MaxSelfDMG", this, 12, 0.1f, 36, false);
    private Setting facePlaceHP = new Setting("FacePlaceDMG", this, 0, 0, 37, true);
    private Setting armor = new Setting("Armor Scale", this, 12, 0, 100, true);

    private Setting switchLine = new Setting("SwitchLine", this, "Switch");
    private Setting switchMode = new Setting("Switch", this, SwapMode.None);
    private Setting antiWeaknessSwitchMode = new Setting("AntiWeaknessSwitch", this, SwapMode.None);

    private Setting pauseLine = new Setting("PauseLine", this, "Pause");
    private Setting pauseIfEating = new Setting("Pause If Eating", this, false);
    private Setting pauseIfHealth = new Setting("Pause If Heaith", this, false);
    private Setting requirestHealth = new Setting("Requirest Health", this, 5, 0, 37, true);

    private Setting renderLine = new Setting("RenderLine", this, "Render");
    private Setting render = new Setting("Render", this, true);


    private  Random random = new Random();
    private Set<BlockPos> placedCrystals = new HashSet<>();
    private EntityPlayer target;
    private Entity lastHitEntity;
    private BlockPos current;
    private Rotation rotation;

    private final TimerUtils breakTimer = new TimerUtils();
    private final TimerUtils placeTimer = new TimerUtils();
    private final TimerUtils clearTimer = new TimerUtils();

    public AutoCrystalRewrite() {
        super("AutoCrystalRewrite", Category.COMBAT);

        instance = this;

        setmgr.rSetting(placeRange);
        setmgr.rSetting(breakRange);
        setmgr.rSetting(wallPlaceRange);
        setmgr.rSetting(wallBreakRange);
        setmgr.rSetting(targetRange);
        setmgr.rSetting(rayTrace);
        setmgr.rSetting(packetSwing);
        setmgr.rSetting(instant);
        setmgr.rSetting(inhibit);

        setmgr.rSetting(delayLine);
        setmgr.rSetting(placeDelay);
        setmgr.rSetting(breakDelay);
        setmgr.rSetting(clearDelay);

        setmgr.rSetting(placeLine);
        setmgr.rSetting(place);
        setmgr.rSetting(newVersionPlace);
        setmgr.rSetting(multiPlace);
        setmgr.rSetting(terrainIgnore);
        setmgr.rSetting(syns);

        setmgr.rSetting(breakLine);
        setmgr.rSetting(_break);
        setmgr.rSetting(breakMode);
        setmgr.rSetting(breakCalc);

        setmgr.rSetting(rotateLine);
        setmgr.rSetting(rotate);
        setmgr.rSetting(clientSide);
        setmgr.rSetting(randomize);
        setmgr.rSetting(yawStep);
        setmgr.rSetting(steps);

        setmgr.rSetting(damage);
        setmgr.rSetting(minDMG);
        setmgr.rSetting(maxSelfDMG);
        setmgr.rSetting(facePlaceHP);
        setmgr.rSetting(armor);

        setmgr.rSetting(switchLine);
        setmgr.rSetting(switchMode);
        setmgr.rSetting(antiWeaknessSwitchMode);

        setmgr.rSetting(pauseLine);
        setmgr.rSetting(pauseIfEating);
        setmgr.rSetting(pauseIfHealth);
        setmgr.rSetting(requirestHealth);

        setmgr.rSetting(renderLine);
        setmgr.rSetting(render);
    }

    public void onEnable() {
        current = null;
        rotation = null;
        breakTimer.reset();
        placeTimer.reset();

        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(listener2);
    }

    public void onDisable() {
        current = null;
        rotation = null;
        breakTimer.reset();
        placeTimer.reset();

        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
        Kisman.EVENT_BUS.unsubscribe(listener2);
    }

    public void update() {
        if (mc.player == null && mc.world == null) return;

        if (clearTimer.passedMillis(clearDelay.getValInt() * 1000L)) {
            placedCrystals.clear();
            clearTimer.reset();
        }

        if(needPause()) return;

//        findNewTarget();
        target = EntityUtil.getTarget(targetRange.getValFloat());

        if (target == null) {
            current = null;
            return;
        }

        doPlace();
        doBreak();
    }

//    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(render.getValBoolean()) {
            if (current == null) return;

            RenderUtil.drawBlockESP(current, 1, 0, 0);
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener2 = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItem(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getHand()).getItem() == Items.END_CRYSTAL) {
            placedCrystals.add(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getPos());
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketSpawnObject && instant.getValBoolean()) {
            SPacketSpawnObject packet =  (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51) {
                BlockPos toRemove = null;
                for (BlockPos pos : placedCrystals) {
                    boolean canSee = EntityUtil.canSee(pos);
                    if (!canSee && (rayTrace.getValEnum() == RayTrace.Full || rayTrace.getValEnum() == RayTrace.Hit)) break;

                    if (mc.player.getDistance(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) >= (canSee ? breakRange.getValDouble() : wallBreakRange.getValDouble()))
                        break;

                    if (breakMode.getValEnum() == BreakMode.Own && Math.sqrt(getDistance(pos.getX(), pos.getY(), pos.getZ(), packet.getX(), packet.getY(), packet.getZ())) > 1.5)
                        continue;

                    if (breakMode.getValEnum() == BreakMode.Smart && EntityUtil.getHealth(mc.player) - CrystalUtils.calculateDamage(mc.world, packet.getX(), packet.getY(), packet.getZ(), mc.player, terrainIgnore.getValBoolean()) < 0)
                        break;

                    toRemove = pos;
                    if (inhibit.getValBoolean()) {
                        try {
                            lastHitEntity = mc.world.getEntityByID(packet.getEntityID());
                        } catch (Exception ignored) { }
                    }

                    if (rotate.getValEnum() == Rotations.Full) {
                        rotation = RotationManager.calcRotation(new BlockPos(packet.getX(), packet.getY(), packet.getZ()));
                    }

                    AccessorCPacketUseEntity hitPacket = (AccessorCPacketUseEntity) new CPacketUseEntity();
                    hitPacket.setEntityId(packet.getEntityID());
                    hitPacket.setAction(CPacketUseEntity.Action.ATTACK);
                    mc.player.connection.sendPacket((CPacketUseEntity) hitPacket);
                    swing();
                    break;
                }
                if (toRemove != null) {
                    placedCrystals.remove(toRemove);
                }
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect && inhibit.getValBoolean() && lastHitEntity != null) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                if (lastHitEntity.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                    lastHitEntity.setDead();
                }
            }
        }
    });

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener1 = new Listener<>(event -> {
        if(rotation != null && !rotate.getValEnum().equals(Rotations.None) ) {
            if(!yawStep.getValEnum().equals(YawStep.None) && steps.getValDouble() < 1) {
                float packetYaw = ((IEntityPlayerSP) mc.player).getLastReportedYaw();
                float diff = MathUtil.wrapDegrees(rotation.getYaw() - packetYaw);

                if(Math.abs(diff) > 180 * steps.getValInt()) {
                    rotation.setYaw(packetYaw + (diff + ((180 * steps.getValInt()) / Math.abs(diff))));
                }
            }

            if(randomize.getValBoolean()) {
                rotation.setYaw(rotation.getYaw() + (random.nextInt(4) - 2) / 100f);
            }

            if(clientSide.getValBoolean()) {
                mc.player.rotationYaw = rotation.getYaw();
                mc.player.rotationPitch = rotation.getPitch();
            } else {
                RotationUtils.setPlayerRotations(rotation);
            }
        }
    });


    private boolean needPause() {
        if(pauseIfEating.getValBoolean() && mc.player.isHandActive()) return true;
        if(pauseIfHealth.getValBoolean() && mc.player.getHealth() <= requirestHealth.getValInt()) return true;

        return false;
    }

    private void doPlace() {
        if(!place.getValBoolean() || !placeTimer.passedMillis(placeDelay.getValInt())) return;

        if(mc.player == null && mc.world == null) return;

        EnumHand hand = null;
        double max = 0.5;

        for(BlockPos pos : CrystalUtils.getSphere(placeRange.getValFloat(), true)) {
            if(!isPosValid(pos) && CrystalUtils.canPlaceCrystal(pos, newVersionPlace.getValBoolean(), true, multiPlace.getValBoolean())) continue;

            double damage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ(), target, terrainIgnore.getValBoolean()),
                    localMinDMG = EntityUtil.getHealth(target) < facePlaceHP.getValInt() || InventoryUtil.isArmorLow(target, armor.getValInt()) ? 0.6 : minDMG.getValDouble(),
                    self = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ(), mc.player, terrainIgnore.getValBoolean()) + 2;

            if(damage <= localMinDMG || self >= maxSelfDMG.getValDouble() || EntityUtil.getHealth(mc.player) - maxSelfDMG.getValDouble() < 0 || damage < 0.5) continue;

            if(damage > max) {
                max = damage;
                current = pos;
            }
        }

        if(current == null || max == 0.6) {
            current = null;
            return;
        }

        if(syns.getValBoolean() && placedCrystals.contains(current)) return;

        boolean offhand = mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL);
        int oldSlot = mc.player.inventory.currentItem;
        int crystalSlot = InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9);

        if(mc.player.isHandActive()) hand = mc.player.getActiveHand();

        switch ((SwapMode) switchMode.getValEnum()) {
            case None: {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    return;
                }
                break;
            }
            case Normal: {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    if(crystalSlot == -1) {
                        return;
                    } else {
                        InventoryUtil.switchToSlot(crystalSlot, false);
                    }
                }
                break;
            }
            case Silent: {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    if(crystalSlot == -1) {
                        return;
                    } else {
                        InventoryUtil.switchToSlot(crystalSlot, true);
                    }
                }
                break;
            }
        }


        if(!rotate.getValEnum().equals(Rotations.None)) {
            rotation = RotationManager.calcRotation(current);
        }

        placeCrystal(current, offhand);

        if(hand != null) mc.player.setActiveHand(hand);

        if(oldSlot != -1 && switchMode.getValEnum().equals(SwapMode.Silent)) {
            InventoryUtil.switchToSlot(oldSlot, true);
        }
    }

    private void doBreak() {
        if(!_break.getValBoolean() ||! breakTimer.passedMillis(breakDelay.getValInt())) return;

        EntityEnderCrystal crystal = null;
        double max = -1337;

        for(Entity entity : mc.world.loadedEntityList) {
            if(entity instanceof  EntityEnderCrystal && isCrystalValid((EntityEnderCrystal) entity)) {
                EntityEnderCrystal crystal1 = (EntityEnderCrystal) entity;
                if(breakCalc.getValBoolean()) {
                    double dmg = CrystalUtils.calculateDamage(mc.world, crystal1.posX, crystal1.posY, crystal1.posZ, crystal1, terrainIgnore.getValBoolean());

                    if(dmg > max) {
                        max = dmg;
                        crystal = crystal1;
                    }
                } else {
                    double dist = -mc.player.getDistance(crystal1);

                    if(dist > max) {
                        max = dist;
                        crystal = crystal1;
                    }
                }
            }
        }

        if(crystal == null) return;

        int oldSlot = mc.player.inventory.currentItem;

        if(mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            int toolSlot = InventoryUtil.findAntiWeaknessTool();

            Item currentItem = mc.player.inventory.getStackInSlot(toolSlot).getItem();

            switch ((SwapMode) antiWeaknessSwitchMode.getValEnum()) {
                case None: {
                    if(!(currentItem instanceof ItemSword ) &&!(mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe)) {
                        return;
                    }
                    break;
                }
                case Normal: {
                    if(!(currentItem instanceof ItemSword ) &&!(mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe)) {
                        if(toolSlot == -1) {
                            return;
                        } else {
                            InventoryUtil.switchToSlot(toolSlot, false);
                        }
                    }
                    break;
                }
                case Silent: {
                    if(!(currentItem instanceof ItemSword ) &&!(mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe)) {
                        if(toolSlot == -1) {
                            return;
                        } else {
                            InventoryUtil.switchToSlot(toolSlot, true);
                        }
                    }
                    break;
                }
            }
        }

        if(rotate.getValEnum().equals(Rotations.Full)) {
            rotation = RotationManager.calcRotation(crystal);
        }

        breakCrystal(crystal);

        if(mc.player.isPotionActive(MobEffects.WEAKNESS) && oldSlot != -1 && switchMode.getValEnum().equals(SwapMode.Silent)) InventoryUtil.switchToSlot(oldSlot, true);
    }

    private void placeCrystal(BlockPos pos, boolean offhand) {
        if(pos == null) return;

        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + ( double ) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(( double ) pos.getX() + 0.5, ( double ) pos.getY() - 0.5, ( double ) pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0f, 0f, 0f));
        mc.player.connection.sendPacket(new CPacketAnimation(swing.getValEnum() == SwingMode.Mainhand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
        mc.playerController.updateController();
        placeTimer.reset();
    }

    private void breakCrystal(EntityEnderCrystal crystal) {
        lastHitEntity = crystal;

        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        swing();
        breakTimer.reset();

        BlockPos toRemove = null;

        if(syns.getValBoolean()) {
            for(BlockPos pos : placedCrystals) {
                if(crystal.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= 3) {
                    toRemove = pos;
                }
            }
        }

        if(toRemove != null) placedCrystals.remove(toRemove);
    }

    public void swing() {
        if(swing.getValEnum().equals(SwingMode.None)) return;

        if(packetSwing.getValBoolean()) {
            mc.player.connection.sendPacket(new CPacketAnimation(swing.getValEnum().equals(SwingMode.Mainhand) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
        } else {
            mc.player.swingArm(swing.getValEnum().equals(SwingMode.Mainhand) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
        }
    }

    private boolean isCrystalValid(EntityEnderCrystal e) {
        boolean canSee = mc.player.canEntityBeSeen(e);
        if (!canSee && (rayTrace.getValEnum() == RayTrace.Full || rayTrace.getValEnum() == RayTrace.Hit)) return false;

        if (breakMode.getValEnum() == BreakMode.Own) {
            for (BlockPos blockPos : placedCrystals) {
                if (e.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) <= 3) continue;
                return false;
            }
        } else if (breakMode.getValEnum() == BreakMode.Smart && EntityUtil.getHealth(mc.player) - CrystalUtils.calculateDamage(mc.world, e.posX, e.posY, e.posZ, mc.player, terrainIgnore.getValBoolean()) < 0) {
            return false;
        }
        return mc.player.getDistance(e) <= (canSee ? breakRange.getValDouble() : wallBreakRange.getValDouble());
    }

    private boolean isPosValid(BlockPos pos) {
        boolean canSee = EntityUtil.canSee(pos);
        if (!canSee && (rayTrace.getValEnum() == RayTrace.Full || rayTrace.getValEnum() == RayTrace.Place)) return false;
        return mc.player.getDistance(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) <= (canSee ? placeRange.getValDouble() : wallPlaceRange.getValDouble());
    }

    private void findNewTarget() {
        target = (EntityPlayer) getNearTarget(mc.player);
    }

    private EntityLivingBase getNearTarget(EntityPlayer distanceTarget) {
        return mc.world.loadedEntityList.stream()
                .filter(this::isValidTarget)
                .map(entity -> (EntityLivingBase) entity)
                .min(Comparator.comparing(distanceTarget::getDistance))
                .orElse(null);
    }

    private boolean isValidTarget(Entity entity) {
        if (entity == null) return false;
        if (!(entity instanceof EntityPlayer)) return false;
        if (Config.instance.friends.getValBoolean() && Kisman.instance.friendManager.isFriend((EntityPlayer) entity)) return false;
        if (entity.isDead || ((EntityPlayer)entity).getHealth() <= 0.0f) return false;
        if (entity.getDistance(mc.player) > 20.0f) return false;
        if (entity instanceof EntityPlayer) {
            if (entity == mc.player) return false;

            return true;
        }
        return false;
    }

    private double getDistance(double x, double y, double z, double x1, double y1, double z1) {
        double d0 = x - x1;
        double d1 = y - y1;
        double d2 = z - z1;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public enum RayTrace {
        None,
        Hit,
        Place,
        Full
    }

    public enum BreakMode {
        Always,
        Own,
        Smart
    }

    public enum Rotations {
        None,
        Semi,
        Full
    }

    public enum SwapMode {
        None,
        Normal,
        Silent
    }

    public enum SwingMode {
        Offhand,
        Mainhand,
        None
    }

    public enum YawStep {
        None,
        Full,
        Semi
    }
}
