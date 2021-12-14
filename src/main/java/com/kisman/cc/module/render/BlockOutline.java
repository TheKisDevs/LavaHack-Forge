package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;
import i.gishreloaded.gishcode.utils.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class BlockOutline extends Module {
    public static BlockOutline instance;

    private float[] color = new float[] {0.78f, 0.62f, 0.88f, 1f};

    private String renderMode = "";

    public BlockOutline() {
        super("BlockOutline", "BlockOutline", Category.RENDER);

        instance = this;

        Kisman.instance.settingsManager.rSetting(new Setting("RenderMode", this, "Outline", new ArrayList<>(Arrays.asList("Outline", "Box", "OutlineBox", "Flat"))));

        Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));
    }

    public void update() {
        this.renderMode = Kisman.instance.settingsManager.getSettingByName(this, "RenderMode").getValString();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if(mc.objectMouseOver == null) {
            return;
        }
        if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            Block block = BlockUtils.getBlock(mc.objectMouseOver.getBlockPos());
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();

            if (Block.getIdFromBlock(block) == 0) {
                return;
            }
            if(this.renderMode.equalsIgnoreCase("OutlineBox")) {
                RenderUtil.drawBlockESP(
                        blockPos,
                        this.color[0],
                        this.color[1],
                        this.color[2]
                );
            } else if(this.renderMode.equalsIgnoreCase("Flat")) {
                RenderUtil.drawBlockFlatESP(
                        blockPos,
                        this.color[0],
                        this.color[1],
                        this.color[2]
                );
            } else if(this.renderMode.equalsIgnoreCase("Outline")) {
                RenderUtil.drawBlockOutlineESP(
                        blockPos,
                        this.color[0],
                        this.color[1],
                        this.color[2]
                );
            }
        }
    }
}
