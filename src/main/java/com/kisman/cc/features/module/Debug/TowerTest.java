package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import net.minecraft.util.math.BlockPos;

public class TowerTest extends Module {

    public TowerTest(){
        super("TowerTest", Category.DEBUG);
    }

    private int lastY;

    @Override
    public void onEnable(){
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }
        lastY = (int) mc.player.posY;
        mc.player.jump();
    }

    @Override
    public void onDisable(){
        lastY = 0;
    }

    public void update(){
        if(mc.player == null || mc.world == null)
            return;

        if((int) mc.player.posY <= lastY)
            return;

        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        //TODO: BlockUtil2.placeBlock usage
//        if(mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos))
//            BlockUtil.placeBlock2(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ).down(), EnumHand.MAIN_HAND, false, false);

        if(mc.player.onGround){
            mc.player.jump();
            lastY = pos.getY();
        } else {
            mc.player.motionY = -0.28;
        }
    }
}
