package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.BlockUtil;
import com.kisman.cc.util.InventoryUtil;
import com.kisman.cc.util.RotationUtils;
import com.kisman.cc.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class SurroundRewrite extends Module {

    private final Setting mode = register(new Setting("Mode", this, Vectors.Normal));
    private final Setting block = register(new Setting("Block", this, "Obsidian", Arrays.asList("Obsidian", "EnderChest")));
    private final Setting swap = register(new Setting("Switch", this, Swap.Silent));
    private final Setting center = register(new Setting("Center", this, false));
    private final Setting toggle = register(new Setting("Toggle", this, Toggle.OffGround));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));
    private final Setting feetBlocks = register(new Setting("FeetBlocks", this, false));

    private final Setting breakCrystals = register(new Setting("BreakCrystals", this, false));

    private final SettingGroup crystalBreaker = register(new SettingGroup(new Setting("CrystalBreaker", this)));

    private final Setting cbMode = crystalBreaker.add(new Setting("CbMode", this, "SurroundBlocks", Arrays.asList("SurroundBlocks", "Area")));
    private final Setting cbRange = crystalBreaker.add(new Setting("CBRange", this, 3.0, 1.0, 6.0, false).setVisible(() -> cbMode.getValString().equals("Area")));
    private final Setting cbDelay = crystalBreaker.add(new Setting("CBDelay", this, 60, 0, 500, true));
    private final Setting cbRotate = crystalBreaker.add(new Setting("CBRotate", this, false));
    private final Setting cbPacket = crystalBreaker.add(new Setting("CBPacket", this, false));
    private final Setting clientSide = crystalBreaker.add(new Setting("ClientSide", this, false));

    private static SurroundRewrite instance;

    private final Timer timer = new Timer();

    private double lastY = -1;

    public SurroundRewrite(){
        super("SurroundRewrite", Category.COMBAT);
        instance = this;
    }

    @Override
    public void onEnable(){
        timer.reset();
        if(mc.player == null || mc.world == null) return;
        if(center.getValBoolean()) centerPlayer();
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null) return;

        double y = mc.player.posY;

        if(toggleMode() == Toggle.OffGround && !mc.player.onGround){
            toggle();
            return;
        }

        if(toggleMode() == Toggle.YChange && ((lastY - y) != 0.0)){
            toggle();
            return;
        }

        if(toggleMode() == Toggle.PositiveYChange && ((lastY - y) > 0.3)){
            toggle();
            return;
        }

        if(toggleMode() == Toggle.Combo && ((lastY - y) > 0.3 || !mc.player.onGround)){
            toggle();
            return;
        }

        List<BlockPos> blocks = ((Vectors) mode.getValEnum()).getBlocks();

        if(breakCrystals.getValBoolean())
            breakCrystals(blocks);

        placeBlocks(blocks);

        lastY = y;

        if(toggleMode() == Toggle.OnComplete){
            toggle();
        }
    }

    private Toggle toggleMode(){
        return (Toggle) toggle.getValEnum();
    }

    @Override
    public void onDisable(){
        lastY = -1;
        timer.reset();
    }

    private void breakCrystals(List<BlockPos> blocks){
        if(!timer.passedMs(cbDelay.getValInt()))
            return;
        //List<BlockPos> blocks = ((Vectors) mode.getValEnum()).getBlocks();
        float[] oldRots = new float[] {mc.player.rotationYaw, mc.player.rotationPitch};
        Set<EntityEnderCrystal> alreadyHit = new HashSet<>(64);
        if(cbMode.getValString().equals("Area")){
            double range = cbRange.getValDouble();
            double x1 = mc.player.posX - range;
            double y1 = mc.player.posY - range;
            double z1 = mc.player.posZ - range;
            double x2 = mc.player.posX + range;
            double y2 = mc.player.posY + range;
            double z2 = mc.player.posZ + range;
            AxisAlignedBB aabb = new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
            for(EntityEnderCrystal crystal : mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, aabb)){
                /*
                if(cbRotate.getValBoolean()){
                    float[] rots = RotationUtils.getRotation(crystal);
                    mc.player.rotationYaw = rots[0];
                    mc.player.rotationPitch = rots[1];
                }
                if(cbPacket.getValBoolean())
                    mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                else
                    mc.playerController.attackEntity(mc.player, crystal);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                if(clientSide.getValBoolean())
                    mc.world.removeEntityFromWorld(crystal.entityId);
                if(cbRotate.getValBoolean()){
                    mc.player.rotationYaw = oldRots[0];
                    mc.player.rotationPitch = oldRots[1];
                }
                 */
                breakCrystal(crystal, oldRots);
            }
            return;
        }
        for(BlockPos pos : blocks){
            for(EntityEnderCrystal crystal : mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(pos))){
                if(alreadyHit.contains(crystal)) continue;
                /*
                if(cbRotate.getValBoolean()){
                    float[] rots = RotationUtils.getRotation(crystal);
                    mc.player.rotationYaw = rots[0];
                    mc.player.rotationPitch = rots[1];
                }
                if(cbPacket.getValBoolean())
                    mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                else
                    mc.playerController.attackEntity(mc.player, crystal);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                if(clientSide.getValBoolean())
                    mc.world.removeEntityFromWorld(crystal.entityId);
                if(cbRotate.getValBoolean()){
                    mc.player.rotationYaw = oldRots[0];
                    mc.player.rotationPitch = oldRots[1];
                }
                 */
                breakCrystal(crystal, oldRots);
                alreadyHit.add(crystal);
            }
        }
    }

    private void breakCrystal(EntityEnderCrystal crystal, float[] oldRots){
        if(cbRotate.getValBoolean()){
            float[] rots = RotationUtils.getRotation(crystal);
            mc.player.rotationYaw = rots[0];
            mc.player.rotationPitch = rots[1];
        }
        if(cbPacket.getValBoolean())
            mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        else
            mc.playerController.attackEntity(mc.player, crystal);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        if(clientSide.getValBoolean())
            mc.world.removeEntityFromWorld(crystal.entityId);
        if(cbRotate.getValBoolean()){
            mc.player.rotationYaw = oldRots[0];
            mc.player.rotationPitch = oldRots[1];
        }
    }

    private void centerPlayer(){
        Vec3d setCenter = new Vec3d(Math.floor(mc.player.posX) + 0.5D, Math.floor(mc.player.posY), Math.floor(mc.player.posZ) + 0.5D);
        mc.player.motionX = 0;
        mc.player.motionZ = 0;
        mc.player.connection.sendPacket(new CPacketPlayer.Position(setCenter.x, setCenter.y, setCenter.z, true));
        mc.player.setPosition(setCenter.x, setCenter.y, setCenter.z);
    }

    private void placeBlocks(List<BlockPos> blocks){
        int slot = getBlockSlot();
        if(slot == -1) return;
        //List<BlockPos> blocks = ((Vectors) mode.getValEnum()).getBlocks();
        Swap swap = (Swap) this.swap.getValEnum();
        int oldSlot = mc.player.inventory.currentItem;
        if(swap == Swap.None){
            ItemStack stack = mc.player.inventory.getStackInSlot(oldSlot);
            Item item = stack.getItem();
            if(!(item instanceof ItemBlock))
                return;
            Block block = ((ItemBlock) item).getBlock();
            Block swapBlock = getSwapBlock();
            if(block != swapBlock)
                return;
        }
        for(BlockPos pos : blocks){
            if(!isReplaceable(pos)) continue;
            if(checkEntities(pos)) continue;
            swap.doSwap(slot, false);
            BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean());
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

    private Block getSwapBlock(){
        if(block.getValString().equals("Obsidian"))
            return Blocks.OBSIDIAN;
        return Blocks.ENDER_CHEST;
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

    private List<BlockPos> getAntiFacePlaceBlocks(){
        List<BlockPos> blocks = new ArrayList<>(16);
        blocks.addAll(Vectors.Normal.getBlocks());
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        List<BlockPosOffset> surroundingBlocks = getSurroundingBlocks(playerPos.up());
        for(BlockPosOffset posOffset : surroundingBlocks){
            BlockPos pos = posOffset.getPos();
            BlockPos up = pos.up();
            BlockPos offset = pos.offset(posOffset.getFacing());
            if(getBlock(up) == Blocks.AIR){
                blocks.add(pos);
                continue;
            }
            if(getBlock(offset) == Blocks.AIR)
                blocks.add(pos);
        }
        return blocks;
    }

    private List<BlockPosOffset> getSurroundingBlocks(BlockPos pos){
        List<BlockPosOffset> list = new ArrayList<>(16);
        list.add(new BlockPosOffset(pos.north(), EnumFacing.NORTH));
        list.add(new BlockPosOffset(pos.east(), EnumFacing.EAST));
        list.add(new BlockPosOffset(pos.south(), EnumFacing.SOUTH));
        list.add(new BlockPosOffset(pos.west(), EnumFacing.WEST));
        return list;
    }

    private Block getBlock(BlockPos pos){
        return mc.world.getBlockState(pos).getBlock();
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
        High(new Vec3d[]{
                new Vec3d(1, -1, 0),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(0, -1, -1),
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1),
                new Vec3d(1, 1, 0),
                new Vec3d(-1, 1, 0),
                new Vec3d(0, 1, 1),
                new Vec3d(0, 1, -1)
        }),
        AntiFacePlace(null),
        Dynamic(null);

        private final Vec3d[] vec3d;

        Vectors(Vec3d[] vec3d){
            this.vec3d = vec3d;
        }

        public List<BlockPos> getBlocks(){
            List<BlockPos> list = new ArrayList<>(64);
            if(this == Dynamic)
                return instance.getDynamicBlocks();
            if(this == AntiFacePlace)
                return instance.getAntiFacePlaceBlocks();
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
        None(new AbstractSwap() { @Override public void doSwap(int slot, boolean swapBack) { } }),
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

    private static class BlockPosOffset {

        private final BlockPos pos;

        private final EnumFacing facing;

        public BlockPosOffset(BlockPos pos, EnumFacing facing) {
            this.pos = pos;
            this.facing = facing;
        }

        public BlockPos getPos() {
            return pos;
        }

        public EnumFacing getFacing() {
            return facing;
        }
    }

    private enum Toggle {
        Never,
        OffGround,
        YChange,
        PositiveYChange,
        Combo,
        OnComplete
    }
}
