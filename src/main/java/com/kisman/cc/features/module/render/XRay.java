package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.ShaderableModule;
import com.kisman.cc.features.module.render.xray.BlockImplementation;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.client.interfaces.IBlockImplementation;
import com.kisman.cc.util.enums.XRayBlocks;
import com.kisman.cc.util.world.CrystalUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class XRay extends ShaderableModule {
    public Setting range = register(new Setting("Range", this, 50, 0, 50, false));

    private final ArrayList<BlockImplementation> implementations = new ArrayList<>(Arrays.asList(
            new BlockImplementation(XRayBlocks.Coal, this, 0),
            new BlockImplementation(XRayBlocks.Iron, this, 1),
            new BlockImplementation(XRayBlocks.Gold, this, 2),
            new BlockImplementation(XRayBlocks.Lapis, this, 3),
            new BlockImplementation(XRayBlocks.Redstone, this, 4),
            new BlockImplementation(XRayBlocks.Diamond, this, 5),
            new BlockImplementation(XRayBlocks.Emerald, this, 6)
    ));

    private ArrayList<BlockPos> blocks = new ArrayList<>();

    private final MultiThreaddableModulePattern threads = threads();

    public XRay() {
        super("XRay", "Shows ores", Category.RENDER, true);
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

        handleDraw();
    }

    @Override
    public void draw0(Boolean@NotNull[] flags) {
        for(BlockPos pos : blocks) for(BlockImplementation impl : implementations) if(flags[impl.getFlag()]) impl.process(pos);
    }
}