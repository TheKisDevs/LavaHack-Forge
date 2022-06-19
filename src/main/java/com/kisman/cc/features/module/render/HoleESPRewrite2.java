package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.world.HoleUtil;
import com.kisman.cc.util.render.cubic.BoundingBox;
import com.kisman.cc.util.render.cubic.ModulePrefixRenderPattern;
import com.kisman.cc.util.render.cubic.RenderBuilder;
import com.kisman.cc.util.render.cubic.RenderPattern;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HoleESPRewrite2 extends Module {
    private final MultiThreaddableModulePattern multiThread = new MultiThreaddableModulePattern(this);

    private final Setting oHoles = register(new Setting("Obsidian", this, true));
    private final Setting bHoles = register(new Setting("Bedrock", this, true));
    private final Setting cHoles = register(new Setting("Custom", this, true));

    private final Setting sHoles = register(new Setting("Single", this, true));
    private final Setting dHoles = register(new Setting("Double", this, true));

    private final Setting range = register(new Setting("Range", this, 12.0, 1.0, 20.0, false));

    private final Setting limit = register(new Setting("Limit", this, 0.0, 0.0, 50.0, true));

    private final Setting ignoreOwn = register(new Setting("IgnoreOwnHole", this, false));

    private final Setting obsidianRender = register(new Setting("RenderObsidian", this, true));
    private final RenderPattern oRender = new ModulePrefixRenderPattern(this, "Obsidian").init();
    private final Setting oHeight = register(new Setting("HeightObsidian", this, 1.0, 0.0, 1.0, false));

    private final Setting bedrockRender = register(new Setting("RenderBedrock", this, true));
    private final RenderPattern bRender = new ModulePrefixRenderPattern(this, "Bedrock").init();
    private final Setting bHeight = register(new Setting("HeightBedrock", this, 1.0, 0.0, 1.0, false));

    private final Setting customRender = register(new Setting("RenderCustom", this, "RenderObsidian"));
    private final RenderPattern cRender = new ModulePrefixRenderPattern(this, "Custom").init();
    private final Setting cHeight = register(new Setting("Height", this, 1.0, 0.0, 1.0, false));

    private Map<BoundingBox, Type> map = new ConcurrentHashMap<>();

    public HoleESPRewrite2(){
        super("HoleESPRewrite2", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        multiThread.reset();
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        multiThread.update(this::doHoleESPLogic);
        doHoleESP();
    }

    private void doHoleESPLogic() {
        mc.addScheduledTask(() -> map = getHoles());
    }

    private void doHoleESP() {
        for(Map.Entry<BoundingBox, Type> entry : map.entrySet()){
            BoundingBox bb = entry.getKey();
            Type type = entry.getValue();
            if(type == Type.OBSIDIAN && !obsidianRender.getValBoolean()) continue;
            if(type == Type.BEDROCK && !bedrockRender.getValBoolean()) continue;
            if(type == Type.CUSTOM && !customRender.getValBoolean()) continue;
            RenderBuilder renderBuilder = renderBuilderFor(type);
            renderBuilder.pos(bb).render();
        }
    }

    private Map<BoundingBox, Type> getHoles(){
        Map<BoundingBox, Type> holes = new ConcurrentHashMap<>();
        List<BlockPos> possibleHoles = getPossibleHoles(range.getValFloat());
        int lim = 0;
        for(BlockPos pos : possibleHoles){
            if(limit.getValInt() != 0 && lim > limit.getValInt()) break;

            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();

            if(holeType == HoleUtil.HoleType.NONE) continue;

            if(holeType == HoleUtil.HoleType.SINGLE && !sHoles.getValBoolean()) continue;
            if(holeType == HoleUtil.HoleType.DOUBLE && !dHoles.getValBoolean()) continue;

            HoleUtil.BlockSafety holeSafety = holeInfo.getSafety();
            AxisAlignedBB centerBlock = holeInfo.getCentre();

            if(centerBlock == null) return holes;

            Type type;

            if (holeSafety == HoleUtil.BlockSafety.UNBREAKABLE) {
                type = Type.BEDROCK;
            } else {
                type = Type.OBSIDIAN;
            }
            if (holeType == HoleUtil.HoleType.CUSTOM) {
                type = Type.CUSTOM;
            }

            if(type == Type.OBSIDIAN && !oHoles.getValBoolean()) continue;
            if(type == Type.BEDROCK && !bHoles.getValBoolean()) continue;
            if(type == Type.CUSTOM && !cHoles.getValBoolean()) continue;

            BoundingBox bb = adjust(holeInfo.getCentre(), type);

            holes.put(bb, type);

            lim++;
        }

        return holes;
    }

    private List<BlockPos> getPossibleHoles(float range){
        List<BlockPos> possibleHoles = new ArrayList<>(64);
        List<BlockPos> blockPosList = EntityUtil.getSphere(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), range, (int) (range + 1), false, true, 0);
        blockPosList = blockPosList.stream().sorted((o1, o2) -> {
            double a = o1.distanceSq(mc.player.posX, mc.player.posY, mc.player.posZ);
            double b = o2.distanceSq(mc.player.posX, mc.player.posY, mc.player.posZ);
            return Double.compare(a, b);
        }).collect(Collectors.toList());
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        for (BlockPos pos : blockPosList) {
            if(ignoreOwn.getValBoolean() && playerPos == pos)
                continue;
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR))
                continue;
            if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.AIR))
                continue;
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR))
                continue;
            if (mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
                possibleHoles.add(pos);
        }
        return possibleHoles;
    }

    private BoundingBox adjust(AxisAlignedBB aabb, Type type){
        BoundingBox bb = new BoundingBox(aabb);
        if(type == Type.OBSIDIAN){
            bb.maxY = bb.minY + oHeight.getValDouble();
            return bb;
        }
        if(type == Type.BEDROCK){
            bb.maxY = bb.minY + bHeight.getValDouble();
            return bb;
        }
        bb.maxY = bb.minY + cHeight.getValDouble();
        return bb;
    }

    private RenderBuilder renderBuilderFor(Type type){
        RenderBuilder renderBuilder = oRender.getRenderBuilder();
        if(type == Type.BEDROCK) renderBuilder = bRender.getRenderBuilder();
        if(type == Type.CUSTOM) renderBuilder = cRender.getRenderBuilder();
        return renderBuilder;
    }

    private enum Type {
        OBSIDIAN,
        BEDROCK,
        CUSTOM
    }
}
