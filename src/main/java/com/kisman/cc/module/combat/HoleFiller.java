package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.combat.holefiller.Hole;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.BlockUtil;
import com.kisman.cc.util.CrystalUtils;
import com.kisman.cc.util.InventoryUtil;
import com.kisman.cc.util.WorldUtil;
import i.gishreloaded.gishcode.utils.TimerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Comparator;

public class HoleFiller extends Module {
    private Setting range = new Setting("Range", this, 4.8, 1, 6, false);
    private Setting placeMode = new Setting("PlaceMode", this, PlaceMode.Always);
    private Setting delay = new Setting("Delay", this, 2, 0, 20, true);
    private Setting targetMode = new Setting("UpdateMode", this, TargetMode.Update);
    private Setting targetHoleRange = new Setting("TargetHoleRange", this, 4.8, 1, 6, false);
    private Setting switchMode = new Setting("SwitchMode", this, SwitchMode.Silent);
    private Setting smartWeb = new Setting("SmartWeb", this, false);

    public static HoleFiller instance;

    private ArrayList<Hole> holes = new ArrayList<>();
    private int delayTick = 0;

    public EntityPlayer target;
    public Hole targetHole;

    public HoleFiller() {
        super("HoleFiller", "HoleFiller", Category.COMBAT);

        instance = this;

        setmgr.rSetting(range);
        setmgr.rSetting(placeMode);
        setmgr.rSetting(delay);
        setmgr.rSetting(targetMode);
        setmgr.rSetting(targetHoleRange);
        setmgr.rSetting(switchMode);
        setmgr.rSetting(smartWeb);
    }

    public void onEnable() {
        target = null;
        targetHole = null;
        delayTick = 0;
        holes.clear();
    }

    public void onDisable() {
        target = null;
        targetHole = null;
        delayTick = 0;
        holes.clear();
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;
        
        if(target == null) {
            super.setDisplayInfo("");
        } else {
            super.setDisplayInfo(TextFormatting.GRAY + "[" + TextFormatting.WHITE + target.getDisplayName() + TextFormatting.GRAY + "]");
        }

        if(target == null && placeMode.getValEnum().equals(PlaceMode.Smart)) {
            findNewTarget();
        } else {//if(timer.passedMillis(delay.getValInt())){
            findHoles(mc.player, (float) range.getValDouble());
            findTargetHole();

            if(targetHole != null) {
                final int obbySlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
                final int webSlot = InventoryUtil.findBlock(Blocks.WEB, 0, 9);
                final int oldSlot = mc.player.inventory.currentItem;

                switch ((SwitchMode) switchMode.getValEnum()) {
                    case None: {
                        if (obbySlot == -1) {
                            return;
                        }
                        break;
                    }
                    case Normal: {
                        if(obbySlot != -1) {
                            InventoryUtil.switchToSlot(obbySlot, false);
                        } else {
                            return;
                        }

                        break;
                    }
                    case Silent: {
                        if(obbySlot != -1) {
                            InventoryUtil.switchToSlot(obbySlot, true);
                        } else {
                            return;
                        }

                        break;
                    }
                }

                BlockUtil.placeBlock(targetHole.pos);

                if(mc.world.getBlockState(targetHole.pos).getBlock() != Blocks.OBSIDIAN || mc.world.getBlockState(targetHole.pos).getBlock() != Blocks.WEB) {
                    switch ((SwitchMode) switchMode.getValEnum()) {
                        case Normal: {
                            InventoryUtil.switchToSlot(webSlot, false);
                            break;
                        }
                        case Silent: {
                            InventoryUtil.switchToSlot(webSlot, true);
                        }
                    }

                    BlockUtil.placeBlock(targetHole.pos);
                }

                if(switchMode.getValEnum().equals(SwitchMode.Silent) && oldSlot != -1) {
                    InventoryUtil.switchToSlot(oldSlot, true);
                }

//                timer.reset();
            }
        }
    }

    private void findTargetHole() {
        targetHole = getNearHole();
    }

    private Hole getNearHole() {
        return holes.stream()
                .filter(hole -> isValidHole(hole))
                .min(Comparator.comparing(hole -> mc.player.getDistanceSq(hole.pos)))
                .orElse(null);
    }

    private void findHoles(EntityPlayer player, float range) {
        holes.clear();

        for(BlockPos pos : CrystalUtils.getSphere(player, range, true, false)) {
            if(mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                if(isBlockHole(pos)) {
                    holes.add(new Hole(pos, (float) mc.player.getDistanceSq(pos), (float) player.getDistanceSq(pos)));
                }
            }
        }
    }

    private void findNewTarget() {
        target = getNearTarget(mc.player);
    }

    private EntityPlayer getNearTarget(EntityPlayer distanceTarget) {
        return (EntityPlayer) mc.world.loadedEntityList.stream()
                .filter(entity -> isValidTarget(entity))
                .map(entity -> (EntityLivingBase) entity)
                .min(Comparator.comparing(entity -> distanceTarget.getDistance(entity)))
                .orElse(null);
    }

    private boolean isValidHole(Hole hole) {
        if(mc.player.getDistanceSq(hole.pos) > range.getValDouble()) return false;

        if(placeMode.getValEnum().equals(PlaceMode.Smart)) {
            if(WorldUtil.getDistance(target, hole.pos) > targetHoleRange.getValDouble()) {
                return false;
            }
        }

        if(!mc.world.getBlockState(hole.pos.up(1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(hole.pos.up(2)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(hole.pos.up(3)).getBlock().equals(Blocks.AIR)) return false;
        //TODO: update hole validation!!!
        //TODO: added a crystal check!!!

        return true;
    }

    private boolean isBlockHole(BlockPos blockpos) {
        int holeblocks = 0;

        if (mc.world.getBlockState(blockpos.add(0, 3, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 2, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 1, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;

        if (holeblocks >= 9) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidTarget(Entity entity) {
        if(!(entity instanceof EntityPlayer)) return false;
        if(entity == mc.player) return false;
        if(((EntityPlayer) entity).getHealth() < 0) return false;
        if(entity.getDistance(mc.player) > range.getValDouble()) return false;

        return true;
    }

    public enum PlaceMode {
        Always,
        Smart
    }

    public enum TargetMode {
        Update,
        Motion
    }

    public enum SwitchMode {
        None,
        Normal,
        Silent
    }
}