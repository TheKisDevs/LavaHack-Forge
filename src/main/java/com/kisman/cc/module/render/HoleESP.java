package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class HoleESP extends Module {
    private Setting render = new Setting("RenderMode", this, "");

    private Setting obby = new Setting("_ObsidianHoles", this, "ObsidianHoles");

    private Setting obbyHoles = new Setting("ObsidianHoles", this, true);
    private Setting obbyColor = new Setting("ObbyColor", this, "ObbyColor", new float[] {0, 1, 0, 1}, false);


    private Setting bedrock = new Setting("_BedrockHoles", this, "BedrockHoles");

    private Setting bedrockHoles = new Setting("BedrockHoles", this, true);
    private Setting bedrockColor = new Setting("BedrockColor", this, "BedrockColor", new float[] {0, 1, 0, 1}, false);


    private ArrayList<BlockPos> holes = new ArrayList<>();

    public HoleESP() {
        super("HoleESP", "HoleESP", Category.RENDER);

        setmgr.rSetting(obby);
        setmgr.rSetting(obbyHoles);
        setmgr.rSetting(obbyColor);

        setmgr.rSetting(bedrock);
        setmgr.rSetting(bedrockHoles);
        setmgr.rSetting(bedrockColor);
    }

    public void onEnable() {
        holes.clear();
    }

    public void onDisable() {
        holes.clear();
    }

    public void update() {

    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (mc.getRenderManager() == null || mc.getRenderManager().options == null)
            return;

    }

/*    public boolean isBlockHole(BlockPos blockpos) {
//        holeblocks = 0;
        if (mc.world.getBlockState(blockpos.add(0, 3, 0)).getBlock() == Blocks.AIR) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 2, 0)).getBlock() == Blocks.AIR) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 1, 0)).getBlock() == Blocks.AIR) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 0, 0)).getBlock() == Blocks.AIR) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN ||mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN ||mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN ||mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK) ++holeblocks;

        if (mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN ||mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK) ++holeblocks;

//        if (holeblocks >= 9) return true;
        else return false;
    }*/
}
