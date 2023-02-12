package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.features.module.PingBypassModule;
import com.kisman.cc.features.subsystem.subsystems.Target;
import com.kisman.cc.features.subsystem.subsystems.Targetable;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.settings.util.SlideRenderingRewritePattern;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.entity.TargetFinder;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.dynamic.BlockEnum;
import com.kisman.cc.util.render.pattern.SlideRendererPattern;
import com.kisman.cc.util.world.BlockUtil;
import com.kisman.cc.util.world.BlockUtil2;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cubic.dynamictask.AbstractTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

/**
 * @author Cubic
 */
@PingBypassModule
@Targetable
public class FlattenRewrite extends Module {

    private final SettingGroup placeGroup = register(new SettingGroup(new Setting("Place", this)));
    private final Setting placeMode = register(placeGroup.add(new Setting("PlaceMode", this, PlaceModeEnum.Modes.Sides).setTitle("Mode")));
    private final Setting placeRange = register(placeGroup.add(new Setting("PlaceRange", this, 5, 1, 10, false).setTitle("Range")));
    private final Setting placeDelay = register(placeGroup.add(new Setting("PlaceDelay", this, PlaceDelay.None).setTitle("Delay Mode")));
    private final Setting placeDelayMS = register(placeGroup.add(new Setting("PlaceDelayMS", this, 50, 0, 500, NumberType.TIME).setVisible(() -> placeDelay.getValEnum() == PlaceDelay.DelayMS).setTitle("Delay")));
    private final Setting placeRetry = register(placeGroup.add(new Setting("PlaceRetry", this, false).setTitle("Retry")));
    private final Setting placeHeightLimit = register(placeGroup.add(new Setting("PlaceHeightLimit", this, 256, 0, 256, true).setTitle("Height Limit")));

    private final SettingGroup swapGroup = register(new SettingGroup(new Setting("Swap", this)));
    private final Setting swapMode = register(swapGroup.add(new Setting("SwapMode", this, SwapModeEnum.SwapModes.Silent).setTitle("Mode")));
    private final Setting swapWhen = register(swapGroup.add(new Setting("SwapWhen", this, SwapWhen.Place).setTitle("When")));
    private final SettingGroup syncItemGroup = register(swapGroup.add(new SettingGroup(new Setting("Sync Item", this))));
    private final Setting swapSyncItem = register(syncItemGroup.add(new Setting("SyncItem", this, false).setTitle("Sync")));
    private final Setting swapSyncItemWhen = register(syncItemGroup.add(new Setting("SyncItemWhen", this, SyncItemWhen.AfterSwap).setVisible(swapSyncItem::getValBoolean).setTitle("When")));

    private final Setting web = register(placeGroup.add(new Setting("Web", this, false)));
    // will try to place inside the player, not underneath - Cubic
    private final Setting webOffset = register(placeGroup.add(new Setting("WebOffset", this, false).setVisible(web::getValBoolean)));
    private final Setting block = register(new Setting("Block", this, BlockEnum.Blocks.Obsidian));

    private final Setting keepY = register(new Setting("KeepY", this, true));

    private final Setting checkDown = register(new Setting("CheckDown", this, 2, 1, 8, true));
    private final Setting alwaysCheckDown = register(new Setting("AlwaysCheckDown", this, false));

    private final Setting enemyRange = register(new Setting("EnemyRange", this, 8, 1, 15, false));
    private final Setting swapEnemy = register(new Setting("SwapEnemy", this, false));
    private final Setting predictCycles = register(new Setting("PredictCycles", this, 0, 0, 10, true).setVisible(() -> ((PlaceModeEnum.Modes) placeMode.getValEnum()).isPredictSupported()));
    private final Setting predictTicks = register(new Setting("PredictTicks", this, 2, 0, 20, true).setVisible(() -> ((PlaceModeEnum.Modes) placeMode.getValEnum()).isPredictSupported()));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));
    private final Setting processPackets = register(new Setting("ProcessPackets", this, false));

    private final SettingGroup render_ = register(new SettingGroup(new Setting("Render", this)));
    private final SlideRenderingRewritePattern renderer_ = new SlideRenderingRewritePattern(this).group(render_).preInit().init();

    private final MultiThreaddableModulePattern threads = threads();
    private final TargetFinder targets = new TargetFinder(enemyRange::getValDouble, threads.getDelay()::getValLong, threads.getMultiThread()::getValBoolean);

    private final SlideRendererPattern renderer = new SlideRendererPattern();

    @ModuleInstance
    public static FlattenRewrite instance;

    private Queue<BlockPos> blocks = new ConcurrentLinkedQueue<>();

    // handle custom delay in separate thread
    private final Thread placeThread;

    @Target
    public Entity enemy = null;

    private double enemyY;

    private BlockPos placeInfo = null;

    private Block block() {
        return web.getValBoolean() ? Blocks.WEB : ((BlockEnum.Blocks) block.getValEnum()).getTask().doTask();
    }

    public FlattenRewrite() {
        super("FlattenRewrite", Category.COMBAT);
        super.displayName = "Flatten";
        placeThread = new Thread(() -> {
            TimerUtils timer = new TimerUtils();
            boolean toggled = this.isToggled();
            while(toggled){
                if(placeDelay.getValEnum() != PlaceDelay.DelayMS)
                    continue;
                if(!timer.passedMillis(placeDelayMS.getValInt())){
                    toggled = this.isToggled();
                    continue;
                }
                int slot = InventoryUtil.getBlockInHotbar(block());
                if(slot == -1)
                    continue;
                int oldSlot = mc.player.inventory.currentItem;
                if(blocks.size() <= 0) {
                    toggled = this.isToggled();
                    continue;
                }
                BlockPos pos = blocks.peek();
                place: {
                    if(pos.getY() > placeHeightLimit.getValInt())
                        break place;
                    if(!entityCheck(pos) && placeRetry.getValBoolean()){
                        placeBlock(pos, oldSlot, slot);
                    }
                    blocks.poll();
                }
                toggled = this.isToggled();
            }
        });
        placeThread.start();
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null) return;

        boolean alreadyCheckedDown = false;

        if(enemy == null || swapEnemy.getValBoolean()){
            enemy = targets.getTarget(enemyRange.getValFloat());

            if(enemy == null) return;

            enemyY = enemy.posY;

            if(!checkDown(new Vec3d(enemy.posX, keepY.getValBoolean() ? enemyY : enemy.posY, enemy.posZ))) return;

            alreadyCheckedDown = true;
        }

        Vec3d vec = new Vec3d(enemy.posX, keepY.getValBoolean() ? enemyY - 1.0 : enemy.posY - 1.0, enemy.posZ);

        if(web.getValBoolean() && webOffset.getValBoolean())
            vec = new Vec3d(vec.x, vec.y + 1, vec.z);

        if(alwaysCheckDown.getValBoolean() && !alreadyCheckedDown && !checkDown(vec))             return;

        ((PlaceModeEnum.Modes) placeMode.getValEnum()).getTask().doTask(vec, enemy);

        int slot = InventoryUtil.getBlockInHotbar(block());

        int oldSlot = mc.player.inventory.currentItem;

        swap(slot, false, SwapWhen.Tick);

        if(placeDelay.getValEnum() == PlaceDelay.None){
            List<BlockPos> placed = new ArrayList<>(blocks.size());
            for(BlockPos pos : blocks){
                if(pos.getY() > placeHeightLimit.getValInt()) continue;
                if(entityCheck(pos)){
                    if(!placeRetry.getValBoolean()) placed.add(pos);
                    continue;
                }
                placeBlock(pos, oldSlot, slot);
                placed.add(pos);
            }
            blocks.removeAll(placed);
        } else if(blocks.size() > 0) {
            BlockPos pos = blocks.peek();
            place: {
                if(pos.getY() > placeHeightLimit.getValInt()) break place;
                if (!entityCheck(pos) || placeRetry.getValBoolean()) placeBlock(pos, oldSlot, slot);
                blocks.poll();
            }
        }

        swap(oldSlot, true, SwapWhen.Tick);

        if(processPackets.getValBoolean()){
            if (mc.player.connection.getNetworkManager().isChannelOpen()) mc.player.connection.getNetworkManager().processReceivedPackets();
            else mc.player.connection.getNetworkManager().checkDisconnected();
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(renderer_.isActive()) {
            renderer.handleRenderWorld(
                    renderer_,
                    placeInfo,
                    null
            );
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        blocks = new ConcurrentLinkedQueue<>();
        placeInfo = null;
        enemy = null;
        enemyY = 0.0;
        targets.reset();
        threads.reset();
        renderer.reset();
    }

    private boolean checkDown(Vec3d vec){
        int beginAdd = -1;
        for(int i = 0; i < checkDown.getValInt(); i++){
            BlockPos pos = new BlockPos(vec.x, vec.y - i - 1, vec.z);
            if(BlockUtil.getPossibleSides(pos).isEmpty())
                continue;
            beginAdd = i + 1;
        }
        if(beginAdd == -1)
            return false;
        for(int i = beginAdd; i >= 1; i--){
            addIfAbsentAndReplaceable(new BlockPos(vec.x, vec.y - i, vec.z));
        }
        return true;
    }

    private boolean entityCheck(BlockPos pos){
        return !mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).isEmpty();
    }

    private void placeBlock(BlockPos pos, int oldSlot, int slot){
        placeInfo = pos;
        if(mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > placeRange.getValDouble())
            return;
        swap(slot, false, SwapWhen.Place);
        BlockUtil2.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), true, packet.getValBoolean());
        swap(oldSlot, true, SwapWhen.Place);
    }

    private void swap(int slot, boolean swapBack, SwapWhen when){
        if(when != swapWhen.getValEnum())
            return;
        SyncItemWhen syncWhen = (SyncItemWhen) swapSyncItemWhen.getValEnum();
        if(syncWhen == SyncItemWhen.Both || syncWhen == SyncItemWhen.BeforeSwap)
            mc.playerController.syncCurrentPlayItem();
        ((SwapModeEnum.SwapModes) swapMode.getValEnum()).getTask().doTask(slot, swapBack);
        if(syncWhen == SyncItemWhen.Both || syncWhen == SyncItemWhen.AfterSwap)
            mc.playerController.syncCurrentPlayItem();
    }

    private static boolean isReplaceable(BlockPos pos){
        return mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos);
    }

    private static List<BlockPos> getSideBlocks(BlockPos pos){
        List<BlockPos> blocks = new ArrayList<>();
        BlockPos north = pos.north();
        BlockPos east = pos.east();
        BlockPos south = pos.south();
        BlockPos west = pos.west();
        if(isReplaceable(north))
            blocks.add(north);
        if(isReplaceable(east))
            blocks.add(east);
        if(isReplaceable(south))
            blocks.add(south);
        if(isReplaceable(west))
            blocks.add(west);
        return blocks;
    }

    private static List<BlockPos> getSquareBlocks(BlockPos pos){
        List<BlockPos> blocks = new ArrayList<>();
        BlockPos north = pos.north();
        BlockPos east = pos.east();
        BlockPos south = pos.south();
        BlockPos west = pos.west();
        BlockPos northWest = north.west();
        BlockPos northEast = north.east();
        BlockPos southWest = south.west();
        BlockPos southEast = south.east();
        if(isReplaceable(north))
            blocks.add(north);
        if(isReplaceable(east))
            blocks.add(east);
        if(isReplaceable(south))
            blocks.add(south);
        if(isReplaceable(west))
            blocks.add(west);
        if(isReplaceable(northWest))
            blocks.add(northWest);
        if(isReplaceable(northEast))
            blocks.add(northEast);
        if(isReplaceable(southWest))
            blocks.add(southWest);
        if(isReplaceable(southEast))
            blocks.add(southEast);
        return blocks;
    }

    private static void addIfAbsentAndReplaceable(BlockPos pos){
        if(!isReplaceable(pos) || instance.blocks.contains(pos))
            return;
        instance.blocks.add(pos);
    }

    private static void addAllIfAbsent(List<BlockPos> list){
        for (BlockPos pos : list) {
            if (!instance.blocks.contains(pos))
                instance.blocks.add(pos);
        }
    }

    private static boolean checkEntities(BlockPos pos){
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, aabb)){
            if(entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
            return true;
        }
        return false;
    }

    private static BlockPos getConnector(Entity entity, BlockPos pos){
        return Stream.of(EnumFacing.HORIZONTALS)
                .map(pos::offset)
                .filter(blockPos -> !BlockUtil.getPossibleSides(blockPos).isEmpty())
                .filter(blockPos -> !checkEntities(blockPos))
                .min(Comparator.comparingDouble(blockPos -> entity.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5)))
                .orElse(null);
    }

    private static BlockPos getDamagePos(Entity entity, BlockPos pos){
        return Stream.of(EnumFacing.HORIZONTALS)
                .map(pos::offset)
                .filter(blockPos -> !BlockUtil.getPossibleSides(blockPos).isEmpty())
                .filter(blockPos -> !checkEntities(blockPos))
                .filter(blockPos -> !new AxisAlignedBB(blockPos ).intersects(entity.getEntityBoundingBox()))
                .filter(blockPos -> mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR)
                .filter(blockPos -> mc.world.getBlockState(blockPos.up(2)).getBlock() == Blocks.AIR)
                .min(Comparator.comparingDouble(blockPos -> entity.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5)))
                .orElse(null);
    }

    private static final class PlaceModeEnum {

        private static final AbstractTask.DelegateAbstractTask<Void> task = AbstractTask.types(Void.class, Vec3d.class, Entity.class);

        public enum Modes {
            Sides(false, task.task(args -> {
                Vec3d vec = args.fetch(0);
                BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
                List<BlockPos> blocks = getSideBlocks(pos);
                addAllIfAbsent(blocks);
                return null;
            })),
            Square(false, task.task(args -> {
                Vec3d vec = args.fetch(0);
                BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
                List<BlockPos> blocks = getSquareBlocks(pos);
                addAllIfAbsent(blocks);
                return null;
            })),
            SmartSquare(false, task.task(args -> {
                Vec3d vec = args.fetch(0);
                Entity entity = args.fetch(1);
                BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
                List<BlockPos> list = new ArrayList<>();
                EnumFacing ignoreFacing = EnumFacing.getFacingFromVector((float) entity.motionX, 0, (float) entity.motionZ);
                for(EnumFacing facing : EnumFacing.HORIZONTALS){
                    if(facing == ignoreFacing)
                        continue;
                    list.add(pos.offset(facing));
                }
                addAllIfAbsent(list);
                return null;
            })),
            PlayerPosition(true, task.task(args -> {
                Vec3d vec = args.fetch(0);
                Entity entity = args.fetch(1);
                BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
                addIfAbsentAndReplaceable(pos);
                double x = vec.x;
                double y = vec.y;
                double z = vec.z;
                int predictAmount = instance.predictCycles.getValInt() * 20 + instance.predictTicks.getValInt();
                for(int i = 0; i < predictAmount; i++){
                    x += entity.motionX;
                    if(!instance.keepY.getValBoolean()) y += entity.motionY;
                    z += entity.motionZ;
                    BlockPos blockPos = new BlockPos(x, y, z);
                    addIfAbsentAndReplaceable(blockPos);
                }
                return null;
            })),
            Player(true, task.task(args -> {
                Vec3d vec = args.fetch(0);
                Entity entity = args.fetch(1);
                BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
                addIfAbsentAndReplaceable(pos);
                double x = vec.x;
                double y = vec.y;
                double z = vec.z;
                int predictAmount = instance.predictCycles.getValInt() * 20 + instance.predictTicks.getValInt();
                for(int i = 0; i < predictAmount; i++){
                    x += entity.motionX;
                    if(!instance.keepY.getValBoolean()) y += entity.motionY;
                    z += entity.motionZ;
                    BlockPos pos1 = new BlockPos(x + 0.3, y, z + 0.3);
                    BlockPos pos2 = new BlockPos(x + 0.3, y, z - 0.3);
                    BlockPos pos3 = new BlockPos(x - 0.3, y, z + 0.3);
                    BlockPos pos4 = new BlockPos(x - 0.3, y, z - 0.3);
                    addIfAbsentAndReplaceable(pos1);
                    addIfAbsentAndReplaceable(pos2);
                    addIfAbsentAndReplaceable(pos3);
                    addIfAbsentAndReplaceable(pos4);
                }
                return null;
            })),
            Scaffold(false, task.task(args -> {
                Vec3d vec = args.fetch(0);
                Entity entity = args.fetch(1);
                BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
                BlockPos connector = getConnector(entity, pos);
                if(BlockUtil.getPossibleSides(pos).isEmpty() && connector != null)
                    addIfAbsentAndReplaceable(connector);
                addIfAbsentAndReplaceable(pos);
                return null;
            })),
            ScaffoldDamage(false, task.task(args -> {
                Vec3d vec = args.fetch(0);
                Entity entity = args.fetch(1);
                BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
                BlockPos connector = getConnector(entity, pos);
                if(BlockUtil.getPossibleSides(pos).isEmpty() && connector != null)
                    addIfAbsentAndReplaceable(connector);
                addIfAbsentAndReplaceable(pos);
                BlockPos damagePos = getDamagePos(entity, pos);
                if(damagePos != null)
                    addIfAbsentAndReplaceable(pos);
                return null;
            }));

            private final boolean predictSupported;

            private final AbstractTask<Void> abstractTask;

            Modes(boolean predictSupported, AbstractTask<Void> task){
                this.predictSupported = predictSupported;
                this.abstractTask = task;
            }

            public boolean isPredictSupported(){
                return this.predictSupported;
            }

            public AbstractTask<Void> getTask(){
                return this.abstractTask;
            }
        }
    }

    private static final class SwapModeEnum {

        private static final AbstractTask.DelegateAbstractTask<Void> task = AbstractTask.types(Void.class, Integer.class, Boolean.class);

        public enum SwapModes {
            Vanilla(task.task(args -> {
                if(args.fetch(1))
                    return null;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(args.fetch(0)));
                mc.player.inventory.currentItem = args.fetch(0);
                return null;
            })),
            Silent(task.task(args -> {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(args.fetch(0)));
                return null;
            })),
            Packet(task.task(args -> {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(args.fetch(0)));
                mc.player.inventory.currentItem = args.fetch(0);
                return null;
            }));

            private final AbstractTask<Void> abstractTask;

            SwapModes(AbstractTask<Void> task){
                this.abstractTask = task;
            }

            public AbstractTask<Void> getTask(){
                return this.abstractTask;
            }
        }
    }

    private enum SwapWhen {
        Place,
        Tick
    }

    private enum PlaceDelay {
        None,
        Tick,
        DelayMS
    }

    private enum SyncItemWhen {
        BeforeSwap,
        AfterSwap,
        Both
    }
}
