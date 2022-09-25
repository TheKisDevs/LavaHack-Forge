package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.util.BoxRendererPattern;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.ColourUtilKt;
import com.kisman.cc.util.entity.player.PlayerUtil;
import com.kisman.cc.util.thread.TaskQueue;
import com.kisman.cc.util.world.BlockInteractionHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class XRay extends Module {
    public Setting range = register(new Setting("Range", this, 50, 0, 50, false));
    private final Setting colorAlpha = register(new Setting("Color Alpha", this, 255, 0, 255, true));

    private final SettingGroup blocks = register(new SettingGroup(new Setting("Blocks", this)));

    public Setting coal = register(blocks.add(new Setting("Coal", this, false)));
    public Setting iron = register(blocks.add(new Setting("Iron", this, false)));
    public Setting gold = register(blocks.add(new Setting("Gold", this, false)));
    public Setting redstone = register(blocks.add(new Setting("Redstone", this, false)));
    public Setting lapis = register(blocks.add(new Setting("Lapis", this, false)));
    public Setting diamond = register(blocks.add(new Setting("Diamond", this, false)));
    public Setting emerald = register(blocks.add(new Setting("Emerald", this, false)));

    private final MultiThreaddableModulePattern multiThread = threads();
    private final BoxRendererPattern renderer = new BoxRendererPattern(this).init();

    private final TaskQueue queue = new TaskQueue();

    public XRay() {
        super("XRay", "Shows ores", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        multiThread.reset();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        multiThread.update(() -> doXRay(event.getPartialTicks()));
    }

    private void doXRay(float ticks) {
        queue.clear();
        for(BlockPos pos : BlockInteractionHelper.getSphere(PlayerUtil.GetLocalPlayerPosFloored(), (float) range.getValDouble(), range.getValInt(), false, true, 0)) renderBlock(pos, ticks);
        while(queue.hasMoreTasks()) {
            queue.runCur();
        }
    }

    private void renderBlock(BlockPos pos, float ticks) {
        queue.add(() -> {
            if(mc.world.getBlockState(pos).getBlock() == Blocks.COAL_ORE && coal.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getCoalOreColor(), pos, colorAlpha.getValInt());
            else if(mc.world.getBlockState(pos).getBlock() == Blocks.IRON_ORE && iron.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getIronOreColor(), pos, colorAlpha.getValInt());
            else if(mc.world.getBlockState(pos).getBlock() == Blocks.GOLD_ORE && gold.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getGoldOreColor(), pos, colorAlpha.getValInt());
            else if((mc.world.getBlockState(pos).getBlock() == Blocks.REDSTONE_ORE || mc.world.getBlockState(pos).getBlock() == Blocks.LIT_REDSTONE_ORE) && redstone.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getRedstoneOreColor(), pos, colorAlpha.getValInt());
            else if(mc.world.getBlockState(pos).getBlock() == Blocks.LAPIS_ORE && lapis.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getLapisOreColor(), pos, colorAlpha.getValInt());
            else if(mc.world.getBlockState(pos).getBlock() == Blocks.DIAMOND_ORE && diamond.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getDiamondOreColor(), pos, colorAlpha.getValInt());
            else if(mc.world.getBlockState(pos).getBlock() == Blocks.EMERALD_ORE && emerald.getValBoolean()) renderer.draw(ticks, ColourUtilKt.BlockColors.Companion.getEmeraldOreColor(), pos, colorAlpha.getValInt());
        });
    }
}