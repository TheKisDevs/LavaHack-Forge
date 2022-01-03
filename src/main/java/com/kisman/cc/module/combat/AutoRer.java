package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.mixin.mixins.accessor.AccessorCPacketUseEntity;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.CrystalUtils;
import com.kisman.cc.util.EntityUtil;
import com.kisman.cc.util.InventoryUtil;
import com.kisman.cc.util.RenderUtil;
import i.gishreloaded.gishcode.utils.TimerUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AutoRer extends Module {
    private final Setting placeRange = new Setting("Place Range", this, 6, 0, 6, false);
    private final Setting breakRange = new Setting("Break Range", this, 6, 0, 6, false);
    private final Setting breakWallRange = new Setting("Break Wall Range", this, 4.5f, 0, 6, false);
    private final Setting targetRange = new Setting("Target Range", this, 9, 0, 16, false);
    private final Setting terrain = new Setting("Terrain", this, false);
    private final Setting switch_ = new Setting("Switch", this, SwitchMode.None);
    private final Setting fastCalc = new Setting("Fast Calc", this, true);
    private final Setting swing = new Setting("Swing", this, SwingMode.PacketSwing);
    private final Setting instant = new Setting("Instant", this, true);
    private final Setting inhibit = new Setting("Inhibit", this, true);
    private final Setting syns = new Setting("Syns", this, true);

    private final Setting placeLine = new Setting("PlaceLine", this, "Place");
    private final Setting place = new Setting("Place", this, true);
    private final Setting secondCheck = new Setting("Second Check", this, false);
    private final Setting armorBreaker = new Setting("Armor Breaker", this, 100, 0, 100, Slider.NumberType.PERCENT);

    private final Setting breakLine = new Setting("BreakLine", this, "Break");
    private final Setting break_ = new Setting("Break", this, true);

    private final Setting delayLine = new Setting("DelayLine", this, "Delay");
    private final Setting placeDelay = new Setting("Place Delay", this, 0, 0, 2000, Slider.NumberType.TIME);
    private final Setting breakDelay = new Setting("Break Delay", this, 0, 0, 2000, Slider.NumberType.TIME);
    private final Setting calcDelay = new Setting("Calc Delay", this, 0, 0, 20000, Slider.NumberType.TIME);
    private final Setting clearDelay = new Setting("Clear Delay", this, 500, 0, 2000, Slider.NumberType.TIME);

    private final Setting dmgLine = new Setting("DMGLine", this, "Damage");
    private final Setting minDMG = new Setting("MinDMG", this, 6, 0, 37, true);
    private final Setting maxSelfDMG = new Setting("MaxSelfDMG", this, 18, 0, 37, true);
    private final Setting lethalMult = new Setting("LethalMult", this, 0, 0, 6, false);

    private final Setting renderLine = new Setting("RenderLine", this, "Render");
    private final Setting render = new Setting("Render", this, Render.Default);
    private final Setting text = new Setting("Text", this, true);
    private final Setting infoMode = new Setting("InfoMode", this, InfoMode.Target);

    private final Setting red = new Setting("Red", this, 1, 0, 1, false);
    private final Setting green = new Setting("Green", this, 0, 0, 1, false);
    private final Setting blue = new Setting("Blue", this, 0, 0, 1, false);
    private final Setting alpha = new Setting("Blue", this, 1, 0, 1, false);

    private final Setting advancedRenderLine = new Setting("AdvancedRenderLine", this, "Advanced render");

    private final Setting startRed = new Setting("Start Red", this, 0, 0, 1, false);
    private final Setting startGreen = new Setting("Start Green", this, 0, 0, 1, false);
    private final Setting startBlue = new Setting("Start Blue", this, 0, 0, 1, false);
    private final Setting startAlpha = new Setting("Start Alpha", this, 0, 0, 1, false);

    private final Setting endRed = new Setting("End Red", this, 1, 0, 1, false);
    private final Setting endGreen = new Setting("End Green", this, 0, 0, 1, false);
    private final Setting endBlue = new Setting("End Blue", this, 0, 0, 1, false);
    private final Setting endAlpha = new Setting("End Alpha", this, 1, 0, 1, false);

    private final List<BlockPos> placedList = new ArrayList<>();
    private final TimerUtils placeTimer = new TimerUtils();
    private final TimerUtils breakTimer = new TimerUtils();
    private final TimerUtils calcTimer = new TimerUtils();
    private final TimerUtils renderTimer = new TimerUtils();
    public static EntityPlayer currentTarget;
    private BlockPos renderPos;
    private BlockPos placePos;
    private Entity lastHitEntity = null;
    private double renderDamage;
    public boolean rotating;
    private float pitch;
    private float yaw;
    private int rotationPacketsSpoofed;

    public AutoRer() {
        super("AutoRer", Category.COMBAT);

        setmgr.rSetting(placeRange);
        setmgr.rSetting(breakRange);
        setmgr.rSetting(breakWallRange);
        setmgr.rSetting(targetRange);
        setmgr.rSetting(terrain);
        setmgr.rSetting(switch_);
        setmgr.rSetting(fastCalc);
        setmgr.rSetting(swing);
        setmgr.rSetting(instant);
        setmgr.rSetting(inhibit);
        setmgr.rSetting(syns);

        setmgr.rSetting(placeLine);
        setmgr.rSetting(place);
        setmgr.rSetting(secondCheck);
        setmgr.rSetting(armorBreaker);

        setmgr.rSetting(breakLine);
        setmgr.rSetting(break_);

        setmgr.rSetting(delayLine);
        setmgr.rSetting(placeDelay);
        setmgr.rSetting(breakDelay);
        setmgr.rSetting(calcDelay);
        setmgr.rSetting(clearDelay);

        setmgr.rSetting(dmgLine);
        setmgr.rSetting(minDMG);
        setmgr.rSetting(maxSelfDMG);
        setmgr.rSetting(lethalMult);

        setmgr.rSetting(renderLine);
        setmgr.rSetting(render);
        setmgr.rSetting(text);
        setmgr.rSetting(infoMode);
        setmgr.rSetting(red);
        setmgr.rSetting(green);
        setmgr.rSetting(blue);
        setmgr.rSetting(alpha);

        setmgr.rSetting(advancedRenderLine);
        setmgr.rSetting(startRed);
        setmgr.rSetting(startGreen);
        setmgr.rSetting(startBlue);
        setmgr.rSetting(startAlpha);
        setmgr.rSetting(endRed);
        setmgr.rSetting(endGreen);
        setmgr.rSetting(endBlue);
        setmgr.rSetting(endAlpha);
    }

    public void onEnable() {
        placedList.clear();
        breakTimer.reset();
        placeTimer.reset();
        renderTimer.reset();
        currentTarget = null;
        renderPos = null;
        rotating = false;

        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);

        placedList.clear();
        breakTimer.reset();
        placeTimer.reset();
        renderTimer.reset();
        currentTarget = null;
        renderPos = null;
        rotating = false;
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        if(renderTimer.passedMillis(clearDelay.getValLong())) {
            placedList.clear();
            renderPos = null;
            renderTimer.reset();
        }

        currentTarget = EntityUtil.getTarget(targetRange.getValFloat());

        if(currentTarget == null) return;
        else super.setDisplayInfo("[" + currentTarget.getName() + "]");
        if(fastCalc.getValBoolean() && calcTimer.passedMillis(calcDelay.getValLong())) {
            calculatePlace();
            calcTimer.reset();
        }

        doPlace();
        doBreak();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        switch (render.getValString()) {
            case "None": break;
            case "Default": {
                RenderUtil.drawBlockESP(renderPos, red.getValFloat(), green.getValFloat(), blue.getValFloat());
                break;
            }
            case "Advanced": {
                RenderUtil.drawGradientFilledBox(renderPos, new Color(startRed.getValFloat(), startGreen.getValFloat(), startBlue.getValFloat(), startAlpha.getValFloat()), new Color(endRed.getValFloat(), endGreen.getValFloat(), endBlue.getValFloat(), endAlpha.getValFloat()));
                break;
            }
        }

        if(text.getValBoolean()) RenderUtil.drawText(renderPos, ((Math.floor(renderDamage) == renderDamage) ? String.valueOf(Integer.valueOf((int) renderDamage)) : String.format("%.1f", renderDamage)));
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketSpawnObject && instant.getValBoolean()) {
            SPacketSpawnObject packet =  (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51) {
                BlockPos toRemove = null;
                for (BlockPos pos : placedList) {
                    boolean canSee = EntityUtil.canSee(pos);
                    if (mc.player.getDistance(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) >= (canSee ? breakRange.getValDouble() : breakWallRange.getValDouble())) break;

                    toRemove = pos;
                    if (inhibit.getValBoolean()) try {lastHitEntity = mc.world.getEntityByID(packet.getEntityID());} catch (Exception ignored) {}

                    AccessorCPacketUseEntity hitPacket = (AccessorCPacketUseEntity) new CPacketUseEntity();
                    hitPacket.setEntityId(packet.getEntityID());
                    hitPacket.setAction(CPacketUseEntity.Action.ATTACK);
                    mc.player.connection.sendPacket((CPacketUseEntity) hitPacket);
                    swing();
                    break;
                }
                if (toRemove != null) placedList.remove(toRemove);
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect && inhibit.getValBoolean() && lastHitEntity != null) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) if (lastHitEntity.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) lastHitEntity.setDead();
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Send> listener1 = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItem(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getHand()).getItem() == Items.END_CRYSTAL) {
            placedList.add(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getPos());
        }
    });

    private void calculatePlace() {
        double maxDamage = 0.5;
        BlockPos placePos = null;
        List<BlockPos> sphere = CrystalUtils.getSphere(placeRange.getValFloat(), true, false);

        for(int size = sphere.size(), i = 0; i < size; ++i) {
            BlockPos pos = sphere.get(i);

            if(CrystalUtils.canPlaceCrystal(pos, secondCheck.getValBoolean())) {
                float targetDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, currentTarget, terrain.getValBoolean());

                if(targetDamage > minDMG.getValInt() || targetDamage * lethalMult.getValDouble() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || InventoryUtil.isArmorUnderPercent(currentTarget, armorBreaker.getValInt())) {
                    float selfDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player, terrain.getValBoolean());

                    if(selfDamage <= maxSelfDMG.getValInt() && selfDamage + 2 < mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage) {
                        if(maxDamage <= targetDamage) {
                            maxDamage = targetDamage;
                            placePos = pos;
                            renderPos = pos;
                            renderDamage = targetDamage;
                        }
                    }
                }
            }
        }

        this.placePos = placePos;
    }

    private void doPlace() {
        if(!place.getValBoolean() || !placeTimer.passedMillis(placeDelay.getValLong()) || (placePos == null && fastCalc.getValBoolean())) return;

        if(!fastCalc.getValBoolean()) {
            calculatePlace();

            if(placePos == null) return;
        }

        if(syns.getValBoolean() && placedList.contains(placePos)) return;

        EnumHand hand = null;

        int oldSlot = mc.player.inventory.currentItem;
        int crystalSlot = InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9);

        if(crystalSlot == -1) return;

        switch (switch_.getValString()) {
            case "None": if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) return;
            case "Normal": {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    InventoryUtil.switchToSlot(crystalSlot, false);
                } break;
            }
            case "Silent": {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    InventoryUtil.switchToSlot(crystalSlot, true);
                } break;
            }
        }

        if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) return;

        boolean offhand = mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL);

        if(mc.player.isHandActive()) hand = mc.player.getActiveHand();

        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + ( double ) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(( double ) placePos.getX() + 0.5, ( double ) placePos.getY() - 0.5, ( double ) placePos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, facing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
        mc.player.connection.sendPacket(new CPacketAnimation(swing.getValString().equals(SwingMode.MainHand.name()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
        placeTimer.reset();
        renderPos = placePos;

        if(hand != null) mc.player.setActiveHand(hand);
        if(oldSlot != -1 && switch_.getValString().equals(SwitchMode.Silent.name())) InventoryUtil.switchToSlot(oldSlot, true);
    }

    private void doBreak() {
        if(!break_.getValBoolean() || !breakTimer.passedMillis(breakDelay.getValLong())) return;

        Entity crystal = null;
        double maxDamage = 0.5;

        for(int i = 0; i < mc.world.loadedEntityList.size(); ++i) {
            Entity entity = mc.world.loadedEntityList.get(i);

            if(entity instanceof EntityEnderCrystal && mc.player.getDistance(entity) < (mc.player.canEntityBeSeen(entity) ? breakRange.getValDouble() : breakWallRange.getValDouble())) {
                float targetDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, currentTarget, terrain.getValBoolean());

                if(targetDamage > minDMG.getValInt() || targetDamage * lethalMult.getValDouble() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || InventoryUtil.isArmorUnderPercent(currentTarget, armorBreaker.getValInt())) {
                    float selfDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, mc.player, terrain.getValBoolean());

                    if(selfDamage <= maxSelfDMG.getValInt() && selfDamage + 2 <= mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage) {
                        if(maxDamage <= targetDamage) {
                            maxDamage = targetDamage;
                            crystal = entity;
                        }
                    }
                }
            }
        }

        if(crystal == null) return;

        lastHitEntity = crystal;
        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        swing();
        breakTimer.reset();

        BlockPos toRemove = null;

        if(syns.getValBoolean()) for(BlockPos pos : placedList) if(crystal.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= 3) toRemove = pos;
        if(toRemove != null) placedList.remove(toRemove);
    }

    private void swing() {
        if(swing.getValString().equals(SwingMode.PacketSwing.name())) mc.player.connection.sendPacket(new CPacketAnimation(swing.getValString().equals(SwingMode.MainHand.name()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
        else mc.player.swingArm(swing.getValString().equals(SwingMode.MainHand.name()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
    }

    public enum Render {
        None,
        Default,
        Advanced
    }

    public enum InfoMode {
        Target,
        Damage,
        Both
    }

    public enum Rotate {
        Off,
        Place,
        Break,
        All
    }

    public enum Raytrace {
        None,
        Place,
        Break,
        Both
    }

    public enum SwitchMode {
        None,
        Normal,
        Silent
    }

    public enum SwingMode {
        MainHand,
        OffHand,
        PacketSwing
    }
}
