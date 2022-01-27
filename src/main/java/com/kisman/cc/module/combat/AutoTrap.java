package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.module.client.Config;
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
    public static AutoTrap instance;
    private Setting targetRange = new Setting("Target Range", this, 10, 1, 20, true);
    private Setting disableOnComplete = new Setting("DisableOnComplete", this, false);
    private Setting placeDelay = new Setting("Delay", this, 50, 0, 100, true);
    private Setting rotate = new Setting("Rotate", this, true);
    private Setting blocksPerTick = new Setting("BlocksPerTick", this, 8, 1, 30, true);
    private Setting antiScaffold = new Setting("AntiScaffold", this, false);
    private Setting antiStep = new Setting("AntiStep", this, false);
    private Setting surroundPlacing = new Setting("SurroundPlacing", this, true);
    private Setting range = new Setting("Range", this, 4, 1, 5, false);
    private Setting raytrace = new Setting("RayTrace", this, false);

    private TimerUtils timer = new TimerUtils();
    private Map<BlockPos, Integer> retries = new HashMap<>();
    private TimerUtils retryTimer = new TimerUtils();
    public EntityPlayer target;
    private boolean didPlace = false;
    private boolean isSneaking;
    private int oldSlot;
    private int placements = 0;
    private boolean smartRotate = false;
    private BlockPos startPos = null;

    public AutoTrap() {
        super("AutoTrap", "trapping all players", Category.COMBAT);
        super.setToggled(false);

        instance = this;

        setmgr.rSetting(targetRange);
        setmgr.rSetting(disableOnComplete);
        setmgr.rSetting(placeDelay);
        setmgr.rSetting(rotate);
        setmgr.rSetting(blocksPerTick);
        setmgr.rSetting(antiScaffold);
        setmgr.rSetting(antiStep);
        setmgr.rSetting(surroundPlacing);
        setmgr.rSetting(range);
        setmgr.rSetting(raytrace);
    }

    public void onEnable() {
        if(mc.player == null && mc.world == null) return;

        startPos = EntityUtil.getRoundedBlockPos(mc.player);
        oldSlot = mc.player.inventory.currentItem;
        retries.clear();
    }

    public void onDisable() {
        isSneaking = EntityUtil.stopSneaking(isSneaking);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        smartRotate = false;
        doTrap();
    }

    private void doTrap() {
        if(check()) return;
        doStaticTrap();
        if(didPlace) timer.reset();
    }

    private void doStaticTrap() {
        final List<Vec3d> placeTargets = BlockUtil.targets(target.getPositionVector(), antiScaffold.getValBoolean(), antiStep.getValBoolean(), surroundPlacing.getValBoolean(), false, false, this.raytrace.getValBoolean());
        placeList(placeTargets);
    }

    private void placeList(final List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        for (final Vec3d vec3d3 : list) {
            final BlockPos position = new BlockPos(vec3d3);
            final int placeability = BlockUtil.isPositionPlaceable(position, this.raytrace.getValBoolean());
            if (placeability == 1 && (this.retries.get(position) == null || this.retries.get(position) < 4)) {
                this.placeBlock(position);
                this.retries.put(position, (this.retries.get(position) == null) ? 1 : (this.retries.get(position) + 1));
                this.retryTimer.reset();
            } else {
                if (placeability != 3) continue;
                this.placeBlock(position);
            }
        }
    }

    private boolean check() {
        if(mc.player == null) return false;
        if(startPos == null) return false;

        didPlace = false;
        placements = 0;
        final int obbySlot2 = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        if (obbySlot2 == -1) setToggled(false);
        final int obbySlot3 = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        if (!super.isToggled()) return true;
        if (!startPos.equals(EntityUtil.getRoundedBlockPos(mc.player))) {
            setToggled(false);
            return true;
        }
        if (retryTimer.passedMillis(2000L)) {
            retries.clear();
            retryTimer.reset();
        }
        if (obbySlot3 == -1) {
            ChatUtils.error(ChatFormatting.RED + "No Obsidian in hotbar, AutoTrap disabling...");
            setToggled(false);
            return true;
        }
        if (mc.player.inventory.currentItem != this.oldSlot && mc.player.inventory.currentItem != obbySlot3) {
            this.oldSlot = mc.player.inventory.currentItem;
        }
        isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        target = EntityUtil.getTarget(targetRange.getValFloat());
        return target == null || !timer.passedMillis(placeDelay.getValInt());
    }

    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerTick.getValInt() && mc.player.getDistanceSq(pos) <= MathUtil.square(5.0)) {
            final int originalSlot = mc.player.inventory.currentItem;
            final int obbySlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
            final int eChestSot = InventoryUtil.findBlock(Blocks.ENDER_CHEST, 0, 9);

            if (obbySlot == -1 && eChestSot == -1) this.toggle();
            if (this.smartRotate) {
                mc.player.inventory.currentItem = ((obbySlot == -1) ? eChestSot : obbySlot);
                mc.playerController.updateController();
                isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), true, isSneaking);
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
            } else {
                mc.player.inventory.currentItem = ((obbySlot == -1) ? eChestSot : obbySlot);
                mc.playerController.updateController();
                isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, this.rotate.getValBoolean(), rotate.getValBoolean(), isSneaking);
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
            }

            this.didPlace = true;
            ++this.placements;
        }
    }
}
