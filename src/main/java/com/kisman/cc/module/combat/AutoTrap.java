package com.kisman.cc.module.combat;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import i.gishreloaded.gishcode.utils.TimerUtils;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.*;

public class AutoTrap extends Module {
    private Setting disableOnComplete = new Setting("DisableOnComplete", this, false);
    private Setting placeDelay = new Setting("Delay", this, 50, 0, 100, true);
    private Setting rotate = new Setting("Rotate", this, true);
    private Setting blocksPerTick = new Setting("BlocksPerTick", this, 8, 1, 30, true);
    private Setting antiScaffold = new Setting("AntiScaffold", this, false);
    private Setting antiStep = new Setting("AntiStep", this, false);
    private Setting range = new Setting("Range", this, 4, 1, 5, false);
    private Setting raytrace = new Setting("RayTrace", this, false);


    private TimerUtils timer = new TimerUtils();
    private Map<BlockPos, Integer> retries = new HashMap<>();
    private TimerUtils retryTimer = new TimerUtils();
    public EntityPlayer target;
    private boolean didPlace = false;
    private boolean switchedItem;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements = 0;
    private boolean smartRotate = false;
    private BlockPos startPos = null;
    private boolean isPlacing;

    public AutoTrap() {
        super("AutoTrap", "trapping all players", Category.COMBAT);

        setmgr.rSetting(disableOnComplete);
        setmgr.rSetting(placeDelay);
        setmgr.rSetting(rotate);
        setmgr.rSetting(blocksPerTick);
        setmgr.rSetting(antiScaffold);
        setmgr.rSetting(antiStep);
        setmgr.rSetting(range);
    }

    public void onEnable() {
        if(mc.player == null && mc.world == null) return;

        startPos = EntityUtil.getRoundedBlockPos(mc.player);
        lastHotbarSlot = mc.player.inventory.currentItem;
        retries.clear();
    }

    public void onDisable() {
        isPlacing = false;
        isSneaking = EntityUtil.stopSneaking(isSneaking);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        smartRotate = false;
        doTrap();
    }

    private void doTrap() {
        if(check()) {
            return;
        }

        doStaticTrap();

        if(didPlace) {
            timer.reset();
        }
    }

    private void doStaticTrap() {
        final List<Vec3d> placeTargets = BlockUtil.targets(this.target.getPositionVector(), this.antiScaffold.getValBoolean(), this.antiStep.getValBoolean(), false, false, false, this.raytrace.getValBoolean());
        this.placeList(placeTargets);
    }

    private void placeList(final List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(AutoTrap.mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), AutoTrap.mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        for (final Vec3d vec3d3 : list) {
            final BlockPos position = new BlockPos(vec3d3);
            final int placeability = BlockUtil.isPositionPlaceable(position, this.raytrace.getValBoolean());
            if (placeability == 1 && (this.retries.get(position) == null || this.retries.get(position) < 4)) {
                this.placeBlock(position);
                this.retries.put(position, (this.retries.get(position) == null) ? 1 : (this.retries.get(position) + 1));
                this.retryTimer.reset();
            }
            else {
                if (placeability != 3) {
                    continue;
                }
                this.placeBlock(position);
            }
        }
    }


    private boolean check() {
        isPlacing = false;
        didPlace = false;
        placements = 0;
        final int obbySlot2 = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        if (obbySlot2 == -1) {
            super.onDisable();
        }
        final int obbySlot3 = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        if (!super.isToggled()) {
            return true;
        }
        if (!startPos.equals(EntityUtil.getRoundedBlockPos(AutoTrap.mc.player))) {
            super.onDisable();
            return true;
        }
        if (retryTimer.passedMillis(2000L)) {
            retries.clear();
            retryTimer.reset();
        }
        if (obbySlot3 == -1) {
            ChatUtils.error(ChatFormatting.RED + "No Obsidian in hotbar, AutoTrap disabling...");
            super.onDisable();
            return true;
        }
        if (AutoTrap.mc.player.inventory.currentItem != this.lastHotbarSlot && AutoTrap.mc.player.inventory.currentItem != obbySlot3) {
            this.lastHotbarSlot = AutoTrap.mc.player.inventory.currentItem;
        }
        isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        target = (EntityPlayer) getNearTarget(mc.player);
        return target == null || !timer.passedMillis(placeDelay.getValInt());
    }

    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerTick.getValInt() && AutoTrap.mc.player.getDistanceSq(pos) <= MathUtil.square(5.0)) {
            isPlacing = true;
            final int originalSlot = AutoTrap.mc.player.inventory.currentItem;
            final int obbySlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
            final int eChestSot = InventoryUtil.findBlock(Blocks.ENDER_CHEST, 0, 9);
            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }
            if (this.smartRotate) {
                AutoTrap.mc.player.inventory.currentItem = ((obbySlot == -1) ? eChestSot : obbySlot);
                AutoTrap.mc.playerController.updateController();
                this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, true, this.isSneaking);
                AutoTrap.mc.player.inventory.currentItem = originalSlot;
                AutoTrap.mc.playerController.updateController();
            }
            else {
                AutoTrap.mc.player.inventory.currentItem = ((obbySlot == -1) ? eChestSot : obbySlot);
                AutoTrap.mc.playerController.updateController();
                this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, this.rotate.getValBoolean(), true, this.isSneaking);
                AutoTrap.mc.player.inventory.currentItem = originalSlot;
                AutoTrap.mc.playerController.updateController();
            }
            this.didPlace = true;
            ++this.placements;
        }
    }

    private EntityLivingBase getNearTarget(EntityPlayer distanceTarget) {
        return mc.world.loadedEntityList.stream()
                .filter(entity -> isValidTarget(entity))
                .map(entity -> (EntityLivingBase) entity)
                .min(Comparator.comparing(entity -> distanceTarget.getDistance(entity)))
                .orElse(null);
    }

    private boolean isValidTarget(Entity entity) {
        if (entity == null)
            return false;

        if (!(entity instanceof EntityLivingBase))
            return false;

        if (entity.isDead || ((EntityLivingBase)entity).getHealth() <= 0.0f)
            return false;

        if (entity.getDistance(mc.player) > range.getValDouble())
            return false;

        if (entity instanceof EntityPlayer) {
            if (entity == mc.player)
                return false;

            return true;
        }

        return false;
    }
}
