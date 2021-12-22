package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import com.kisman.cc.module.*;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.module.combat.autocrystal.Crystal;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class AutoCrystal extends Module {
    public static AutoCrystal instance;

    private final Setting modeLine = new Setting("ModeLine", this, "Mode");

    private final Setting mode = new Setting("Mode", this, "ClientTick", new ArrayList<>(Arrays.asList("ClientTick", "MotionTick")));
    private final Setting placeMode = new Setting("PlaceMode", this, "MostDamage", new ArrayList<>(Arrays.asList("Nearest", "Priority", "MostDamage")));
    private final Setting breakMode = new Setting("BreakMode", this, "Always", new ArrayList<>(Arrays.asList("Always", "Smart")));
    private final Setting logicMode = new Setting("LogicMode", this, "PlaceBreak", new ArrayList<>(Arrays.asList("PlaceBreak", "BreakPlace")));
    private final Setting syns = new Setting("Syns", this, true);
    private final Setting rotate = new Setting("Rotate", this, false);
    private final Setting rotations = new Setting("Spoofs", this, 1, 1, 20, true);
    private final Setting raytrace = new Setting("Raytrace", this, false);
    private final Setting debug = new Setting("Debug", this, false);


    private final Setting rangeLine = new Setting("RangeLine", this, "Range");

    private final Setting placeRange = new Setting("PlaceRange", this, 4.2f, 0, 6, false);
    private final Setting breakRange = new Setting("BreakRange", this, 4.2f, 0, 6, false);
    private final Setting targetRange = new Setting("TargetRange", this, Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(1.5514623f) ^ 0x7EB69651)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(2.1071864E38f) ^ 0x7F1E86EF)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(0.59863883f) ^ 0x7EE94065)), false);
    private final Setting wallRange = new Setting("WallRange", this, 3.5, 0, 5, false);


    private final Setting damageLine = new Setting("DanageLine", this, "Damage");

    private final Setting minDMG = new Setting("MinDMG", this, 4, 0, 20, true);
    private final Setting maxSelfDMG = new Setting("MaxSelfDMG", this, 4, 0, 20, true);


    private final Setting placeLine = new Setting("PlaceLine", this, "Place");

    private final Setting place = new Setting("Place", this, true);
    private final Setting placeDelay = new Setting("PlaceDelay", this, 1, 0, 20, true);
    private final Setting placeHand = new Setting("PlaceHand", this, Hands.Mainhand);
    private final Setting secondCheck = new Setting("SecondCheck", this, true);
    private final Setting placeObsidianIfNoValidSpots = new Setting("PlaceObsidianIfNoValidSpots", this, false);
    private final Setting placeRotate = new Setting("PlaceRotate", this, false);


    private final Setting breakLine = new Setting("BreakLine", this, "Break");

    private final Setting _break = new Setting("Break", this, true);
    private final Setting breakDelay = new Setting("BreakDelay", this, 1, 0, 20, true);
    private final Setting breakHand = new Setting("BreakHand", this, Hands.Mainhand);
    private final Setting antiWeakness = new Setting("AntiWeakness", this, false);
    private final Setting breakRotate = new Setting("BreakRotate", this, false);


    private final Setting switchLine = new Setting("SwitchLine", this, "Switch");

    private final Setting switchMode = new Setting("SwitchMode", this, SwitchModes.None);
    private final Setting weaknessSwitchMode = new Setting("WeaknessSwitchMode", this, SwitchModes.None);


    private final Setting swingLine = new Setting("SwingLine", this, "Swing");

    private final Setting placeSwing = new Setting("PlaceSwing", this, true);
    private final Setting breakSwing = new Setting("BreakSwing", this, true);
    private final Setting packetSwing = new Setting("PacketSwing", this, false);


    private final Setting pauseLine = new Setting("PauseLine", this, "Pause");

    private final Setting pauseWhileEating = new Setting("PauseWhileEating", this, false);
    private final Setting pauseIfHittingBlock = new Setting("PauseIfHittingBlock", this, false);
    private final Setting minHealthPause = new Setting("MinHealthPause", this, false);
    private final Setting requireHealth =  new Setting("RequireHealth", this, false);


    private final Setting faceLine = new Setting("FaceLine", this, "FacePlace");

    private final Setting facePlace = new Setting("FacePlace", this, true);
    private final Setting facePlaceHP = new Setting("FacePlaceHP", this,  10, 0, 36, true);


    private final Setting armorLine = new Setting("ArmorLine", this, "ArmorBreaker");

    private final Setting armorBreaker = new Setting("ArmorBreaker", this, true);
    private final Setting armorPercent = new Setting("ArmorPercent", this, 20, 0, 100, true);


    private final Setting renderLine = new Setting("RenderLine", this, "Render");

    private final Setting render = new Setting("Render", this, true);
    private final Setting color = new Setting("Color", this, "Color", new float[] {0.9f, 0.11f, 0.11f, 1});
    private final Setting renderMode = new Setting("RenderMode", this, Renders.Fill);
    private final Setting renderDamage = new Setting("RenderDamage", this, true);


    public EntityPlayer target = null;
    public Crystal lastPlaceCrystal = null;

    private int placeTicks;
    private int breakTicks;

    private int rotationPacketsSpoofs = 0;
    private boolean rotating = false;
    private float yaw = 0f, pitch = 0f;

    public AutoCrystal() {
        super("AutoCrystal", "super.gay();", Category.COMBAT);

        instance = this;

        setmgr.rSetting(modeLine);
        setmgr.rSetting(mode);
        setmgr.rSetting(placeMode);
        setmgr.rSetting(breakMode);
        setmgr.rSetting(logicMode);
        setmgr.rSetting(syns);
        setmgr.rSetting(rotate);
        setmgr.rSetting(rotations);
        setmgr.rSetting(raytrace);
        setmgr.rSetting(debug);

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
        setmgr.rSetting(placeDelay);
        setmgr.rSetting(placeHand);
        setmgr.rSetting(secondCheck);
        setmgr.rSetting(placeObsidianIfNoValidSpots);
        setmgr.rSetting(placeRotate);

        setmgr.rSetting(breakLine);
        setmgr.rSetting(_break);
        setmgr.rSetting(breakDelay);
        setmgr.rSetting(breakHand);
        setmgr.rSetting(antiWeakness);
        setmgr.rSetting(breakRotate);

        setmgr.rSetting(pauseLine);
        setmgr.rSetting(pauseWhileEating);
        setmgr.rSetting(pauseIfHittingBlock);
        setmgr.rSetting(minHealthPause);
        setmgr.rSetting(requireHealth);

        setmgr.rSetting(faceLine);
        setmgr.rSetting(facePlace);
        setmgr.rSetting(facePlaceHP);

        setmgr.rSetting(armorLine);
        setmgr.rSetting(armorBreaker);
        setmgr.rSetting(armorPercent);

        setmgr.rSetting(renderLine);
        setmgr.rSetting(render);
        setmgr.rSetting(color);
        setmgr.rSetting(renderMode);
        setmgr.rSetting(renderDamage);
    }

    public void onEnable() {
        placeTicks = 0;
        breakTicks = 0;
        lastPlaceCrystal = null;

        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);

        lastPlaceCrystal = null;
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        findNewTarget();

        if(target != null) {
            super.setDisplayInfo("[" +  target.getDisplayName().getFormattedText() + TextFormatting.GRAY + "]");
        } else {
            super.setDisplayInfo("");
        }

        doAutoCrystal();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(render.getValBoolean() && lastPlaceCrystal != null && lastPlaceCrystal.pos != null) {
            switch ((Renders) renderMode.getValEnum()) {
                case Both: {
                    RenderUtil.drawBlockESP(lastPlaceCrystal.pos, color.getR() / 255, color.getG() / 255, color.getB() / 255);
                    break;
                }
                case Fill: {
                    RenderUtil.drawBox(lastPlaceCrystal.pos, 1, new Colour(color.getR(), color.getG(), color.getB(), color.getA()), GeometryMasks.Quad.ALL);
                }
                case Outline: {
                    RenderUtil.drawBlockOutlineESP(lastPlaceCrystal.pos, color.getR() / 255, color.getG() / 255, color.getB() / 255);
                    break;
                }
            }

            if(renderDamage.getValBoolean()) {

            }
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener1 = new Listener<>(event -> {
        if(rotate.getValBoolean() && rotating && event.getPacket() instanceof CPacketPlayer) {
            final CPacketPlayer packet = (CPacketPlayer) event.getPacket();

            packet.yaw = yaw;
            packet.pitch = pitch;

            ++rotationPacketsSpoofs;

            if(rotationPacketsSpoofs >= rotations.getValInt()) {
                rotating = false;
                rotationPacketsSpoofs = 0;
            }
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
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

    private void doAutoCrystal() {
        doAutoCrystalLogic();
    }

    private void doAutoCrystalLogic() {
        switch (logicMode.getValString()) {
            case "PlaceBreak": {
                if(place.getValBoolean() && target != null) {
                    placeCrystal();
                }
                if(_break.getValBoolean()) {
                    breakCrystal();
                }
                break;
            }
            case "BreakPlace": {
                if(_break.getValBoolean()) {
                    breakCrystal();
                }
                if(place.getValBoolean() && target != null) {
                    placeCrystal();
                }
                break;
            }
        }
    }

    private BlockPos placeCrystal() {
        if(placeTicks++ <= placeDelay.getValInt()) {
            return BlockPos.ORIGIN;
        }

        placeTicks = 0;

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

        if(maxDamage == 0.5) {
            return BlockPos.ORIGIN;
        }

        final int crystalSlot = InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9);
        final int oldSlot = mc.player.inventory.currentItem;

        switch ((SwitchModes) switchMode.getValEnum()) {
            case None: {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    return BlockPos.ORIGIN;
                }
                break;
            }
            case Normal: {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    if(crystalSlot == -1) {
                        return BlockPos.ORIGIN;
                    } else {
                        InventoryUtil.switchToSlot(crystalSlot, false);
                    }
                }
                break;
            }
            case Silent: {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
                    if(crystalSlot == -1) {
                        return BlockPos.ORIGIN;
                    } else {
                        InventoryUtil.switchToSlot(crystalSlot, true);
                    }
                }
                break;
            }
        }

        if(mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            if (mc.player.isHandActive()) {
                hand = mc.player.getActiveHand();
            }

            rotateToPos(placePos, true);

            final EnumFacing facing = EnumFacing.UP;
            boolean offhand = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;

            if(raytrace.getValBoolean()) {
                //facing
            }

            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, facing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
            mc.playerController.updateController();

            if(placeSwing.getValBoolean()) {
                swingItem(false);
            }

            if (switchMode.getValEnum().equals(SwitchModes.Silent) && oldSlot != -1) {
                InventoryUtil.switchToSlot(oldSlot, true);
            }

            lastPlaceCrystal = new Crystal(placePos, maxDamage);

            if(debug.getValBoolean()) {
                ChatUtils.message("Placed crystal at: " + placePos.getX() + " " + placePos.getY() + " " + placePos.getZ());
            }

            return placePos;
        } else {
            return BlockPos.ORIGIN;
        }
    }

    private void breakCrystal() {
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

        if(breakHand.getValEnum().equals(Hands.None)) {
            mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        } else {
            mc.player.connection.sendPacket(new CPacketUseEntity(crystal, breakHand.getValEnum().equals(Hands.Mainhand) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
        }

        if(breakSwing.getValBoolean()) {
            swingItem(true);
        }

        if(oldSlot != -1 && weaknessSwitchMode.getValEnum().equals(SwitchModes.Silent)) {
            InventoryUtil.switchToSlot(oldSlot, true);
        }

        breakTicks = 0;
    }

    private void rotateToPos(BlockPos pos, boolean place) {
        boolean isOnRotate = placeRotate.getValBoolean() && breakRotate.getValBoolean();

        if(rotate.getValBoolean()) {
            if(placeRotate.getValBoolean() && place) {
                block0: {
                    final float[] angle = AngleUtil.calculateAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5f), (pos.getY() - 0.5f), (pos.getZ() + 0.5f)));
                    if (isOnRotate) {
                        RotationUtils.setPlayerRotations(angle[0], angle[1]);
                        break block0;
                    }
                    yaw = angle[0];
                    pitch = angle[1];
                    rotating = true;
                }
            }

            if(breakRotate.getValBoolean() && !place) {
                block0: {
                    final float[] angle = AngleUtil.calculateAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5f), (pos.getY() - 0.5f), (pos.getZ() + 0.5f)));
                    if (isOnRotate) {
                        RotationUtils.setPlayerRotations(angle[0], angle[1]);
                        break block0;
                    }
                    yaw = angle[0];
                    pitch = angle[1];
                    rotating = true;
                }
            }
        } else {
            rotating = false;
        }

        if(!isOnRotate) {
            rotating = false;
        }
    }

    private boolean isValidItemsInHand() {
        if(mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {return true;
        } else if(mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {return true;
        } else {return !switchMode.getValEnum().equals(SwitchModes.None);}
    }

    public void findNewTarget() {
        target = (EntityPlayer) getNearTarget(mc.player);
    }

    public EntityLivingBase getNearTarget(EntityPlayer distanceTarget) {
        return mc.world.loadedEntityList.stream()
                .filter(this::isValidTarget)
                .map(entity -> (EntityLivingBase) entity)
                .min(Comparator.comparing(distanceTarget::getDistance))
                .orElse(null);
    }

    public boolean isValidTarget(Entity entity) {
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

    private boolean isValidCrystal(Entity entity) {
        if(!(entity instanceof EntityEnderCrystal)) return false;
        if (entity.getDistance(mc.player) > (!mc.player.canEntityBeSeen(entity) ? wallRange.getValDouble() : breakRange.getValDouble())) return false;

        switch (breakMode.getValString()) {
            case "Always": return true;
            case "Smart":
                if (target == null) return false;

                float targetDMG = CrystalUtils.calculateDamage(mc.world, entity.posX + 0.5, entity.posY + 1.0, entity.posZ + 0.5, target, 0);
                float selfDMG = CrystalUtils.calculateDamage(mc.world, entity.posX + 0.5, entity.posY + 1.0, entity.posZ + 0.5, mc.player, 0);
                float minDMG = (float) this.minDMG.getValDouble();

                /// FacePlace
                if (target.getHealth() + target.getAbsorptionAmount() <= facePlaceHP.getValDouble())
                    minDMG = 1f;

                if (targetDMG > minDMG && selfDMG < maxSelfDMG.getValDouble()) return true;

                break;
            default: break;
        }

        return false;
    }

    public void swingItem(boolean breakSwing) {
        blockSwing: {
            if (breakSwing) {
                block0:{
                    block3:{
                        block1:{
                            block2:{
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
                block0:{
                    block3:{
                        block1:{
                            block2:{
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
        Outline,
        Fill,
        Both
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

    public enum DelayModes {
        First("Cooldown"),
        Second("Multiplier");

        private final String name;

        DelayModes(String name) {
            this.name = name;
        }

        public String getName() { return name; }
    }
}