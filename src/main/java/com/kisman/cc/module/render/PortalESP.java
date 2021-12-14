package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import net.minecraft.init.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class PortalESP extends Module {
    private Setting range = new Setting("Range", this, 50, 0, 100, false);

    public PortalESP() {
        super("PortalESP", "esp on portal blocks", Category.RENDER);

        setmgr.rSetting(range);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        ArrayList<BlockPos> blocks = new ArrayList<>();

        for(BlockPos pos : BlockInteractionHelper.getSphere(PlayerUtil.GetLocalPlayerPosFloored(), (float) range.getValDouble(), range.getValInt(), false, true, 0)) {
            if(mc.world.getBlockState(pos).getBlock() == Blocks.PORTAL) {
                blocks.add(pos);
            }
        }

        for(BlockPos pos : blocks) {
            RenderUtil.drawBlockESP(pos, 0.67f, 0, 1);
        }
    }
}
