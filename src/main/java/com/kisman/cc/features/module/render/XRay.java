package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.render.xray.BlockImplementation;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.enums.XRayBlocks;
import com.kisman.cc.util.interfaces.IBlockImplementation;
import com.kisman.cc.util.world.CrystalUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class XRay extends Module {
    public Setting range = register(new Setting("Range", this, 50, 0, 50, false));

    private final ArrayList<IBlockImplementation> implementations = new ArrayList<>(Arrays.asList(
            new BlockImplementation(XRayBlocks.Coal, this),
            new BlockImplementation(XRayBlocks.Iron, this),
            new BlockImplementation(XRayBlocks.Gold, this),
            new BlockImplementation(XRayBlocks.Lapis, this),
            new BlockImplementation(XRayBlocks.Redstone, this),
            new BlockImplementation(XRayBlocks.Diamond, this),
            new BlockImplementation(XRayBlocks.Emerald, this)
    ));

    private ArrayList<BlockPos> blocks = new ArrayList<>();

    private final MultiThreaddableModulePattern threads = threads();

    public XRay() {
        super("XRay", "Shows ores", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        threads.reset();
        blocks.clear();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        threads.update(() -> {
            ArrayList<BlockPos> list = new ArrayList<>();

            for(BlockPos pos : CrystalUtils.getSphere(mc.player, range.getValFloat(), true, false)) for(IBlockImplementation impl : implementations) if(impl.valid(pos)) list.add(pos);

            mc.addScheduledTask(() -> blocks = list);
        });

        for(BlockPos pos : blocks) {
            for(IBlockImplementation impl : implementations) {
                impl.process(pos);
            }
        }
    }
}