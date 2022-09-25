package com.kisman.cc.features.module.movement;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import net.minecraft.util.math.BlockPos;

public class SafeWalk extends Module {

    public SafeWalk(){
        super("SafeWalk", Category.MOVEMENT);
    }

    @Override
    public void update() {
        if(mc.player == null || mc.world == null)
            return;
        double dX = mc.player.posX - Math.floor(mc.player.posX);
        double dZ = mc.player.posZ - Math.floor(mc.player.posZ);
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        BlockPos checkNorth = playerPos.down().north();
        BlockPos checkEast = playerPos.down().east();
        BlockPos checkSouth = playerPos.down().south();
        BlockPos checkWest = playerPos.down().west();
        if(dX >= 1.0 && isReplaceable(checkEast) && mc.player.motionX > 0.0)
            mc.player.motionX = 0.0;
        if(dX <= 0.0 && isReplaceable(checkWest) && mc.player.motionY < 0.0)
            mc.player.motionX = 0.0;
        if(dZ >= 1.0 && isReplaceable(checkSouth) && mc.player.motionZ > 0.0)
            mc.player.motionZ = 0.0;
        if(dZ <= 0.0 && isReplaceable(checkNorth) && mc.player.motionZ < 0.0)
            mc.player.motionZ = 0.0;
        ChatUtility.info().printClientModuleMessage("doing things");
    }

    private boolean isReplaceable(BlockPos pos){
        return mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos);
    }
}
