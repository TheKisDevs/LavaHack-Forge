package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.BlockInteractionHelper;
import com.kisman.cc.util.PlayerUtil;
import com.kisman.cc.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class XRay extends Module {
    public static XRay instance;

    public Block[] xrayBlocks = new Block[]{
            Blocks.COAL_ORE,
            Blocks.IRON_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.LAPIS_ORE,
            Blocks.DIAMOND_ORE
    };

    public Setting range = new Setting("Range", this, 50, 0, 100, false);

    public XRay() {
        super("XRay", "", Category.RENDER);

        instance = this;

        setmgr.rSetting(range);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        for(BlockPos pos : BlockInteractionHelper.getSphere(PlayerUtil.GetLocalPlayerPosFloored(), (float) range.getValDouble(), range.getValInt(), false, true, 0)) {
            for(int i = 0; i < xrayBlocks.length; i++) {
                if(mc.world.getBlockState(pos).getBlock() == xrayBlocks[i]) {
                    RenderUtil.drawBlockESP(pos, 1, 1, 1);
                }
            }
        }
    }
}
