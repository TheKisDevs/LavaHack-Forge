package com.kisman.cc.features.module.Debug;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.collections.Pair;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ScaffoldTest extends Module {

    public ScaffoldTest(){
        super("ScaffoldTest", Category.DEBUG);
    }

    private Vec3d oldPlayerPos;

    @Override
    public void onEnable(){
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }
        oldPlayerPos = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
    }

    @Override
    public void onDisable(){
        oldPlayerPos = null;
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null)
            return;

        //BlockPos oldPos = new BlockPos(oldPlayerPos).down();

        BlockPos oldPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ).down();

        BlockPos playerPos = new BlockPos(mc.player.posX + mc.player.motionX, mc.player.posY, mc.player.posZ + mc.player.motionZ).down();

        Queue<BlockPos> queue = new LinkedList<>();

        if(unconnected(oldPos, playerPos)){
            ChatUtility.info().printClientModuleMessage("Unconnected");
            addConnectingBlocks(oldPos, playerPos, queue);
        }

        //queue.add(playerPos.down());

        queue.add(playerPos);

        for(BlockPos pos : queue){
            if(!mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos))
                continue;
            BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, false, false);
        }
    }

    public boolean unconnected(BlockPos pos1, BlockPos pos2){
        List<BlockPos> neighbourBlocks = new ArrayList<>();
        neighbourBlocks.add(pos1.north());
        neighbourBlocks.add(pos1.east());
        neighbourBlocks.add(pos1.south());
        neighbourBlocks.add(pos1.west());
        return !neighbourBlocks.contains(pos2);
    }

    public void addConnectingBlocks(BlockPos pos1, BlockPos pos2, Queue<BlockPos> queue){
        Pair<EnumFacing> pair = getPossibleConnectingFacings(pos1, pos2);
        ChatUtility.info().printClientModuleMessage(pair.getFirst().toString() + " / " + pair.getSecond().toString());
        BlockPos p1 = pos1.offset(pair.getFirst());
        BlockPos p2 = pos1.offset(pair.getSecond());
        if(mc.player.getDistanceSq(p1.getX() + 0.5, p1.getY(), p1.getZ() + 0.5) < mc.player.getDistanceSq(p2.getX() + 0.5, p2.getY(), p2.getZ() + 0.5))
            queue.add(p1);
        else
            queue.add(p2);
        if(pos1.getY() < pos2.getY())
            queue.add(pos2.down());
    }

    private Pair<EnumFacing> getPossibleConnectingFacings(BlockPos pos1, BlockPos pos2){
        List<EnumFacing> possibleFacings = new ArrayList<>();
        if(pos1.getX() < pos2.getX())
            possibleFacings.add(EnumFacing.EAST);
        else
            possibleFacings.add(EnumFacing.WEST);
        if(pos1.getZ() < pos2.getZ())
            possibleFacings.add(EnumFacing.SOUTH);
        else
            possibleFacings.add(EnumFacing.NORTH);
        return new Pair<>(possibleFacings.get(0), possibleFacings.get(1));
    }
}
