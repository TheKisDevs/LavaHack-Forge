package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.combat.holefillerrewrite.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.settings.util.RenderingRewritePattern;
import com.kisman.cc.util.Timer;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.entity.TargetFinder;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.world.BlockUtil;
import com.kisman.cc.util.world.HoleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

/**
 * Work in progress
 * @author Cubic
 */
public class HoleFillerRewrite extends Module {
    public static HoleFillerRewrite instance;
    
    private final SettingGroup logic = register(new SettingGroup(new Setting("Logic", this)));
    private final SettingGroup render_ = register(new SettingGroup(new Setting("Render", this)));

    private final Setting obsidianHoles = register(logic.add(new Setting("ObsidianHoles", this, true)));
    private final Setting bedrockHoles = register(logic.add(new Setting("BedrockHoles", this, true)));
    private final Setting singleHoles = register(logic.add(new Setting("SingleHoles", this, true)));
    private final Setting doubleHoles = register(logic.add(new Setting("DoubleHoles", this, true)));
    private final Setting customHoles = register(logic.add(new Setting("CustomHoles", this, true)));
    private final Setting blocks = register(logic.add(new Setting("Blocks", this, "Obsidian", Arrays.asList("Obsidian", "EnderChest"))));
    private final Setting swap = register(logic.add(new Setting("Switch", this, "Silent", Arrays.asList("None", "Vanilla", "Normal", "Packet", "Silent"))));
    private final Setting rotate = register(logic.add(new Setting("Rotate", this, false)));
    private final Setting packet = register(logic.add(new Setting("Packet", this, false)));
    private final Setting place = register(logic.add(new Setting("Place", this, "Instant", Arrays.asList("Instant", "Tick", "Delay"))));
    private final Setting delay = register(logic.add(new Setting("DelayMS", this, 50, 0, 500, true).setVisible(() -> place.getValString().equals("Delay"))));
    private final Setting placeMode = register(logic.add(new Setting("PlaceMode", this, "All", Arrays.asList("All", "Target"))));
    private final Setting enemyRange = register(logic.add(new Setting("TargetRange", this, 10, 1, 15, false).setVisible(() -> placeMode.getValString().equals("Target"))));
    private final Setting aroundEnemyRange = register(logic.add(new Setting("TargetHoleRange", this, 4, 1, 10, false).setVisible(() -> placeMode.getValString().equals("Target"))));
    private final Setting holeRange = register(logic.add(new Setting("HoleRange", this, 5, 1, 10, false)));
    private final Setting limit = register(logic.add(new Setting("Limit", this, 0, 0, 50, true)));

    private final Setting render = register(render_.add(new Setting("Render", this, true)));
    private final RenderingRewritePattern renderer_ = new RenderingRewritePattern(this, render::getValBoolean, null, render_).preInit();
    private final Setting movingLength = register(render_.add(new Setting("Moving Length", this, 400, 0, 1000, NumberType.TIME).setVisible(render::getValBoolean)));
    private final Setting fadeLength = register(render_.add(new Setting("Fade Length", this, 200, 0, 1000, NumberType.TIME).setVisible(render::getValBoolean)));

    private final MultiThreaddableModulePattern threads = new MultiThreaddableModulePattern(this);
    private final TargetFinder targets = new TargetFinder(enemyRange::getValDouble, () -> threads.getDelay().getValLong(), threads.getMultiThread()::getValBoolean);

    private final HoleFillerRewriteRenderer renderer = new HoleFillerRewriteRenderer();

    private Entity entity = null;

    public HoleFillerRewrite(){
        super("HoleFillerRewrite", Category.COMBAT);
        super.setDisplayInfo(() -> entity == null ? "" : ("[" + (entity != mc.player ? entity.getName() : "Self") + "]"));

        instance = this;
    }

    private List<BlockPos> holes = new ArrayList<>();

    private final Timer placeTimer = new Timer();

    private final Set<BlockPos> placed = new HashSet<>(512);

    private int lim = 0;

    private PlaceInfo placeInfo = null;

    @Override
    public void onEnable() {
        super.onEnable();
        targets.reset();
        threads.reset();
        renderer.reset();
        placeInfo = null;
    }

    @Override
    public void update() {
        if(mc.world == null || mc.player == null) return;

        entity = placeMode.getValString().equals("All") ? mc.player : targets.getTarget();

        if(entity == null) return;

        threads.update(() -> {
            mc.addScheduledTask(() -> holes = getHoleBlocks(entity));
        });

        placeHoleBlocks(entity);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(render.getValBoolean()) {
            renderer.onRenderWorld(
                    movingLength.getValFloat(),
                    fadeLength.getValFloat(),
                    renderer_,
                    placeInfo
            );
        }
    }

    private void placeHoleBlocks(Entity entity){
        int slot = getBlockSlot();
        if(slot == -1)
            return;
        if(place.getValString().equals("Instant")){
            holes.forEach(blockPos -> place(blockPos, slot));
            placeTimer.reset();
            return;
        }
        if(place.getValString().equals("Tick")){
            placeHoleBlocksChained(entity, slot);
            placeTimer.reset();
            return;
        }
        if(place.getValString().equals("Delay") && placeTimer.passedMs(delay.getValInt())){
            placeHoleBlocksChained(entity, slot);
            placeTimer.reset();
        }
    }

    private void placeHoleBlocksChained(Entity entity, int slot){
        boolean clear = true;
        for(BlockPos pos : holes){
            if(placed.contains(pos))
                continue;
            if(!mc.world.getEntitiesWithinAABBExcludingEntity(null, mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos)).isEmpty()) continue;
            placeInfo = new PlaceInfo((EntityLivingBase) entity, pos);
            place(pos, slot);
            placed.add(pos);
            clear = false;
            break;
        }
        if(clear)
            placed.clear();
    }

    private List<BlockPos> getHoleBlocks(Entity entity){
        List<BlockPos> holes = new ArrayList<>(64);
        float range = entity.equals(mc.player) ? holeRange.getValFloat() : aroundEnemyRange.getValFloat();
        Set<BlockPos> possibleHoles = getPossibleHoles(entity, range);
        lim = 0;
        if(singleHoles.getValBoolean())
            holes.addAll(getHoleBlocksOfType(possibleHoles, HoleUtil.HoleType.SINGLE));
        if(doubleHoles.getValBoolean())
            holes.addAll(getHoleBlocksOfType(possibleHoles, HoleUtil.HoleType.DOUBLE));
        if(customHoles.getValBoolean())
            holes.addAll(getHoleBlocksOfType(possibleHoles, HoleUtil.HoleType.CUSTOM));
        return holes;
    }

    private List<BlockPos> getHoleBlocksOfType(Set<BlockPos> possibleHoles, HoleUtil.HoleType type){
        List<BlockPos> holes = new ArrayList<>(32);
        for(BlockPos pos : possibleHoles){
            if(limit.getValInt() != 0 && lim > limit.getValInt())
                break;
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            HoleUtil.BlockSafety safety = holeInfo.getSafety();
            if(holeType != type)
                continue;
            if(safety == HoleUtil.BlockSafety.UNBREAKABLE && bedrockHoles.getValBoolean()){
                List<BlockPos> blocks = splitAABB(holeInfo.getCentre());
                holes.addAll(blocks);
                lim++;
                continue;
            }
            if(!obsidianHoles.getValBoolean())
                continue;
            List<BlockPos> blocks = splitAABB(holeInfo.getCentre());
            holes.addAll(blocks);
            lim++;
        }
        return holes;
    }

    /*
    private List<BlockPos> getSingleHoleBlocks(Set<BlockPos> possibleHoles){
        List<BlockPos> holes = new ArrayList<>(32);
        for(BlockPos pos : possibleHoles){
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            HoleUtil.BlockSafety safety = holeInfo.getSafety();
            if(holeType != HoleUtil.HoleType.SINGLE)
                continue;
            if(safety == HoleUtil.BlockSafety.UNBREAKABLE && !bedrockHoles.getValBoolean())
                continue;
            if(!obsidianHoles.getValBoolean())
                continue;

            List<BlockPos> blocks = splitAABB(holeInfo.getCentre());
            holes.addAll(blocks);
        }
        return holes;
    }

    private List<BlockPos> getDoubleHoleBlocks(Set<BlockPos> possibleHoles){
        List<BlockPos> holes = new ArrayList<>(32);
        for(BlockPos pos : possibleHoles){
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            HoleUtil.BlockSafety safety = holeInfo.getSafety();
            if(holeType != HoleUtil.HoleType.DOUBLE)
                continue;
            if(safety == HoleUtil.BlockSafety.UNBREAKABLE && !bedrockHoles.getValBoolean())
                continue;
            if(!obsidianHoles.getValBoolean())
                continue;

            List<BlockPos> blocks = splitAABB(holeInfo.getCentre());
            holes.addAll(blocks);
        }
        return holes;
    }
     */

    private Set<BlockPos> getPossibleHoles(Entity entity, float range){
        Set<BlockPos> possibleHoles = new HashSet<>();
        List<BlockPos> blockPosList = EntityUtil.getSphere(getEntityPos(entity), range, (int) range, false, true, 0);
        for (BlockPos pos : blockPosList) {
            AxisAlignedBB aabb = new AxisAlignedBB(pos);
            if(!mc.world.getEntitiesWithinAABB(Entity.class, aabb).isEmpty())
                continue;
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR))
                continue;
            if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.AIR))
                continue;
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR))
                continue;
            if (mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
                possibleHoles.add(pos);
        }
        return possibleHoles;
    }

    private BlockPos getEntityPos(Entity entity){
        return new BlockPos(entity.posX, entity.posY, entity.posZ);
    }

    private List<BlockPos> splitAABB(AxisAlignedBB aabb){
        List<BlockPos> list = new ArrayList<>();
        double xDiff = aabb.maxX - aabb.minX;
        double zDiff = aabb.maxZ - aabb.minZ;
        if(xDiff > 2.0 && zDiff > 2.0)
            return list;
        if(xDiff > zDiff){
            int x = (int) aabb.minX;
            int lim = (int) aabb.maxX;
            for(; x < lim; x++){
                list.add(new BlockPos(x, (int) aabb.minY, (int) aabb.minZ));
            }
        } else {
            int z = (int) aabb.minZ;
            int lim = (int) aabb.maxZ;
            for(; z < lim; z++){
                list.add(new BlockPos((int) aabb.minX, (int) aabb.minY, z));
            }
        }
        return list;
    }

    private int getBlockSlot(){
        if(blocks.getValString().equals("Obsidian")){
            return InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        } else {
            return InventoryUtil.getBlockInHotbar(Blocks.ENDER_CHEST);
        }
    }

    private void place(BlockPos pos, int slot){
        if(mc.player == null || mc.player.inventory == null) return;
        int oldSlot = mc.player.inventory.currentItem;
        doSwitch(slot, false);
        BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean(), false);
        doSwitch(oldSlot, true);
        mc.playerController.updateController();
    }

    private void doSwitch(int slot, boolean swapBack){
        switch(swap.getValString()){
            case "None":
                break;
            case "Vanilla":
                if(swapBack)
                    break;
                mc.player.inventory.currentItem = slot;
                break;
            case "Normal":
                mc.player.inventory.currentItem = slot;
                break;
            case "Packet":
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                break;
            case "Silent":
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                mc.player.inventory.currentItem = slot;
                break;
        }
    }

    @Override
    public boolean isBeta(){
        return true;
    }

    /*
    private static class BlockPosBundle {

        private BlockPos first;

        private BlockPos second;

        public BlockPosBundle(BlockPos first, BlockPos second){
            this.first = first;
            this.second = second;
        }

        public BlockPos getFirst() {
            return first;
        }

        public void setFirst(BlockPos first) {
            this.first = first;
        }

        public BlockPos getSecond() {
            return second;
        }

        public void setSecond(BlockPos second) {
            this.second = second;
        }
    }
     */
}
