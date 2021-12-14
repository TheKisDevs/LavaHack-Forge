package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import com.kisman.cc.module.*;
import com.kisman.cc.module.chat.Notification;
import com.kisman.cc.module.combat.autocrystal.Crystal;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextFormatting;

import java.util.*;

public class AutoCrystalBypass extends Module {
    private Setting modeLine = new Setting("ModeLine", this, "Mode");

    private Setting mode = new Setting("Mode", this, "ClientTick", new ArrayList<>(Arrays.asList("ClientTick", "MotionTick")));
    private Setting placeMode = new Setting("PlaceMode", this, "MostDamage", new ArrayList<>(Arrays.asList("Nearest", "Priority", "MostDamage")));
    private Setting breakMode = new Setting("BreakMode", this, "Always", new ArrayList<>(Arrays.asList("Always", "OnlyOwn")));
    private Setting logicPlace = new Setting("LogicPlace", this, "1", new ArrayList<>(Arrays.asList("1", "2")));
    private Setting logicMode = new Setting("LogicMode", this, "PlaceBreak", new ArrayList<>(Arrays.asList("PlaceBreak", "BreakPlace")));


    private Setting rangeLine = new Setting("RangeLine", this, "Range");

    private Setting placeRange = new Setting("PlaceRange", this, 4.2f, 0, 6, false);
    private Setting breakRange = new Setting("BreakRange", this, 4.2f, 0, 6, false);
    private Setting targetRange = new Setting("TargetRange", this, Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(1.5514623f) ^ 0x7EB69651)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(2.1071864E38f) ^ 0x7F1E86EF)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(0.59863883f) ^ 0x7EE94065)), false);
    private Setting wallRange = new Setting("WallRange", this, 3.5, 0, 5, false);


    private Setting damageLine = new Setting("DanageLine", this, "Damage");

    private Setting minDMG = new Setting("MinDMG", this, 4, 0, 20, true);
    private Setting maxSelfDMG = new Setting("MaxSelfDMG", this, 4, 0, 20, true);


    private Setting placeLine = new Setting("PlaceLine", this, "Place");

    private Setting place = new Setting("Place", this, true);
    private Setting placeHand = new Setting("PlaceHand", this, Hands.Mainhand);
    private Setting placeDelay = new Setting("PlaceDelay", this, 1, 0, 20, true);
    private Setting placeUnderBlock = new Setting("PlaceUnderBlock", this, false);

    private Setting holePlace = new Setting("HolePlace", this, true);
    private Setting raytrace = new Setting("RayTrace", this, true);
    private Setting onlyPlaceWithCrystal = new Setting("OnlyPlaceWithCrystal", this, true);
    private Setting placeObsidianIfNoValidSpots = new Setting("PlaceObsidianIfNoValidSpots", this, false);
    private Setting rotate = new Setting("Rotate", this, false);


    private Setting breakLine = new Setting("BreakLine", this, "Break");

    private Setting _break = new Setting("Break", this, true);
    private Setting breakHand = new Setting("BreakHand", this, Hands.Mainhand);
    private Setting breakDelay = new Setting("BreakDelay", this, 1, 0, 20, true);
    private Setting antiWeakness = new Setting("AntiWeakness", this, false);
    private Setting onlyBreakWithPlace = new Setting("OnlyBreakWithPlace", this, false);


    private Setting switchLine = new Setting("SwitchLine", this, "Switch");

    private Setting switchMode = new Setting("SwitchMode", this, SwitchModes.None);
    private Setting weaknessSwitchMode = new Setting("WeaknessSwitchMode", this, SwitchModes.None);


    private Setting swingLine = new Setting("SwingLine", this, "Swing");

    private Setting placeSwing = new Setting("PlaceSwing", this, true);
    private Setting breakSwing = new Setting("BreakSwing", this, true);
    private Setting packetSwing = new Setting("PacketSwing", this, false);


    private Setting pauseLine = new Setting("PauseLine", this, "Pause");

    private Setting pauseWhileEating = new Setting("PauseWhileEating", this, false);
    private Setting pauseIfHittingBlock = new Setting("PauseIfHittingBlock", this, false);
    private Setting minHealthPause = new Setting("MinHealthPause", this, false);
    private Setting requireHealth =  new Setting("RequireHealth", this, false);


    private Setting multiLine = new Setting("MultiLine", this, "MultiPlace");

    private Setting autoMultiPlace = new Setting("AutoMultiPlace", this, false);

    private Setting multiPlace = new Setting("MultiPlace", this, MultiPlaceModes.None);
    private Setting multiPlaceHP = new Setting("MultiPlaceHP", this, 10, 0, 36, true);


    private Setting faceLine = new Setting("FaceLine", this, "FacePlace");

    private Setting facePlace = new Setting("FacePlace", this, true);
    private Setting facePlaceHP = new Setting("FacePlaceHP", this,  10, 0, 36, true);


    private Setting armorLine = new Setting("ArmorLine", this, "ArmorBreaker");

    private Setting armorBreaker = new Setting("ArmorBreaker", this, true);
    private Setting armorPercent = new Setting("ArmorPercent", this, 20, 0, 100, true);


    private Setting singlePlaceLine = new Setting("SinglePlaceLine", this, "SinglePlace");

    private Setting secondCheck = new Setting("SecondCheck", this, true);


    private Setting otherLine = new Setting("OtherLine", this, "Other");

    private Setting antiSuicide = new Setting("AntiSuicide", this, true);
    private Setting syns = new Setting("Syns", this, true);
    private Setting placeCalculate = new Setting("Calculate", this, "kisman.cc", new ArrayList<>(Arrays.asList("kisman.cc", "Europa")));


    private int placeTicks;
    private int breakTicks;

    public ArrayList<BlockPos> placedCrystal = new ArrayList<>();

    public EntityPlayer target = null;

    private AimBot aimBot;

    public static AutoCrystalBypass instance;

    public AutoCrystalBypass() {
        super("AutoCrystalBypass", "AutoCrystalBypass", Category.COMBAT);

        instance = this;

        aimBot = AimBot.instance;

        setmgr.rSetting(modeLine);
        setmgr.rSetting(mode);
        setmgr.rSetting(placeMode);
        setmgr.rSetting(breakMode);
        setmgr.rSetting(logicPlace);
        setmgr.rSetting(logicMode);

        setmgr.rSetting(rangeLine);
        setmgr.rSetting(placeRange);
        setmgr.rSetting(breakRange);
        setmgr.rSetting(targetRange);
        setmgr.rSetting(wallRange);

        setmgr.rSetting(damageLine);
        setmgr.rSetting(minDMG);
        setmgr.rSetting(maxSelfDMG);

        setmgr.rSetting(switchLine);
        setmgr.rSetting(switchMode);
        setmgr.rSetting(weaknessSwitchMode);

        setmgr.rSetting(swingLine);
        setmgr.rSetting(placeSwing);
        setmgr.rSetting(breakSwing);
        setmgr.rSetting(packetSwing);

        setmgr.rSetting(placeLine);
        setmgr.rSetting(place);
        setmgr.rSetting(placeHand);
        setmgr.rSetting(placeDelay);
        setmgr.rSetting(placeUnderBlock);
        setmgr.rSetting(holePlace);
        setmgr.rSetting(raytrace);
        setmgr.rSetting(onlyPlaceWithCrystal);
        setmgr.rSetting(placeObsidianIfNoValidSpots);
        setmgr.rSetting(rotate);

        setmgr.rSetting(breakLine);
        setmgr.rSetting(_break);
        setmgr.rSetting(breakHand);
        setmgr.rSetting(breakDelay);
        setmgr.rSetting(antiWeakness);
        setmgr.rSetting(onlyBreakWithPlace);

        setmgr.rSetting(pauseLine);
        setmgr.rSetting(pauseWhileEating);
        setmgr.rSetting(pauseIfHittingBlock);
        setmgr.rSetting(minHealthPause);
        setmgr.rSetting(requireHealth);

        setmgr.rSetting(multiLine);
        setmgr.rSetting(autoMultiPlace);
        setmgr.rSetting(multiPlace);
        setmgr.rSetting(multiPlaceHP);

        setmgr.rSetting(faceLine);
        setmgr.rSetting(facePlace);
        setmgr.rSetting(facePlaceHP);

        setmgr.rSetting(armorLine);
        setmgr.rSetting(armorBreaker);
        setmgr.rSetting(armorPercent);

        setmgr.rSetting(singlePlaceLine);
        setmgr.rSetting(secondCheck);

        setmgr.rSetting(otherLine);
        setmgr.rSetting(antiSuicide);
        setmgr.rSetting(syns);
        setmgr.rSetting(placeCalculate);
    }

    public void onEnable() {
        target = null;
        placedCrystal.clear();

        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);

        target = null;
        placedCrystal.clear();
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        if(target != null) {
            super.setDisplayInfo("[" +  target.getDisplayName().getFormattedText() + TextFormatting.GRAY + "]");
        } else {
            super.setDisplayInfo("");
        }

        if(mode.getValString().equalsIgnoreCase("ClientTick")) {
            doAutoCrystal(null);
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener1 = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketSoundEffect && syns.getValBoolean()) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();

            if(packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for(Entity entity : mc.world.loadedEntityList) {
                    if (!(entity instanceof EntityEnderCrystal) || !(entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) <= Double.longBitsToDouble(Double.doubleToLongBits(0.03533007623236061) ^ 0x7FE016C8A3F762CFL))) {
                        continue;
                    }

                    entity.setDead();
                }
            }
        }
    });

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {
        if(mode.getValString().equalsIgnoreCase("MotionTick")) {
            doAutoCrystal(event);
        }
    });

    private void doAutoCrystal(EventPlayerMotionUpdate event) {
        if(mc.player == null && mc.world == null) return;

        switch(logicMode.getValString()) {
            case "PlaceBreak": {
                doPlaceCrystal(event == null ? null : event);
                breakCrystal(null);
            }
            case "BreakPlace": {
                breakCrystal(null);
                doPlaceCrystal(event == null ? null : event);
            }
        }
    }

    private void doPlaceCrystal(EventPlayerMotionUpdate event) {
        if(place.getValBoolean()) {
            if (logicMode.getValString().equalsIgnoreCase("1")) {

                if (placeTicks++ <= placeDelay.getValInt()) {
                    return;
                }

                if (needPause()) {
                    aimBot.rotationSpoof = null;

                    return;
                }

                placeCrystal(event);

                placeTicks = 0;
            } else {
                placeSingleCrystal(event);
            }
        }
    }

    private BlockPos placeCrystal(EventPlayerMotionUpdate event) {
        List<BlockPos> availableBlockPositions = CrystalUtils.findCrystalBlocks(mc.player, (int) placeRange.getValDouble());

        switch (placeMode.getValString()) {
            case "Nearest":
                findNewTarget();
                break;
            case "Priority":
                if (target == null || target.getDistance(mc.player) > placeRange.getValDouble() + 2f || target.isDead || target.getHealth() <= 0.0f) ///< Allow 2 tolerence
                    findNewTarget();
                break;
            case "MostDamage": {
                if (availableBlockPositions.isEmpty()) {
                    findNewTarget();
                } else {
                    EntityPlayer l_Target = null;

                    float minDMG = (float) this.minDMG.getValDouble();
                    float maxSelfDMG = (float) this.maxSelfDMG.getValDouble();
                    float dmg = 0.0f;

                    for (EntityPlayer player : mc.world.playerEntities) {
                        if (!isValidTarget(player))
                            continue;

                        for (BlockPos pos : availableBlockPositions) {
                            if (player.getDistanceSq(pos) >= 169.0D)
                                continue;

                            float l_TempDMG = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, player, 0);

                            if (l_TempDMG < minDMG)
                                continue;

                            float l_SelfTempDMG = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, mc.player, 0);

                            if (l_SelfTempDMG > maxSelfDMG)
                                continue;

                            if (wallRange.getValDouble() > 0) {
                                if (!PlayerUtil.CanSeeBlock(pos))
                                    if (pos.getDistance((int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ) > wallRange.getValDouble())
                                        continue;
                            }

                            if (l_TempDMG > dmg) {
                                dmg = l_TempDMG;
                                l_Target = player;
                            }
                        }
                    }

                    if (target == null)
                        findNewTarget();

                    if (target != null && l_Target != target && l_Target != null && Notification.instance.target.getValBoolean()) {
                        String newTarget = String.format("Found new target %s", l_Target.getName());
                        ChatUtils.complete(newTarget);
                    }

                    target = l_Target;
                }
                break;
            }
            default:
                break;
        }

        if (availableBlockPositions.isEmpty()) {
            /*if (placeObsidianIfNoValidSpots.getValBoolean() && target != null) {
                int slot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);

                if (slot != -1) {
                    if (mc.player.inventory.currentItem != slot) {
                        mc.player.inventory.currentItem = slot;
                        mc.playerController.updateController();
                        return BlockPos.ORIGIN;
                    }

                    float range = (float) placeRange.getValDouble();

                    float targetDMG = 0.0f;
                    float _minDMG = (float) minDMG.getValDouble();

                    /// FacePlace
                    if (target.getHealth() + target.getAbsorptionAmount() <= facePlaceHP.getValDouble())
                        _minDMG = 1f;

                    BlockPos l_TargetPos = null;

                    for (
                            BlockPos pos : BlockInteractionHelper.getSphere(
                            PlayerUtil.GetLocalPlayerPosFloored(),
                            (float) placeRange.getValDouble(),
                            (int)range, false, true, 0
                    )) {
                        if(placeCalculate.getValString().equalsIgnoreCase("First")) {
                            BlockInteractionHelper.ValidResult l_Result = BlockInteractionHelper.valid(pos);

                            if (l_Result != BlockInteractionHelper.ValidResult.Ok)
                                continue;

                            if (!CrystalUtils.CanPlaceCrystalIfObbyWasAtPos(pos))
                                continue;

                            float l_TempDMG = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, target, 0);

                            if (l_TempDMG < _minDMG)
                                continue;

                            if (l_TempDMG >= targetDMG) {
                                l_TargetPos = pos;
                                targetDMG = l_TempDMG;
                            }

                            if (l_TargetPos != null) {
                                BlockInteractionHelper.place(l_TargetPos, (float) placeRange.getValDouble(), true, false); ///< sends a new packet, might be bad for ncp flagging tomany packets..

                                if (Notification.instance.placeObby.getValBoolean()) {
                                    String placeMsg = String.format("Tried to place obsidian at %s would deal %s dmg", l_TargetPos.toString(), targetDMG);

                                    ChatUtils.complete(placeMsg);
                                }
                            }
                        } else if(placeCalculate.getValString().equalsIgnoreCase("Second")) {
                            if(target == null) {
                                findNewTarget();
                            } else if(
                                    mc.world.getBlockState(pos).getBlock() == Blocks.AIR ||
                                            !CrystalUtils.canPlaceCrystal(pos, placeUnderBlock.getValBoolean(), ((MultiPlaceModes) multiPlace.getValEnum()).equals(MultiPlaceModes.Static) != false ||
                                                            (multiPlace.getValEnum()).equals(MultiPlaceModes.Dynamic) != false &&
                                                                    CrystalUtils.isEntityMoving(mc.player) != false &&
                                                                    CrystalUtils.isEntityMoving(target) != false,
                                                    holePlace.getValBoolean())
                            ) {
                                return pos;
                            }
                        }
                    }
                }
            }*/

            return BlockPos.ORIGIN;
        }


        if (target == null)
            return BlockPos.ORIGIN;

        if (autoMultiPlace.getValBoolean()) {
            if (target.getHealth() + target.getAbsorptionAmount() <= multiPlaceHP.getValDouble())
                multiPlace.setValEnum(MultiPlaceModes.Dynamic);
            else
                multiPlace.setValEnum(MultiPlaceModes.None);
        }

        float l_MinDmg = (float) minDMG.getValDouble();
        float l_MaxSelfDmg = (float) maxSelfDMG.getValDouble();
        float l_FacePlaceHealth = (float) facePlaceHP.getValDouble();

        /// FacePlace
        if (target.getHealth() <= l_FacePlaceHealth)
            l_MinDmg = 1f;

        /// AntiSuicide
        if (antiSuicide.getValBoolean()) {
            while (mc.player.getHealth() + mc.player.getAbsorptionAmount() < l_MaxSelfDmg)
                l_MaxSelfDmg /= 2;
        }

        BlockPos bestPosition = null;
        float l_DMG = 0.0f;

        /// todo: use this, but we will lose dmg... maybe new option, for LeastDMGToSelf? but seems useless
        float l_SelfDMG = 0.0f;

        for (BlockPos l_Pos : availableBlockPositions) {
            if (target.getDistanceSq(l_Pos) >= 169.0D)
                continue;

            float l_TempDMG = CrystalUtils.calculateDamage(mc.world, l_Pos.getX() + 0.5, l_Pos.getY() + 1.0, l_Pos.getZ() + 0.5, target, 0);

            if (l_TempDMG < l_MinDmg)
                continue;

            float l_SelfTempDMG = CrystalUtils.calculateDamage(mc.world, l_Pos.getX() + 0.5, l_Pos.getY() + 1.0, l_Pos.getZ() + 0.5, mc.player, 0);

            if (l_SelfTempDMG > l_MaxSelfDmg)
                continue;

            if (wallRange.getValDouble() > 0) {
                if (!PlayerUtil.CanSeeBlock(l_Pos))
                    if (l_Pos.getDistance((int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ) > wallRange.getValDouble())
                        continue;
            }

            if (l_TempDMG > l_DMG) {
                l_DMG = l_TempDMG;
                l_SelfDMG = l_SelfTempDMG;
                bestPosition = l_Pos;
            }
        }

        if (bestPosition == null)
            return BlockPos.ORIGIN;

        /*for (Hole l_Hole : Holes.GetHoles())
        {
            float l_HoleFillDmg = CrystalUtils.calculateDamage(mc.world, l_Hole.getX() + 0.5, l_Hole.getY() + 1.0, l_Hole.getZ() + 0.5, l_Player, 0);
            if (l_HoleFillDmg > l_DMG)
            {
                m_HoleToFill = l_Hole;
                l_DMG = l_HoleFillDmg;
            }
        }
        if (m_HoleToFill != null)
        {
            SalHack.INSTANCE.logChat("Filling the hole at " + m_HoleToFill.toString() + " will deal " + l_DMG);
           // return;
        }*/

//        if(e_target != null) {
//            if(e_target instanceof EntityPlayer) {
//                if (filterPosition(bestPosition, (EntityPlayer) e_target));
//            }
//        } else {
//            findNewTarget();
//        }

        final int crystalSlot = InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9);
        final int oldSlot = mc.player.inventory.currentItem;
        switch ((SwitchModes) switchMode.getValEnum()) {
            case None: {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    return BlockPos.ORIGIN;
                }
            }
            case Normal: {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    if(crystalSlot == -1) {
                        return BlockPos.ORIGIN;
                    } else {
                        InventoryUtil.switchToSlot(crystalSlot, false);
                    }
                }
            }
            case Silent: {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
                    if(crystalSlot == -1) {
                        return BlockPos.ORIGIN;
                    } else {
                        InventoryUtil.switchToSlot(crystalSlot, true);
                    }
                }
            }
        }

        final double pos[] =  EntityUtil.calculateLookAt(
                bestPosition.getX() + 0.5,
                bestPosition.getY() - 0.5,
                bestPosition.getZ() + 0.5,
                mc.player);

        if (mode.getValString().equalsIgnoreCase("ClientTick")) {
            aimBot.rotationSpoof = new RotationSpoof((float)pos[0], (float)pos[1]);

            Random rand = new Random(2);

            aimBot.rotationSpoof.yaw += (rand.nextFloat() / 100);
            aimBot.rotationSpoof.pitch += (rand.nextFloat() / 100);
        }

        EnumFacing facing = null;

        if(raytrace.getValBoolean()) {
            RayTraceResult result = mc.world.rayTraceBlocks(
                    new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ),
                    new Vec3d(bestPosition.getX() + 0.5, bestPosition.getY() - 0.5,
                            bestPosition.getZ() + 0.5)
            );

            if (result == null || result.sideHit == null)
                facing = EnumFacing.UP;
            else
                facing = result.sideHit;
        }

        if (mode.getValString().equalsIgnoreCase("MotionTick") && event != null) {
            event.cancel();

            spoofRotationsTo((float)pos[0], (float)pos[1]);
        }

        boolean offhand = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        boolean validateSilentSwitch = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && switchMode.getValEnum().equals(SwitchModes.Silent);
        if(mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(bestPosition, facing,
                    (validateSilentSwitch ? EnumHand.MAIN_HAND: offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND), 0, 0, 0));
        }

        swingItem(false);
        // mc.playerController.processRightClickBlock(mc.player, mc.world, l_BestPosition, EnumFacing.UP, new Vec3d(0, 0, 0), EnumHand.MAIN_HAND);
        // SalHack.INSTANCE.logChat(String.format("%s%s DMG and SelfDMG %s %s %S", ChatFormatting.LIGHT_PURPLE, l_DMG, l_SelfDMG, l_Facing, m_Target.getName()));

        placedCrystal.add(bestPosition);

        if(oldSlot != -1 && switchMode.getValEnum().equals(SwitchModes.Silent)) {
            InventoryUtil.switchToSlot(oldSlot, true);
        }

        return bestPosition;
    }

    private void breakCrystal(BlockPos pos) {
        if(!_break.getValBoolean()) {
            return;
        }

        if(breakTicks++ <= breakDelay.getValInt()) {
            return;
        }

        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream()
                .filter(entity -> isValidCrystal(entity))
                .map(entity -> (EntityEnderCrystal) entity)
                .min(Comparator.comparing(entityEnderCrystal -> mc.player.getDistance(entityEnderCrystal)))
                .orElse(null);

        if(crystal == null) {
            return;
        }

        final int swordSlot = InventoryUtil.findItem(Items.DIAMOND_SWORD, 0, 9);
        final int oldSlot = mc.player.inventory.currentItem;

        if(antiWeakness.getValBoolean() && mc.player.isPotionActive(MobEffects.WEAKNESS) && swordSlot != -1 && !(mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD)) {
            switch((SwitchModes) weaknessSwitchMode.getValEnum()) {
                case Normal: {
                    InventoryUtil.switchToSlot(swordSlot, false);
                    break;
                }
                case Silent: {
                    InventoryUtil.switchToSlot(swordSlot, true);
                    break;
                }
                default: {
                    break;
                }
            }
        }

        mc.playerController.attackEntity(mc.player, crystal);
        swingItem(true);

        if(oldSlot != -1 && weaknessSwitchMode.getValEnum().equals(SwitchModes.Silent)) {
            InventoryUtil.switchToSlot(oldSlot, true);
        }

        breakTicks = 0;
    }

    private BlockPos placeSingleCrystal(EventPlayerMotionUpdate event) {
        if(placeTicks++ <= placeDelay.getValInt()) {
            return BlockPos.ORIGIN;
        }

        if(!isValidItemsInHand()) {
            return BlockPos.ORIGIN;
        }

//        if(switchMode.getValEnum().equals(SwitchModes.None) && mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) return BlockPos.ORIGIN;

        EnumHand hand = null;
        BlockPos placePos = null;
        double maxDamage = 0.5;

        for(BlockPos pos : CrystalUtils.getSphere((float) placeRange.getValDouble(), true, false)) {
            final double targetDMG = EntityUtil.calculate(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, target);
            final double selfDMG = EntityUtil.calculate(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, mc.player) + 2.0;

            if(CrystalUtils.canPlaceCrystal(pos, this.secondCheck.getValBoolean()) && ((targetDMG >= this.minDMG.getValInt() || (targetDMG >= target.getHealth() && target.getHealth() <= this.facePlaceHP.getValInt() || (target.getHealth() <= armorPercent.getValDouble())) && ((EntityUtil.calculate(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, mc.player) + 2.0 < mc.player.getHealth() && selfDMG < targetDMG))))) {
                if(maxDamage > targetDMG) continue;

                if(target.isDead) continue;

                placePos = pos;
                maxDamage = targetDMG;
            }
        }

        if(maxDamage == 0.5) return BlockPos.ORIGIN;

        final int crystalSlot = InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9);
        final int oldSlot = mc.player.inventory.currentItem;
        switch ((SwitchModes) switchMode.getValEnum()) {
            case None: {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    return BlockPos.ORIGIN;
                }
            }
            case Normal: {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    if(crystalSlot == -1) {
                        return BlockPos.ORIGIN;
                    } else {
                        InventoryUtil.switchToSlot(crystalSlot, false);
                    }
                }
            }
            case Silent: {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
                    if(crystalSlot == -1) {
                        return BlockPos.ORIGIN;
                    } else {
                        InventoryUtil.switchToSlot(crystalSlot, true);
                    }
                }
            }
        }

        if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) return BlockPos.ORIGIN;

        if(mc.player.isHandActive()) {
            hand = mc.player.getActiveHand();
        }

        if(rotate.getValBoolean()) {
            //rotate
        }

        final EnumFacing facing = EnumFacing.UP;
        boolean offhand = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;

        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, facing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
        mc.playerController.updateController();

        if(switchMode.getValEnum().equals(SwitchModes.Silent) && oldSlot != -1) {
            InventoryUtil.switchToSlot(oldSlot, true);
        }

        placeTicks = 0;
        return placePos;
    }

    private boolean isValidItemsInHand() {
        int i = 0;

        if(mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) i++;
        if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
            if(!switchMode.getValEnum().equals(SwitchModes.None)) i++;
        }

        if(i > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void findNewTarget() {
        target = (EntityPlayer) getNearTarget(mc.player);
    }

    public EntityLivingBase getNearTarget(EntityPlayer distanceTarget) {
        return mc.world.loadedEntityList.stream()
                .filter(entity -> isValidTarget(entity))
                .map(entity -> (EntityLivingBase) entity)
                .min(Comparator.comparing(entity -> distanceTarget.getDistance(entity)))
                .orElse(null);
    }

    private EntityLivingBase getNearTargetCrystal(EntityEnderCrystal distanceTarget) {
        return mc.world.loadedEntityList.stream()
                .filter(entity -> isValidTarget(entity))
                .map(entity -> (EntityLivingBase) entity)
                .min(Comparator.comparing(entity -> distanceTarget.getDistance(entity)))
                .orElse(null);
    }

    public boolean isValidTarget(Entity entity) {
        if (entity == null)
            return false;

        if (!(entity instanceof EntityLivingBase))
            return false;

        if (entity.isDead || ((EntityLivingBase)entity).getHealth() <= 0.0f)
            return false;

        if (entity.getDistance(mc.player) > 20.0f)
            return false;

        if (entity instanceof EntityPlayer) {
            if (entity == mc.player)
                return false;

            return true;
        }

        return false;
    }

    private boolean isValidCrystal(Entity entity) {
        if(!(entity instanceof EntityEnderCrystal)) {
            return false;
        }


        if (entity.getDistance(mc.player) > (!mc.player.canEntityBeSeen(entity) ? wallRange.getValDouble() : breakRange.getValDouble()))
            return false;

        switch (breakMode.getValString()) {
            case "Always":
                return true;
            case "OnlyOwn":
                /// create copy
                for (BlockPos pos : new ArrayList<>(placedCrystal)) {
                    if (pos != null && pos.getDistance((int)entity.posX, (int)entity.posY, (int)entity.posZ) <= 3.0)
                        return true;
                }
                break;
            case "Smart":
                EntityLivingBase target = this.target != null ? this.target : getNearTargetCrystal((EntityEnderCrystal) entity);

                if (target == null)
                    return false;

                float targetDMG = CrystalUtils.calculateDamage(mc.world, entity.posX + 0.5, entity.posY + 1.0, entity.posZ + 0.5, target, 0);
                float selfDMG = CrystalUtils.calculateDamage(mc.world, entity.posX + 0.5, entity.posY + 1.0, entity.posZ + 0.5, mc.player, 0);

                float minDMG = (float) this.minDMG.getValDouble();

                /// FacePlace
                if (target.getHealth() + target.getAbsorptionAmount() <= facePlaceHP.getValDouble())
                    minDMG = 1f;

                if (targetDMG > minDMG && selfDMG < maxSelfDMG.getValDouble())
                    return true;

                break;
            default:
                break;
        }

        return false;
    }

    public void swingItem(boolean breakSwing) {
        blockSwing: {
            if(breakSwing) {
                block0: {
                    block3: {
                        block1: {
                            block2: {
                                if (breakHand.getValEnum().equals(Hands.None)) break block0;

                                if (!packetSwing.getValBoolean()) break block1;

                                if (!breakHand.getValEnum().equals(Hands.Mainhand)) break block2;

                                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                                break block0;
                            }

                            if (!breakHand.getValEnum().equals(Hands.Offhand)) break block0;

                            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                            break block0;
                        }

                        if (!breakHand.getValEnum().equals(Hands.Mainhand)) break block3;

                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        break block0;
                    }

                    if (!breakHand.getValEnum().equals(Hands.Offhand)) break block0;

                    mc.player.swingArm(EnumHand.OFF_HAND);
                }
            } else {
                block0: {
                    block3: {
                        block1: {
                            block2: {
                                if (placeHand.getValEnum().equals(Hands.None)) break block0;

                                if (!packetSwing.getValBoolean()) break block1;

                                if (!placeHand.getValEnum().equals(Hands.Mainhand)) break block2;

                                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                                break block0;
                            }

                            if (!placeHand.getValEnum().equals(Hands.Offhand)) break block0;

                            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                            break block0;
                        }

                        if (!placeHand.getValEnum().equals(Hands.Mainhand)) break block3;

                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        break block0;
                    }

                    if (!placeHand.getValEnum().equals(Hands.Offhand)) break block0;

                    mc.player.swingArm(EnumHand.OFF_HAND);
                }
            }
        }
    }

    public enum Renders {
        None,
        Normal
    }

    public enum MultiPlaceModes {
        None,
        Dynamic,
        Static
    }

    public enum Hands {
        None,
        Mainhand,
        Offhand
    }

    public enum SwitchModes {
        None,
        Normal,
        Silent
    }

    private void spoofRotationsTo(float _yaw, float _pitch) {
        boolean isSprinting = mc.player.isSprinting();

        if (isSprinting != mc.player.serverSprintState) {
            if (isSprinting)
            {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            } else {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }

            mc.player.serverSprintState = isSprinting;
        }

        boolean isSneaking = mc.player.isSneaking();

        if (isSneaking != mc.player.serverSneakState) {
            if (isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            } else {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }

            mc.player.serverSneakState = isSneaking;
        }

        if (PlayerUtil.isCurrentViewEntity()) {
            float pitch = _pitch;
            float yaw = _yaw;

            AxisAlignedBB axisalignedbb = mc.player.getEntityBoundingBox();
            double posXDifference = mc.player.posX - mc.player.lastReportedPosX;
            double posYDifference = axisalignedbb.minY - mc.player.lastReportedPosY;
            double posZDifference = mc.player.posZ - mc.player.lastReportedPosZ;
            double yawDifference = (double)(yaw - mc.player.lastReportedYaw);
            double rotationDifference = (double)(pitch - mc.player.lastReportedPitch);
            ++mc.player.positionUpdateTicks;
            boolean movedXYZ = posXDifference * posXDifference + posYDifference * posYDifference + posZDifference * posZDifference > 9.0E-4D || mc.player.positionUpdateTicks >= 20;
            boolean movedRotation = yawDifference != 0.0D || rotationDifference != 0.0D;

            if (mc.player.isRiding()) {
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.motionX, -999.0D, mc.player.motionZ, yaw, pitch, mc.player.onGround));
                movedXYZ = false;
            } else if (movedXYZ && movedRotation) {
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, axisalignedbb.minY, mc.player.posZ, yaw, pitch, mc.player.onGround));
            } else if (movedXYZ) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, axisalignedbb.minY, mc.player.posZ, mc.player.onGround));
            } else if (movedRotation) {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, mc.player.onGround));
            } else if (mc.player.prevOnGround != mc.player.onGround) {
                mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));
            }

            if (movedXYZ) {
                mc.player.lastReportedPosX = mc.player.posX;
                mc.player.lastReportedPosY = axisalignedbb.minY;
                mc.player.lastReportedPosZ = mc.player.posZ;
                mc.player.positionUpdateTicks = 0;
            }

            if (movedRotation) {
                mc.player.lastReportedYaw = yaw;
                mc.player.lastReportedPitch = pitch;
            }

            mc.player.prevOnGround = mc.player.onGround;
            mc.player.autoJumpEnabled = mc.player.mc.gameSettings.autoJump;
        }
    }

    private boolean needPause() {
        if(minHealthPause.getValBoolean() && (mc.player.getHealth() + mc.player.getAbsorptionAmount()) < (int) requireHealth.getValDouble()) {
            return true;
        }

        if(pauseIfHittingBlock.getValBoolean() &&  mc.playerController.isHittingBlock && mc.player.getHeldItemMainhand().getItem() instanceof ItemTool) {
            return true;
        }

        if(pauseWhileEating.getValBoolean() && PlayerUtil.IsEating()) {
            return true;
        }

        return false;
    }
}