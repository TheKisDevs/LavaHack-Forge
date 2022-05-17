package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.BlockUtil;
import com.kisman.cc.util.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SurroundRewrite extends Module {

    private final Setting mode = register(new Setting("Mode", this, Vectors.Normal));
    private final Setting block = register(new Setting("Block", this, "Obsidian", Arrays.asList("Obsidian", "EnderChest")));
    private final Setting swap = register(new Setting("Switch", this, Swap.Vanilla));
    private final Setting center = register(new Setting("Center", this, false));
    private final Setting toggleOfGround = register(new Setting("ToggleOfGround", this, false));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));
    private final Setting feetBlocks = register(new Setting("FeetBlocks", this, false));

    private static SurroundRewrite instance;

    private double lastY = -1;

    public SurroundRewrite(){
        super("SurroundRewrite", Category.COMBAT);
        instance = this;
    }

    @Override
    public void onEnable(){
        if(mc.player == null || mc.world == null) return;
        if(center.getValBoolean()) centerPlayer();
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null) return;

        double y = mc.player.posY;

        if(toggleOfGround.getValBoolean() && ((lastY - y) > 0.3 || !mc.player.onGround))
            toggle();

        placeBlocks();

        lastY = y;
    }

    @Override
    public void onDisable(){
        lastY = -1;
    }

    private void centerPlayer(){
        Vec3d setCenter = new Vec3d(Math.floor(mc.player.posX) + 0.5D, Math.floor(mc.player.posY), Math.floor(mc.player.posZ) + 0.5D);
        mc.player.motionX = 0;
        mc.player.motionZ = 0;
        mc.player.connection.sendPacket(new CPacketPlayer.Position(setCenter.x, setCenter.y, setCenter.z, true));
        mc.player.setPosition(setCenter.x, setCenter.y, setCenter.z);
    }

    private void placeBlocks(){
        int slot = getBlockSlot();
        if(slot == -1) return;
        List<BlockPos> blocks = ((Vectors) mode.getValEnum()).getBlocks();
        Swap swap = (Swap) this.swap.getValEnum();
        int oldSlot = mc.player.inventory.currentItem;
        for(BlockPos pos : blocks){
            if(!isReplaceable(pos)) continue;
            if(checkEntities(pos)) continue;
            swap.doSwap(slot, false);
            BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean(), false);
            swap.doSwap(oldSlot, true);
        }
    }

    private boolean checkEntities(BlockPos pos){
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, aabb)){
            if(entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
            return true;
        }
        return false;
    }

    private int getBlockSlot(){
        if(block.getValString().equals("Obsidian"))
            return InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        return InventoryUtil.getBlockInHotbar(Blocks.ENDER_CHEST);
    }

    private List<BlockPos> getDynamicBlocks(){
        List<BlockPos> upperBlocks = getDynamicUpperBlocks();
        List<BlockPos> blocks = new ArrayList<>(16);
        if(feetBlocks.getValBoolean())
            blocks.addAll(getDynamicBlocksOffset(-1));
        for(BlockPos pos : upperBlocks){
            List<BlockPos> helpingBlocks = getHelpingBlocks(pos);
            blocks.addAll(helpingBlocks);
            blocks.add(pos);
        }
        return blocks;
    }

    private List<BlockPos> getHelpingBlocks(BlockPos pos){
        // this will change in the future
        return Arrays.asList(pos.down());
    }

    private List<BlockPos> getDynamicUpperBlocks(){
        List<BlockPos> rawBlocks = getDynamicBlocksOffset(0);
        List<BlockPos> blocks = new ArrayList<>(16);
        for(BlockPos pos : rawBlocks){
            BlockPos b1 = pos.north();
            BlockPos b2 = pos.east();
            BlockPos b3 = pos.south();
            BlockPos b4 = pos.west();
            if(!rawBlocks.contains(b1)) blocks.add(b1);
            if(!rawBlocks.contains(b2)) blocks.add(b2);
            if(!rawBlocks.contains(b3)) blocks.add(b3);
            if(!rawBlocks.contains(b4)) blocks.add(b4);
        }
        return blocks;
    }

    private List<BlockPos> getDynamicBlocksOffset(int offset){
        List<BlockPos> list = new ArrayList<>(16);
        Vec3d vec1 = new Vec3d(mc.player.posX + 0.3, mc.player.posY + offset, mc.player.posZ + 0.3);
        Vec3d vec2 = new Vec3d(mc.player.posX + 0.3, mc.player.posY + offset, mc.player.posZ - 0.3);
        Vec3d vec3 = new Vec3d(mc.player.posX - 0.3, mc.player.posY + offset, mc.player.posZ + 0.3);
        Vec3d vec4 = new Vec3d(mc.player.posX - 0.3, mc.player.posY + offset, mc.player.posZ - 0.3);
        addIfChecks(vec1, list);
        addIfChecks(vec2, list);
        addIfChecks(vec3, list);
        addIfChecks(vec4, list);
        return list;
    }

    private void addIfChecks(Vec3d vec, List<BlockPos> list){
        BlockPos pos = new BlockPos(vec);
        if(isReplaceable(pos) && !list.contains(pos))
            list.add(pos);
    }

    private boolean isReplaceable(BlockPos pos){
        return mc.world.getBlockState(pos).getMaterial().isReplaceable();
    }

    private enum Vectors {
        Normal(new Vec3d[]{
                new Vec3d(1, -1, 0),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(0, -1, -1),
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1)
        }),
        Strict(new Vec3d[]{
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1)
        }),
        Safe(new Vec3d[]{
                new Vec3d(1, -1, 0),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(0, -1, -1),
                new Vec3d(1, -1, 1),
                new Vec3d(1, -1, -1),
                new Vec3d(-1, -1, 1),
                new Vec3d(-1, -1, -1),
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1),
                new Vec3d(1, 0, 1),
                new Vec3d(1, 0, -1),
                new Vec3d(-1, 0, 1),
                new Vec3d(-1, 0, -1),
                new Vec3d(2, 0, 0),
                new Vec3d(-2, 0, 0),
                new Vec3d(0, 0, 2),
                new Vec3d(0, 0, -2)
        }),
        Cubic(new Vec3d[]{
                new Vec3d(1, -1, 0),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(0, -1, -1),
                new Vec3d(1, -1, 1),
                new Vec3d(1, -1, -1),
                new Vec3d(-1, -1, 1),
                new Vec3d(-1, -1, -1),
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1),
                new Vec3d(1, 0, 1),
                new Vec3d(1, 0, -1),
                new Vec3d(-1, 0, 1),
                new Vec3d(-1, 0, -1)
        }),
        Dynamic(null);

        private final Vec3d[] vec3d;

        Vectors(Vec3d[] vec3d){
            this.vec3d = vec3d;
        }

        public List<BlockPos> getBlocks(){
            List<BlockPos> list = new ArrayList<>(64);
            if(this == Dynamic)
                return instance.getDynamicBlocks();
            if(instance.feetBlocks.getValBoolean())
                list.addAll(instance.getDynamicBlocksOffset(-1));
            Vec3d posVec = mc.player.getPositionVector();
            for(Vec3d vec : vec3d){
                list.add(new BlockPos(vec.add(posVec)));
            }
            return list;
        }
    }

    private enum Swap {
        Vanilla(new VanillaSwap()),
        Packet(new PacketSwap()),
        Silent(new SilentSwap());

        private final AbstractSwap swap;

        Swap(AbstractSwap swap){
            this.swap = swap;
        }

        public void doSwap(int slot, boolean swapBack){
            if(mc.player.inventory.currentItem == slot) return;
            swap.doSwap(slot, swapBack);
        }
    }

    private static abstract class AbstractSwap {

        public AbstractSwap(){

        }

        public abstract void doSwap(int slot, boolean swapBack);
    }

    private static class VanillaSwap extends AbstractSwap {

        @Override
        public void doSwap(int slot, boolean swapBack) {
            if(swapBack) return;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
        }
    }

    private static class PacketSwap extends AbstractSwap {

        @Override
        public void doSwap(int slot, boolean swapBack) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            if(swapBack)
                mc.playerController.updateController();
        }
    }

    private static class SilentSwap extends AbstractSwap {

        @Override
        public void doSwap(int slot, boolean swapBack) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
            if(swapBack)
                mc.playerController.updateController();
        }
    }
}
