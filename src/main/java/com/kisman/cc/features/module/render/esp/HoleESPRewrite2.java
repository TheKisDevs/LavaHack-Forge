package com.kisman.cc.features.module.render.esp;

import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.util.FadeRenderingRewritePattern;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.client.interfaces.Drawable;
import com.kisman.cc.util.enums.FadeLogic;
import com.kisman.cc.util.render.objects.world.Box;
import com.kisman.cc.util.world.HoleUtil;
import com.kisman.cc.util.world.WorldUtilKt;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;

@ModuleInfo(
        name = "HoleESPRewrite2",
        display = "Holes",
        desc = "Highlights holes",
        submodule = true
)
public class HoleESPRewrite2 extends Module implements Drawable {
    private final MultiThreaddableModulePattern threads = threads();

    private final Setting oHoles = register(new Setting("Obsidian", this, true).setTitle("Obby"));
    private final Setting bHoles = register(new Setting("Bedrock", this, true));
    private final Setting cHoles = register(new Setting("Custom", this, true));

    private final Setting sHoles = register(new Setting("Single", this, true).setTitle("1x1"));
    private final Setting dHoles = register(new Setting("Double", this, true).setTitle("2x1"));

    private final Setting range = register(new Setting("Range", this, 12.0, 1.0, 20.0, false));

    private final Setting limit = register(new Setting("Limit", this, 0.0, 0.0, 50.0, true));

    private final Setting ignoreOwn = register(new Setting("IgnoreOwnHole", this, false));

    private final SettingGroup obbyRendererGroup = register(new SettingGroup(new Setting("Obby", this)));
    private final FadeRenderingRewritePattern obbyRenderer = new FadeRenderingRewritePattern(this, FadeLogic.Distance, false).prefix("Obsidian").group(obbyRendererGroup).preInit().init();
    private final Setting oHeight = register(new Setting("HeightObsidian", this, 1.0, 0.0, 1.0, false));

    private final SettingGroup bedrockRendererGroup = register(new SettingGroup(new Setting("Bedrock", this)));
    private final FadeRenderingRewritePattern bedrockRenderer = new FadeRenderingRewritePattern(this, FadeLogic.Distance, false).prefix("Bedrock").group(bedrockRendererGroup).preInit().init();
    private final Setting bHeight = register(new Setting("HeightBedrock", this, 1.0, 0.0, 1.0, false));

    private final SettingGroup customRendererGroup = register(new SettingGroup(new Setting("Custom", this)));
    private final FadeRenderingRewritePattern customRenderer = new FadeRenderingRewritePattern(this, FadeLogic.Distance, false).prefix("Custom").group(customRendererGroup).preInit().init();
    private final Setting cHeight = register(new Setting("Height", this, 1.0, 0.0, 1.0, false));

    private static final Comparator<AxisAlignedBB> comparator = (o1, o2) -> Double.compare(mc.player.getDistanceSq(o2.getCenter().x, o2.getCenter().y, o2.getCenter().z), mc.player.getDistanceSq(o1.getCenter().x, o1.getCenter().y, o1.getCenter().z));

    // sorted map solves flickering problem i hope
    private Map<AxisAlignedBB, Type> map = new TreeMap<>(comparator);
    private Map<AxisAlignedBB, Long> timeStamps = new TreeMap<>(comparator);

    @ModuleInstance
    public static HoleESPRewrite2 instance;

    @Override
    public void onEnable() {
        super.onEnable();
        threads.reset();
    }

    @Override
    public void onDisable(){
        super.onDisable();
        map.clear();
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        threads.update(this::doHoleESPLogic);

        if(rendererFor(Type.OBSIDIAN).canRender() || rendererFor(Type.BEDROCK).canRender() || rendererFor(Type.CUSTOM).canRender()) {
            doHoleESP(false);
        }
    }

    @Override
    public void draw() {
        doHoleESP(true);
    }

    private void doHoleESPLogic() {
        mc.addScheduledTask(() -> {
            Map<AxisAlignedBB, Type> holes = getHoles();
            Map<AxisAlignedBB, Long> timeStamps = new TreeMap<>(comparator);

            for(AxisAlignedBB bb : holes.keySet()) timeStamps.put(bb, System.currentTimeMillis());

            map = holes;
            this.timeStamps = timeStamps;
        });
    }

    public void doHoleESP(boolean callingFromDraw/*i will need it later...*/) {
        Map<AxisAlignedBB, Type> renderNormally = new TreeMap<>(comparator);
        renderNormally.putAll(map);
        for(Map.Entry<AxisAlignedBB, Type> entry : renderNormally.entrySet()) {
            AxisAlignedBB bb = entry.getKey();
            Type type = entry.getValue();
            Vec3d center = Box.byAABB(bb).center();
            try {
                if((callingFromDraw && !rendererFor(type).canRender()) || (!callingFromDraw && rendererFor(type).canRender())) rendererFor(type).draw(bb, timeStamps.get(bb), range.getValFloat(), (float) mc.player.getDistance(center.x, center.y, center.z));
            } catch(Exception ignored) {}
        }
    }

    private Map<AxisAlignedBB, Type> getHoles(){
        Map<AxisAlignedBB, Type> holes = new TreeMap<>(comparator);
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

            AxisAlignedBB bb = adjust(holeInfo.getCentre(), type);

            if(!holes.containsKey(bb))
                holes.put(bb, type);

            lim++;
        }

        return holes;
    }

    private List<BlockPos> getPossibleHoles(float range){
        List<BlockPos> possibleHoles = new ArrayList<>(64);
        List<BlockPos> blockPosList = WorldUtilKt.sphere((int) range);
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

    private AxisAlignedBB adjust(AxisAlignedBB bb, Type type){
        double maxY;

        if(type == Type.OBSIDIAN) maxY = bb.minY + oHeight.getValDouble();
        else if(type == Type.BEDROCK) maxY = bb.minY + bHeight.getValDouble();
        else maxY = bb.minY + cHeight.getValDouble();

        return new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, maxY, bb.maxZ);
    }

    private FadeRenderingRewritePattern rendererFor(Type type) {
        if(type == Type.BEDROCK) return bedrockRenderer;
        if(type == Type.CUSTOM) return customRenderer;
        return obbyRenderer;
    }

    private enum Type {
        OBSIDIAN,
        BEDROCK,
        CUSTOM
    }
}
