package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.BlockUtil;
import com.kisman.cc.util.EntityUtil;
import com.kisman.cc.util.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Flatten extends Module {

    private final Setting enemyRange = register(new Setting("EnemyRange", this, 8, 2, 15, false));
    private final Setting placeRange = register(new Setting("PlaceRange", this, 5, 1, 6, false));
    private final Setting doubleDown = register(new Setting("DoubleDown", this, false));
    private final Setting predictTicks = register(new Setting("PredictTicks", this, 1, 0, 10, true));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));
    private final Setting antiGlitch = register(new Setting("AntiGlitch", this, false));

    public Flatten(){
        super("Flatten", Category.COMBAT);
    }

    private List<BlockPos> blockPositions = new ArrayList<>();

    BlockPos targetPos;

    EntityPlayer target;

    EntityPlayer prevTarget = null;

    double posY;

    boolean overwrite = false;

    int slot;

    @Override
    public void update(){
        if(mc.world == null || mc.player == null)
            return;

        blockPositions.clear();

        slot = InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);

        if(slot == -1)
            return;

        EntityPlayer target = EntityUtil.getTarget(enemyRange.getValFloat());

        if(target == null)
            return;

        if(prevTarget != target || overwrite){
            prevTarget = target;
            posY = target.posY - 1.0;
        }

        targetPos = new BlockPos(target.posX, posY + 1.0, target.posZ);

        blockPositions = doPredictions(target );

        int oldSlot = mc.player.inventory.currentItem;

        doSwitch(slot, false);

        for(BlockPos pos : blockPositions){
            place(pos);
        }

        doSwitch(oldSlot, true);

        mc.playerController.updateController();
    }

    @Override
    public void onDisable(){
        target = null;
        prevTarget = null;
        overwrite = false;
    }

    /*
    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.world == null || mc.player == null)
            return;

        if(!isToggled())
            return;

        for(BlockPos pos : doPredictions(mc.player, (int) mc.player.posY - 1)){
            AxisAlignedBB aabb = Rendering.correct(new AxisAlignedBB(pos));
            Rendering.draw(aabb, 2f, new Colour(255, 255, 255, 120), Rendering.DUMMY_COLOR, Rendering.Mode.BOTH);
        }
    }
     */

    // what a spaghetti code mess xD
    private void reachTarget(){
        if(mc.world.getBlockState(targetPos.down()).getBlock().isReplaceable(mc.world, targetPos.down()))
            return;

        if(isSolid(targetPos.north().down())){
            place(targetPos.down());
            overwrite = false;
        }else if(isSolid(targetPos.east().down())){
            overwrite = false;
            place(targetPos.down());
        }else if(isSolid(targetPos.south().down())){
            place(targetPos.down());
            overwrite = false;
        }else if(isSolid(targetPos.west().down())){
            place(targetPos.down());
            overwrite = false;
        }else if(isSolid(targetPos.down().down())) {
            place(targetPos.down());
            overwrite = false;
        } else if(!doubleDown.getValBoolean()){
            // player unreachable
            overwrite = true;
        } else if(doubleDown.getValBoolean()){
            if(isSolid(targetPos.north().down().down())){
                place(targetPos.down().down());
                overwrite = false;
            }else if(isSolid(targetPos.east().down().down())){
                place(targetPos.down().down());
                overwrite = false;
            }else if(isSolid(targetPos.south().down().down())){
                place(targetPos.down().down());
                overwrite = false;
            }else if(isSolid(targetPos.west().down().down())){
                place(targetPos.down());
                overwrite = false;
            }else if(isSolid(targetPos.down().down().down())){
                place(targetPos.down().down());
                overwrite = false;
            } else {
                // player unreachable
                overwrite = true;
            }
        }
    }

    private boolean isSolid(BlockPos pos){
        Block block = mc.world.getBlockState(pos).getBlock();
        return !block.isReplaceable(mc.world, pos);
    }

    private void doSwitch(int slot, boolean swapBack){
        // for the time being, modes will be added
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
    }

    private void place(BlockPos pos){
        double range = placeRange.getValDouble();
        if(pos.distanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) > (range * range))
            return;
        int oldSlot = mc.player.inventory.currentItem;
        //doSwitch(slot, false);
        BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND,rotate.getValBoolean(), packet.getValBoolean(), false);
        if(antiGlitch.getValBoolean())
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
        //doSwitch(oldSlot, true);
    }

    private List<BlockPos> doPredictions(Entity entity){
        List<BlockPos> positions = new ArrayList<>();
        double x = entity.posX;
        double z = entity.posZ;
        double motionX = 0;
        double motionZ = 0;
        for(int i = 0; i < predictTicks.getValInt(); i++){
            motionX += entity.motionX;
            motionZ += entity.motionZ;
            check(x + motionX, posY, z + motionZ, positions);
        }
        return positions;
    }

    private void check(double x, double y, double z, List<BlockPos> positions){
        BlockPos pos1 = new BlockPos(x + 0.3, y, z + 0.3);
        BlockPos pos2 = new BlockPos(x + 0.3, y, z - 0.3);
        BlockPos pos3 = new BlockPos(x - 0.3, y, z + 0.3);
        BlockPos pos4 = new BlockPos(x - 0.3, y, z - 0.3);
        if(isPlaceable(pos1))
            addIfAbsent(positions, pos1);
        if(isPlaceable(pos2))
            addIfAbsent(positions, pos2);
        if(isPlaceable(pos3))
            addIfAbsent(positions, pos3);
        if(isPlaceable(pos4))
            addIfAbsent(positions, pos4);
    }

    private <T> void addIfAbsent(List<T> list, T t){
        if(!list.contains(t)){
            list.add(t);
        }
    }

    private boolean isPlaceable(BlockPos pos){
        if(!mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos))
            return false;
        for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))){
            if(entity instanceof EntityItem || entity instanceof EntityXPOrb)
                continue;
            return false;
        }
        return true;
    }
}
