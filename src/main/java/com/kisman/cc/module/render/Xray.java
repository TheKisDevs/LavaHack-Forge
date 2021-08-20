package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.*;
import com.kisman.cc.util.RenderUtil;

import i.gishreloaded.gishcode.utils.*;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import i.gishreloaded.gishcode.xray.*;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class Xray extends Module{
    public TimerUtils timer;
	
	LinkedList<XRayBlock> blocks = new LinkedList<XRayBlock>();

    public Xray() {
        super("Xray", "ebanuy chiter",Category.RENDER);

        timer = new TimerUtils();

        Kisman.instance.settingsManager.rSetting(new Setting("Distance", this, 50, 4, 100, true));
        Kisman.instance.settingsManager.rSetting(new Setting("UpdateDelay", this, 100, 0, 300, true));
    }

    public void onEnable() {
        blocks.clear();
    }

    public void update() {
        int distance = (int) Kisman.instance.settingsManager.getSettingByName(this, "Distance").getValDouble();
        int delay = (int) Kisman.instance.settingsManager.getSettingByName(this, "UpdateDelay").getValDouble();

        if(!timer.isDelay(delay)) {
            return;
        }

        blocks.clear();

        for(XRayData data : XRayManager.xrayList) {
            for(BlockPos blockPos : BlockUtils.findBlocksNearEntity(Wrapper.INSTANCE.player(), data.getId(), data.getMeta(), distance)) {
                XRayBlock xRayBlock = new XRayBlock(blockPos, data);
                blocks.add(xRayBlock);
            }
        }
        timer.setLastMS();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        RenderUtil.drawXRayBlocks(blocks, event.getPartialTicks());
    }
}
