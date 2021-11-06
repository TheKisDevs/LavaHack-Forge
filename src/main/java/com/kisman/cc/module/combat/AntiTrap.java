package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.TimerUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class AntiTrap extends Module {
    public static AntiTrap instance;

    private Setting mode = new Setting("Mode", this, "MotionTick", new ArrayList<>(Arrays.asList("MotionTick", "ClientTick")));
    private Setting delay = new Setting("Delay", this, 400, 0, 1000, true);
    private Setting switchMode = new Setting("SwitchMode", this, SwitchModes.None);
    private Setting rotate = new Setting("Rotate", this, Rotate.NONE);
    private Setting sortY = new Setting("SortY", this, true);

    private TimerUtils timer = new TimerUtils();

    public Set<BlockPos> placedPos = new HashSet<>();
    private final Vec3d[] surroundTargets = new Vec3d[] { new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(-1.0, 1.0, 1.0) };

    private int lastHotbarSlot = -1;
    private boolean switchedItem;
    private boolean offhand = false;

    public AntiTrap() {
        super("AntiTrap", "", Category.COMBAT);

        instance = this;

        setmgr.rSetting(mode);
        setmgr.rSetting(delay);
        setmgr.rSetting(switchMode);
        setmgr.rSetting(rotate);
        setmgr.rSetting(sortY);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);

        if((mc.player == null && mc.world == null) || timer.passedMillis(delay.getValInt())) {
            super.onDisable();
            return;
        }

        lastHotbarSlot = mc.player.inventory.currentItem;
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);

        if((mc.player == null && mc.world == null)) {
            return;
        }

        switchItem();
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        if(mode.getValString().equalsIgnoreCase("ClientTick")) {
            doAntiTrap();
        }
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {
        if(event.getEra() == Event.Era.PRE && mode.getValString().equalsIgnoreCase("MotionTick")) {
            doAntiTrap();
        }
    });

    private void doAntiTrap() {
        final boolean offhand = AntiTrap.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        this.offhand = offhand;
        if (!this.offhand && InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9) == -1) {
            super.onDisable();
            return;
        }
        this.lastHotbarSlot = AntiTrap.mc.player.inventory.currentItem;
        final ArrayList<Vec3d> targets = new ArrayList<>();
        Collections.addAll(targets, BlockUtil.convertVec3ds(AntiTrap.mc.player.getPositionVector(), this.surroundTargets));
        final EntityPlayer closestPlayer = (EntityPlayer) getNearTarget(mc.player);
        if (closestPlayer != null) {
            final EntityPlayer entityPlayer;
            targets.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
            if (sortY.getValBoolean()) {
                targets.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
            }
        }
        for (final Vec3d vec3d3 : targets) {
            final BlockPos pos = new BlockPos(vec3d3);
            if (!CrystalUtils.canPlaceCrystal(pos)) {
                continue;
            }
            placeCrystal(pos);
            super.onDisable();
            break;
        }
    }

    private void placeCrystal(final BlockPos pos) {
        final boolean bl;
        final boolean mainhand = bl = (AntiTrap.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
        if (!mainhand && !this.offhand && !this.switchItem()) {
            super.onDisable();
            return;
        }
        final RayTraceResult result = AntiTrap.mc.world.rayTraceBlocks(new Vec3d(AntiTrap.mc.player.posX, AntiTrap.mc.player.posY + AntiTrap.mc.player.getEyeHeight(), AntiTrap.mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5));
        final EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        final float[] angle = AngleUtil.calculateAngle(AntiTrap.mc.player.getPositionEyes(AntiTrap.mc.getRenderPartialTicks()), new Vec3d((double)(pos.getX() + 0.5f), (double)(pos.getY() - 0.5f), (double)(pos.getZ() + 0.5f)));
        switch ((Rotate) this.rotate.getValEnum()) {
            case NORMAL: {
                RotationUtils.setPlayerRotations(angle[0], angle[1]);
                break;
            }
            case PACKET: {
                AntiTrap.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], (float) MathHelper.normalizeAngle((int)angle[1], 360), AntiTrap.mc.player.onGround));
                break;
            }
        }
        placedPos.add(pos);
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        mc.player.swingArm(EnumHand.MAIN_HAND);
        this.timer.reset();
    }

    private boolean switchItem() {
        if (offhand) {
            return true;
        }
        InventoryUtil.switchToSlot(InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9), this.switchMode.getValEnum().equals(SwitchModes.Silent));
        switchedItem = true;
        return true;
    }

    private EntityLivingBase getNearTarget(Entity distanceTarget) {
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

        if (entity.getDistance(mc.player) > 6)
            return false;

        if (entity instanceof EntityPlayer) {
            if (entity == mc.player)
                return false;

            return true;
        }

        return false;
    }

    public enum Rotate {
        NONE,
        NORMAL,
        PACKET
    }

    public enum SwitchModes {
        None,
        Normal,
        Silent
    }
}
