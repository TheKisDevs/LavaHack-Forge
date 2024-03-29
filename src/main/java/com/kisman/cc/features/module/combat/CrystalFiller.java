package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.features.subsystem.subsystems.EnemyManagerKt;
import com.kisman.cc.features.subsystem.subsystems.Target;
import com.kisman.cc.features.subsystem.subsystems.Targetable;
import com.kisman.cc.features.subsystem.subsystems.TargetsNearest;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.util.SlideRenderingRewritePattern;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.world.CrystalUtils;
import com.kisman.cc.util.world.WorldUtilKt;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;

@Targetable
@TargetsNearest
public class CrystalFiller extends Module {
    private final Setting range = register(new Setting("Range", this, 4.8, 1, 6, false));
    private final Setting rayTrace = register(new Setting("Ray Trace", this, true));
    private final Setting placeMode = register(new Setting("PlaceMode", this, PlaceMode.Always));
    private final Setting delay = register(new Setting("Delay", this, 2, 0, 20, true));
    private final Setting targetHoleRange = register(new Setting("TargetHoleRange", this, 4.8, 1, 6, false));
    private final Setting switchMode = register(new Setting("SwitchMode", this, SwitchMode.Silent));
    private final Setting crystalDMGCheck = register(new Setting("CrystalDMGCheck", this, false));
    private final Setting minDMG = register(new Setting("MinDMG", this, 5, 0, 36, true));
    private final Setting maxSelfDMG = register(new Setting("MaxSelfDMG", this, 15, 0, 36, true));

    private final SettingGroup rendererGroup = register(new SettingGroup(new Setting("Renderer", this)));
    private final SlideRenderingRewritePattern renderer = new SlideRenderingRewritePattern(this).group(rendererGroup).preInit().init();

    @ModuleInstance
    public static CrystalFiller instance;

    private final ArrayList<Hole> holes = new ArrayList<>();
    private final ArrayList<Hole> blackHoleList = new ArrayList<>();
    private final ArrayList<EntityEnderCrystal> crystals = new ArrayList<>();
    private int delayTicks = 0;

    private BlockPos renderPos = null;
    @Target
    public EntityPlayer target;
    public Hole targetHole;

    public CrystalFiller() {
        super("CrystalFiller", "HoleFiller but crystal", Category.COMBAT);
        super.setDisplayInfo(() -> "[" + (target == null ? "no target no fun" : target.getName()) + "]");
    }

    public void onEnable() {
        super.onEnable();
        target = null;
        targetHole = null;
        delayTicks = 0;
        holes.clear();
        blackHoleList.clear();
        crystals.clear();
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        target = EnemyManagerKt.nearest();

        if(target == null) return;

        renderPos = null;
        doCrystalFiller();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        renderer.draw(renderPos);
    }

    private void doCrystalFiller() {
        if (target == null && placeMode.getValString().equals(PlaceMode.Smart.name())) target = EnemyManagerKt.nearest();
        else if(delayTicks++ > delay.getValInt()){
            findHoles(mc.player, (float) range.getValDouble());
            findTargetHole();

            if (targetHole != null) {
                final int crystalSlot = InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9);
                final int oldSlot = mc.player.inventory.currentItem;

                switch (switchMode.getValString()) {
                    case "None" : {
                        if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) return;
                        break;
                    }
                    case "Normal" : {
                        if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL && crystalSlot != -1) InventoryUtil.switchToSlot(crystalSlot, false);
                        break;
                    }
                    case "Silent" : {
                        if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL && crystalSlot != -1) InventoryUtil.switchToSlot(crystalSlot, true);
                        break;
                    }
                }

                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) return;

                RayTraceResult result = null;

                if(rayTrace.getValBoolean()) result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) targetHole.pos.getX() + Double.longBitsToDouble(Double.doubleToLongBits(2.0585482766064893) ^ 9214496676598388901L), (double) targetHole.getDownHoleBlock().getY() - Double.longBitsToDouble(Double.doubleToLongBits(18.64274749914699) ^ 9210605105263438863L), (double) this.targetHole.pos.getZ() + Double.longBitsToDouble(Double.doubleToLongBits(3.2686479786919134) ^ 9217221578882085433L)));

                final EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
                final boolean offhand = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(targetHole.getDownHoleBlock(), facing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                mc.playerController.updateController();

                renderPos = targetHole.getDownHoleBlock();

                if (switchMode.getValString().equals(SwitchMode.Silent.name()) && oldSlot != -1) InventoryUtil.switchToSlot(oldSlot, true);

                delayTicks = 0;
            }
        }
    }

    private void findTargetHole() {
        targetHole = getNearHole();
    }

    private Hole getNearHole() {
        return holes.stream()
                .filter(this::isValidHole)
                .min(Comparator.comparing(hole -> mc.player.getDistanceSq(hole.pos)))
                .orElse(null);
    }

    private void findHoles(EntityPlayer player, float range) {
        holes.clear();
        for(BlockPos pos : WorldUtilKt.sphere(player, (int) range)) if(mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) if(isBlockHole(pos)) holes.add(new Hole(pos, (float) mc.player.getDistanceSq(pos), (float) player.getDistanceSq(pos)));
    }

    private boolean isValidHole(Hole hole) {
        if(mc.player.getDistance(hole.pos.getX(), hole.pos.getY(), hole.pos.getZ()) > range.getValDouble()) return false;
        if(placeMode.getValString().equals(PlaceMode.Smart.name()) && target != null) {
            if(target.getDistance(hole.pos.getX(), hole.pos.getY(), hole.pos.getZ()) > targetHoleRange.getValDouble()) return false;
            if(crystalDMGCheck.getValBoolean()) {
                final float tempDMG = CrystalUtils.calculateDamage(mc.world, hole.pos, target);
                final float tempSelfDMG = CrystalUtils.calculateDamage(mc.world, hole.pos, mc.player);

                if (tempDMG < minDMG.getValDouble() || tempSelfDMG > maxSelfDMG.getValDouble()) return false;
            }
        }

        return mc.world.getBlockState(hole.pos.up(1)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(hole.pos.up(2)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(hole.pos.up(3)).getBlock().equals(Blocks.AIR);
        //TODO: update hole validation!!!
        //TODO: added a crystal check!!!
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

        return holeblocks >= 9;
    }

    public enum PlaceMode {
        Always,
        Smart
    }

    public enum SwitchMode {
        None,
        Normal,
        Silent
    }

    public static class Hole {
        public BlockPos pos;
        public float distToPlayer;
        public float distToTarget;

        public Hole(BlockPos pos, float distToPlayer, float distToTarget) {
            this.pos = pos;
            this.distToPlayer = distToPlayer;
            this.distToTarget = distToTarget;
        }

        public BlockPos getDownHoleBlock() {
            if(pos == null) return BlockPos.ORIGIN;

            return pos.down();
        }
    }
}
