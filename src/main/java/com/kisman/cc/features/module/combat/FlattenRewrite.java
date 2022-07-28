package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.Timer;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.dynamic.BlockEnum;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.cubic.dynamictask.AbstractTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FlattenRewrite extends Module {

    private final SettingGroup placeGroup = register(new SettingGroup(new Setting("Place", this)));
    private final Setting placeMode = register(placeGroup.add(new Setting("PlaceMode", this, PlaceModeEnum.Modes.Sides)));
    private final Setting placeDelay = register(placeGroup.add(new Setting("PlaceDelay", this, PlaceDelay.None)));
    private final Setting placeDelayMS = register(placeGroup.add(new Setting("PlaceDelayMS", this, 50, 0, 500, true).setVisible(() -> placeDelay.getValEnum() == PlaceDelay.DelayMS)));

    private final SettingGroup swapGroup = register(new SettingGroup(new Setting("Swap", this)));
    private final Setting swapMode = register(swapGroup.add(new Setting("SwapMode", this, SwapModeEnum.SwapModes.Silent)));
    private final Setting swapWhen = register(swapGroup.add(new Setting("SwapWhen", this, SwapWhen.Place)));
    private final Setting swapSyncItem = register(swapGroup.add(new Setting("SyncItem", this, false)));
    private final Setting swapSyncItemWhen = register(swapGroup.add(new Setting("SyncItemWhen", this, SyncItemWhen.AfterSwap).setVisible(swapSyncItem::getValBoolean)));

    private final Setting block = register(new Setting("Block", this, BlockEnum.Blocks.Obsidian));

    private final Setting keepY = register(new Setting("KeepY", this, true));

    private final Setting checkDown = register(new Setting("CheckDown", this, 2, 1, 8, true));

    private final Setting enemyRange = register(new Setting("EnemyRange", this, 8, 1, 15, false));
    private final Setting swapEnemy = register(new Setting("SwapEnemy", this, false));
    private final Setting predictTicks = register(new Setting("PredictTicks", this, 2, 0, 20, true).setVisible(() -> ((PlaceModeEnum.Modes) placeMode.getValEnum()).isPredictSupported()));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));
    private final Setting processPackets = register(new Setting("ProcessPackets", this, false));

    private static FlattenRewrite instance;

    private final Queue<BlockPos> blocks = new ConcurrentLinkedQueue<>();

    // handle custom delay in separate thread
    private final Thread placeThread;

    public FlattenRewrite() {
        super("FlattenRewrite", Category.COMBAT);
        instance = this;
        placeThread = new Thread(() -> {
            Timer timer = new Timer();
            boolean toggled = this.isToggled();
            while(toggled){
                if(placeDelay.getValEnum() != PlaceDelay.DelayMS)
                    continue;
                if(!timer.passedMs(placeDelayMS.getValInt())){
                    toggled = this.isToggled();
                    continue;
                }
                int slot = InventoryUtil.getBlockInHotbar(((BlockEnum.Blocks) block.getValEnum()).getTask().doTask());
                if(slot == -1)
                    continue;
                int oldSlot = mc.player.inventory.currentItem;
                if(blocks.size() > 0)
                    placeBlock(blocks.poll(), oldSlot, slot);
                toggled = this.isToggled();
            }
        });
    }

    private void checkDown(){

    }

    private void placeBlock(BlockPos pos, int oldSlot, int slot){
        swap(slot, false, SwapWhen.Place);
        BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean());
        swap(slot, true, SwapWhen.Place);
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
            PlayerPosition(false, task.task(args -> {
                Vec3d vec = args.fetch(0);
                Entity entity = args.fetch(1);
                BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
                addIfAbsentAndReplaceable(pos);
                double x = vec.x;
                double y = vec.y;
                double z = vec.z;
                for(int i = 0; i < instance.predictTicks.getValInt(); i++){
                    x += entity.motionX;
                    if(!instance.keepY.getValBoolean()) y += entity.motionY;
                    z += entity.motionZ;
                    BlockPos blockPos = new BlockPos(x, y, z);
                    addIfAbsentAndReplaceable(blockPos);
                }
                return null;
            })),
            Player(false, task.task(args -> {
                Vec3d vec = args.fetch(0);
                Entity entity = args.fetch(1);
                BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
                addIfAbsentAndReplaceable(pos);
                double x = vec.x;
                double y = vec.y;
                double z = vec.z;
                for(int i = 0; i < instance.predictTicks.getValInt(); i++){
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

        private static final AbstractTask.DelegateAbstractTask<Void> task = AbstractTask.types(Void.class, int.class, boolean.class);

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
