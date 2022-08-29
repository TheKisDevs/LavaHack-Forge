package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.collections.Bind;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.math.Interpolation;
import com.kisman.cc.util.math.MathUtil;
import com.kisman.cc.util.world.HoleUtil;
import com.kisman.cc.util.render.cubic.BoundingBox;
import com.kisman.cc.util.render.cubic.ModulePrefixRenderPattern;
import com.kisman.cc.util.render.cubic.RenderBuilder;
import com.kisman.cc.util.render.cubic.RenderPattern;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;

public class HoleESPRewrite2 extends Module {
    private final MultiThreaddableModulePattern multiThread = new MultiThreaddableModulePattern(this);

    private final Setting oHoles = register(new Setting("Obsidian", this, true).setTitle("Obby"));
    private final Setting bHoles = register(new Setting("Bedrock", this, true));
    private final Setting cHoles = register(new Setting("Custom", this, true));

    private final Setting sHoles = register(new Setting("Single", this, true).setTitle("1x1"));
    private final Setting dHoles = register(new Setting("Double", this, true).setTitle("2x1"));

    private final Setting range = register(new Setting("Range", this, 12.0, 1.0, 20.0, false));

    private final Setting limit = register(new Setting("Limit", this, 0.0, 0.0, 50.0, true));

    private final Setting ignoreOwn = register(new Setting("IgnoreOwnHole", this, false));

    private final RenderPattern oRender = new ModulePrefixRenderPattern(this, "Obsidian").init();
    private final Setting oHeight = register(new Setting("HeightObsidian", this, 1.0, 0.0, 1.0, false));

    private final RenderPattern bRender = new ModulePrefixRenderPattern(this, "Bedrock").init();
    private final Setting bHeight = register(new Setting("HeightBedrock", this, 1.0, 0.0, 1.0, false));

    private final RenderPattern cRender = new ModulePrefixRenderPattern(this, "Custom").init();
    private final Setting cHeight = register(new Setting("Height", this, 1.0, 0.0, 1.0, false));

    private final Setting fadeIn = register(new Setting("FadeIn", this, false));
    private final Setting fadeInTicks = register(new Setting("FadeInTicks", this, 200, 0, 500, true));
    private final Setting fadeInCool = register(new Setting("FadeInCool", this, false));

    private final Setting fadeOut = register(new Setting("FadeOut", this, false));
    private final Setting fadeOutTicks = register(new Setting("FadeOutTicks", this, 200, 0, 500, true));
    private final Setting fadeOutCool = register(new Setting("FadeOutCool", this, false));

    private static final Comparator<BoundingBox> comparator = (o1, o2) -> Double.compare(mc.player.getDistanceSq(o2.getCenter().x, o2.getCenter().y, o2.getCenter().z), mc.player.getDistanceSq(o1.getCenter().x, o1.getCenter().y, o1.getCenter().z));

    private static final Comparator<Bind<BoundingBox, Type>> comparatorBind = (o1, o2) -> Double.compare(mc.player.getDistanceSq(o2.getFirst().getCenter().x, o2.getFirst().getCenter().y, o2.getFirst().getCenter().z), mc.player.getDistanceSq(o1.getFirst().getCenter().x, o1.getFirst().getCenter().y, o1.getFirst().getCenter().z));

    // sorted map solves flickering problem i hope
    private Map<BoundingBox, Type> map = new TreeMap<>(comparator);

    private Map<Bind<BoundingBox, Type>, Double> newHoles = new TreeMap<>(comparatorBind);

    private Map<Bind<BoundingBox, Type>, Double> oldHoles = new TreeMap<>(comparatorBind);

    public HoleESPRewrite2(){
        super("HoleESPRewrite2", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        multiThread.reset();
    }

    @Override
    public void onDisable(){
        super.onDisable();
        map.clear();
        newHoles.clear();
        oldHoles.clear();
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        multiThread.update(this::doHoleESPLogic);
        doHoleESP();
    }

    private void doHoleESPLogic() {
        mc.addScheduledTask(() -> {
            Map<BoundingBox, Type> holes = getHoles();
            if(fadeOut.getValBoolean()){
                for(Map.Entry<BoundingBox, Type> entry : map.entrySet()){
                    if(!holes.containsKey(entry.getKey()))
                        oldHoles.put(new Bind<>(entry.getKey(), entry.getValue()), 0.0);
                }
            } else {
                oldHoles.clear();
            }
            if(fadeIn.getValBoolean()){
                for(Map.Entry<BoundingBox, Type> entry : holes.entrySet()){
                    if(!map.containsKey(entry.getKey()))
                        newHoles.put(new Bind<>(entry.getKey(), entry.getValue()), 0.0D);
                }
            } else {
                newHoles.clear();
            }
            map = holes;
        });
    }

    private void doHoleESP() {
        Map<BoundingBox, Type> renderNormally = new TreeMap<>(comparator);
        renderNormally.putAll(map);
        Map<Bind<BoundingBox, Type>, Double> requestAdd = new TreeMap<>(comparatorBind);
        Map<Bind<BoundingBox, Type>, Double> requestRenderFadeIn = new TreeMap<>(comparatorBind);
        if(fadeIn.getValBoolean()){
            Map<Bind<BoundingBox, Type>, Double> newMap = new TreeMap<>(comparatorBind);
            for(Map.Entry<Bind<BoundingBox, Type>, Double> entry : newHoles.entrySet()){
                Vec3d vec3d = entry.getKey().getFirst().getCenter();
                boolean canAdd = true;
                if(mc.player.getDistance(vec3d.x, vec3d.y, vec3d.z) > range.getValDouble()){
                    requestAdd.put(entry.getKey(), entry.getValue());
                    canAdd = false;
                }
                if(checkCompleted(entry.getValue(), true)){
                    renderNormally.put(entry.getKey().getFirst(), entry.getKey().getSecond());
                    continue;
                }
                BoundingBox bb = entry.getKey().getFirst();
                double diffX = bb.maxX - bb.minX;
                double diffY = bb.maxY - bb.minY;
                double diffZ = bb.maxZ - bb.minZ;
                BoundingBox boundingBox = bb.scaleNew(entry.getValue() * diffX, entry.getValue() * diffY, entry.getValue() * diffZ);
                RenderBuilder renderBuilder = renderBuilderFor(entry.getKey().getSecond());
                renderBuilder.pos(boundingBox).render();
                //ChatUtility.info().printClientModuleMessage("Double: " + entry.getValue().toString()); // for debug only
                double newDouble;
                if(fadeInCool.getValBoolean())
                    newDouble = MathUtil.lerp(entry.getValue(), 1.0, 1.0 / fadeInTicks.getValDouble());
                else
                    newDouble = entry.getValue() + (1.0 / fadeInTicks.getValDouble());
                if(canAdd)
                    newMap.put(entry.getKey(), newDouble);
            }
            newHoles = newMap;
        }
        if(fadeOut.getValBoolean()){
            Map<Bind<BoundingBox, Type>, Double> newMap = new TreeMap<>(comparatorBind);
            for(Map.Entry<Bind<BoundingBox, Type>, Double> entry : oldHoles.entrySet()){
                Vec3d vec3d = entry.getKey().getFirst().getCenter();
                boolean canAdd = true;
                if(mc.player.getDistance(vec3d.x, vec3d.y, vec3d.z) > range.getValDouble()){
                    requestRenderFadeIn.put(entry.getKey(), entry.getValue());
                    canAdd = false;
                }
                if(checkCompleted(entry.getValue(), false))
                    continue;
                BoundingBox bb = entry.getKey().getFirst();
                double diffX = bb.maxX - bb.minX;
                double diffY = bb.maxY - bb.minY;
                double diffZ = bb.maxZ - bb.minZ;
                BoundingBox boundingBox = bb.scaleNew(1.0 - (entry.getValue() * diffX), 1.0 - (entry.getValue() * diffY), 1.0 - (entry.getValue() * diffZ));
                RenderBuilder renderBuilder = renderBuilderFor(entry.getKey().getSecond());
                renderBuilder.pos(boundingBox).render();
                double newDouble;
                if(fadeOutCool.getValBoolean())
                    newDouble = MathUtil.lerp(entry.getValue(), 1.0, 1.0 / fadeInTicks.getValDouble());
                else
                    newDouble = entry.getValue() + (1.0 / fadeInTicks.getValDouble());
                if(canAdd)
                    newMap.put(entry.getKey(), newDouble);
            }
            oldHoles = newMap;
            oldHoles.putAll(requestAdd);

            newHoles.putAll(requestRenderFadeIn);
        }
        for(Map.Entry<BoundingBox, Type> entry : renderNormally.entrySet()) {
            if (fadeIn.getValBoolean() && newHoles.containsKey(new Bind<>(entry.getKey(), entry.getValue())))
                continue;
            if (fadeOut.getValBoolean() && oldHoles.containsKey(new Bind<>(entry.getKey(), entry.getValue())))
                continue;
            BoundingBox bb = entry.getKey();
            Type type = entry.getValue();
            RenderBuilder renderBuilder = renderBuilderFor(type);
            renderBuilder.pos(bb).render();
        }
    }

    private boolean checkCompleted(double a, boolean fadeIn){
        if(fadeIn){
            if(fadeInCool.getValBoolean())
                return Interpolation.isAlmostZero(1.0 - a, Interpolation.ALMOST_ZERO);
            return a >= 1.0;
        }
        if(fadeOutCool.getValBoolean())
            return Interpolation.isAlmostZero(1.0 - a, Interpolation.ALMOST_ZERO);
        return a >= 1.0;
    }

    private Map<BoundingBox, Type> getHoles(){
        Map<BoundingBox, Type> holes = new TreeMap<>(comparator);
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

            if(!holes.containsKey(bb))
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
