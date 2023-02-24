package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.render.cubic.BoundingBox;
import com.kisman.cc.util.render.cubic.ModuleSuffixRenderPattern;
import com.kisman.cc.util.render.cubic.RenderBuilder;
import com.kisman.cc.util.render.cubic.RenderPattern;
import com.kisman.cc.util.world.HoleUtil;
import com.kisman.cc.util.world.WorldUtilKt;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

@Deprecated
public class HoleESPRewrite extends Module {

    private final Setting oHoles = register(new Setting("Obsidian", this, true));
    private final Setting bHoles = register(new Setting("Bedrock", this, true));

    private final Setting sHoles = register(new Setting("Single", this, true));
    private final Setting dHoles = register(new Setting("Double", this, true));
    //private final Setting cHoles = register(new Setting("Custom", this, true));

    private final Setting range = register(new Setting("Range", this, 12.0, 1.0, 20.0, false));

    private final Setting limit = register(new Setting("Limit", this, 0.0, 0.0, 50.0, true));

    private final Setting obsidianRender = register(new Setting("RenderObsidian", this, true));
    private final RenderPattern oRender = new ModuleSuffixRenderPattern(this, "").init();
    private final Setting oHeight = register(new Setting("Height", this, 1.0, 0.0, 1.0, false));

    private final Setting bedrockRender = register(new Setting("RenderBedrock", this, true));
    private final RenderPattern bRender = new ModuleSuffixRenderPattern(this, "2").init();
    private final Setting bHeight = register(new Setting("Height 2", this, 1.0, 0.0, 1.0, false));

    //private final Setting customRender = register(new Setting("RenderObsidian", this, "RenderObsidian"));
    //private final RenderPattern cRender = new ModuleRenderPattern(this);
    //private final Setting cHeight = register(new Setting("Height", this, 1.0, 0.0, 1.0, false));

    private int lim = 0;

    private List<HoleUtil.HoleInfo> holes = new ArrayList<>();

    private Map<HoleUtil.HoleInfo, RenderBuilder> renderer = new HashMap<>(128);

    public HoleESPRewrite(){
        super("HoleESPRewrite", Category.RENDER);
        //cRender.init();
    }

    @Override
    public void update(){
        if(mc.world == null || mc.player == null)
            return;

        holes = getHoleBlocks();
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.world == null || mc.player == null)
            return;

        if(!this.isToggled())
            return;

        Set<AxisAlignedBB> alreadyRendered = new HashSet<>(128);

        for(HoleUtil.HoleInfo info : holes){
            if(info == null)
                continue;
            HoleUtil.BlockSafety safety = info.getSafety();
            if(safety == null)
                return;
            if(safety == HoleUtil.BlockSafety.BREAKABLE && !bedrockRender.getValBoolean())
                continue;
            if(!obsidianRender.getValBoolean())
                continue;
            RenderBuilder render = renderer.get(info);
            if(render == null)
                continue;
            if(alreadyRendered.contains(info.getCentre()))
                continue;
            BoundingBox bb = new BoundingBox(info.getCentre());
            bb.maxY = bb.minY + oHeight.getValDouble();
            if(safety == HoleUtil.BlockSafety.UNBREAKABLE)
                bb.maxY = bb.minY + bHeight.getValDouble();
            render.pos(bb).render();
            alreadyRendered.add(info.getCentre());
        }

        alreadyRendered.clear();
    }

    private List<HoleUtil.HoleInfo> getHoleBlocks(){
        List<HoleUtil.HoleInfo> holes = new ArrayList<>(64);
        float range = this.range.getValFloat();
        Set<BlockPos> possibleHoles = getPossibleHoles(range);
        lim = 0;
        renderer.clear();
        if(sHoles.getValBoolean())
            holes.addAll(getHoleBlocksOfType(possibleHoles, HoleUtil.HoleType.SINGLE));
        if(dHoles.getValBoolean())
            holes.addAll(getHoleBlocksOfType(possibleHoles, HoleUtil.HoleType.DOUBLE));
        //if(cHoles.getValBoolean())
        //    holes.addAll(getHoleBlocksOfType(possibleHoles, HoleUtil.HoleType.CUSTOM));
        return holes;
    }

    private List<HoleUtil.HoleInfo> getHoleBlocksOfType(Set<BlockPos> possibleHoles, HoleUtil.HoleType type){
        List<HoleUtil.HoleInfo> holes = new ArrayList<>(32);
        for(BlockPos pos : possibleHoles){
            if(limit.getValInt() != 0 && lim > limit.getValInt())
                break;
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            HoleUtil.BlockSafety safety = holeInfo.getSafety();
            if(holeType != type)
                continue;
            if(safety == HoleUtil.BlockSafety.UNBREAKABLE && bHoles.getValBoolean()){
                holes.add(holeInfo);
                renderer.put(holeInfo, bRender.getRenderBuilder());
                lim++;
                continue;
            }
            if(!oHoles.getValBoolean())
                continue;
            holes.add(holeInfo);
            renderer.put(holeInfo, oRender.getRenderBuilder());
            lim++;
        }
        return holes;
    }

    private Set<BlockPos> getPossibleHoles(float range){
        Set<BlockPos> possibleHoles = new HashSet<>();
        List<BlockPos> blockPosList = WorldUtilKt.sphere((int) range);
        for (BlockPos pos : blockPosList) {
            AxisAlignedBB aabb = new AxisAlignedBB(pos);
            if(!mc.world.getEntitiesWithinAABB(Entity.class, aabb).isEmpty())
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
}
