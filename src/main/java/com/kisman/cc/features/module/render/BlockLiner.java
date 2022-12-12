package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.Rendering;
import com.kisman.cc.util.world.WorldUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Module for debug
 * Don't add to module manager
 */
public class BlockLiner extends Module {

    private final Setting xVal = register(new Setting("XVal", this, 4, -10, 10, true));
    private final Setting zVal = register(new Setting("ZVal", this, 4, -10, 10, true));
    private final Setting connect = register(new Setting("Connect", this, false));
    private final Setting mode = register(new Setting("Mode", this, "Inside", Arrays.asList("Inside", "Outside")));

    public BlockLiner(){
        super("BlockLiner", "dev/debug module", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.world == null || mc.player == null)
            return;

        if(!isToggled())
            return;

        System.out.println("Rendering...");

        BlockPos playerPos = new BlockPos(mc.player.posX, 4.0, mc.player.posZ);

        BlockPos toPos = playerPos.add(xVal.getValDouble(), 0, zVal.getValDouble());

        List<BlockPos> positions = WorldUtil.getBlocks(playerPos, toPos, connect.getValBoolean(), mode.getValString().equals("Outside"));

        System.out.println(positions.isEmpty());

        if(positions.isEmpty())
            return;

        for(BlockPos pos : positions){
            AxisAlignedBB aabb = Rendering.correct(new AxisAlignedBB(pos));
            Rendering.draw(aabb, 2f, new Colour(255, 255, 255, 120), Rendering.DUMMY_COLOR, Rendering.Mode.BOX_OUTLINE);
        }
    }
}
