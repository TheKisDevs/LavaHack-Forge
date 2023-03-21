package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerMove;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(
        name = "SafeWalk",
        category = Category.MOVEMENT,
        wip = true
)
public class SafeWalk extends Module {
    private static final double OFFSET = 0.001;

    @Override
    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(moveListener);
    }

    @Override
    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(moveListener);
    }

    private final Listener<EventPlayerMove> moveListener = new Listener<>(event -> {
        if(!mc.player.onGround || !mc.world.getCollisionBoxes(mc.player, mc.player.boundingBox.offset(event.x, -0.5, event.x).expand(-OFFSET, 0, -OFFSET)).isEmpty()){
            return;
        }
        AxisAlignedBB boundingBox = mc.player.boundingBox;
        if(event.x > 0 && Math.floor(boundingBox.minX + event.x) > Math.floor(boundingBox.minX)){
            event.x = Math.ceil(boundingBox.minX) - boundingBox.minX - OFFSET;
        }
        if(event.x < 0 && Math.floor(boundingBox.maxX + event.x) < Math.floor(boundingBox.maxX)){
            event.x = Math.floor(boundingBox.maxX) - boundingBox.maxX + OFFSET;
        }
        if(event.z > 0 && Math.floor(boundingBox.maxZ + event.z) > Math.floor(boundingBox.maxZ)){
            event.z = Math.ceil(boundingBox.maxX) - boundingBox.maxZ - OFFSET;
        }
        if(event.z < 0 && Math.floor(boundingBox.minZ + event.z) < Math.floor(boundingBox.minZ)){
            event.z = Math.floor(boundingBox.minZ) - boundingBox.minZ + OFFSET;
        }
        /*
        if(event.z > 0 && Math.floor(boundingBox.minZ + event.z) > Math.floor(boundingBox.minZ)){
            event.z = Math.ceil(boundingBox.minZ) - boundingBox.minZ - OFFSET;
        }
        if(event.z < 0 && Math.floor(boundingBox.maxZ + event.z) < Math.floor(boundingBox.maxZ)){
            event.x = Math.floor(boundingBox.maxZ) - boundingBox.maxZ + OFFSET;
        }
         */
        event.cancel();
        /*
        double dX = mc.player.posX - Math.floor(mc.player.posX);
        double dZ = mc.player.posZ - Math.floor(mc.player.posZ);
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        BlockPos checkNorth = playerPos.down().north();
        BlockPos checkEast = playerPos.down().east();
        BlockPos checkSouth = playerPos.down().south();
        BlockPos checkWest = playerPos.down().west();
        if(dX >= 1.0 && isReplaceable(checkEast) && mc.player.motionX > 0.0)
            event.x = 0.0;
        if(dX <= 0.0 && isReplaceable(checkWest) && mc.player.motionY < 0.0)
            event.x = 0.0;
        if(dZ >= 1.0 && isReplaceable(checkSouth) && mc.player.motionZ > 0.0)
            event.z = 0.0;
        if(dZ <= 0.0 && isReplaceable(checkNorth) && mc.player.motionZ < 0.0)
            event.z = 0.0;
        event.cancel();
         */
    });

    private boolean isReplaceable(BlockPos pos){
        return mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos);
    }
}
