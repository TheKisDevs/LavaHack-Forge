package com.kisman.cc.features.module.player;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import net.minecraft.util.math.RayTraceResult;

public class AutoMine extends Module {

    public AutoMine(){
        super("AutoMine", Category.PLAYER);
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null)
            return;

        RayTraceResult result = mc.objectMouseOver;
        if(result == null)
            return;
        mc.playerController.onPlayerDamageBlock(result.getBlockPos(), result.sideHit);
    }
}
