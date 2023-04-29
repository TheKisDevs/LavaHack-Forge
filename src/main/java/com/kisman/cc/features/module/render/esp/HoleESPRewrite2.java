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
import com.kisman.cc.util.world.Holes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

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
    private final Setting qHoles = register(new Setting("Quad", this, false).setTitle("2x2"));

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
    private Map<AxisAlignedBB, Holes.Safety> map = new TreeMap<>(comparator);
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

        if(rendererFor(Holes.Safety.Obsidian).canRender() || rendererFor(Holes.Safety.Bedrock).canRender() || rendererFor(Holes.Safety.Mix).canRender()) {
            doHoleESP(false);
        }
    }

    @Override
    public void draw() {
        doHoleESP(true);
    }

    private void doHoleESPLogic() {
        mc.addScheduledTask(() -> {
            Map<AxisAlignedBB, Holes.Safety> holes = getHoles();
            Map<AxisAlignedBB, Long> timeStamps = new TreeMap<>(comparator);

            for(AxisAlignedBB bb : holes.keySet()) timeStamps.put(bb, System.currentTimeMillis());

            map = holes;
            this.timeStamps = timeStamps;
        });
    }

    public void doHoleESP(boolean callingFromDraw/*i will need it later...*/) {
        Map<AxisAlignedBB, Holes.Safety> renderNormally = new TreeMap<>(comparator);
        renderNormally.putAll(map);
        for(Map.Entry<AxisAlignedBB, Holes.Safety> entry : renderNormally.entrySet()) {
            AxisAlignedBB bb = entry.getKey();
            Holes.Safety safety = entry.getValue();
            Vec3d center = Box.byAABB(bb).center();
            try {
                if((callingFromDraw && !rendererFor(safety).canRender()) || (!callingFromDraw && rendererFor(safety).canRender())) rendererFor(safety).draw(bb, timeStamps.get(bb), range.getValFloat(), (float) mc.player.getDistance(center.x, center.y, center.z));
            } catch(Exception ignored) {}
        }
    }

    private Map<AxisAlignedBB, Holes.Safety> getHoles(){
        Map<AxisAlignedBB, Holes.Safety> holes = new TreeMap<>(comparator);
        int lim = 0;

        for(Holes.Hole hole : Holes.getHoles(range.getValDouble())) {
            if(limit.getValInt() == 0 || lim <= limit.getValInt()) {
                Holes.Type type = hole.getType();
                Holes.Safety safety = wrapSafety(hole.getSafety(), type);

                if (ignoreOwn.getValBoolean() && hole.getHoleBlocks().contains(mc.player.getPosition())) continue;
                if (type == Holes.Type.Single && !sHoles.getValBoolean()) continue;
                if (type == Holes.Type.Double && !dHoles.getValBoolean()) continue;
                if (type == Holes.Type.Quadruple && !qHoles.getValBoolean()) continue;
                if (safety == Holes.Safety.Obsidian && !oHoles.getValBoolean()) continue;
                if (safety == Holes.Safety.Bedrock && !bHoles.getValBoolean()) continue;
                if (safety == Holes.Safety.Mix && !cHoles.getValBoolean()) continue;

                AxisAlignedBB bb = adjust(hole.getAabb(), safety);

                holes.put(bb, safety);
                lim++;
            }
        }

        return holes;
    }

    private Holes.Safety wrapSafety(Holes.Safety safety, Holes.Type type) {
        if(type == Holes.Type.UnsafeDouble || type == Holes.Type.UnsafeQuadruple) return Holes.Safety.Mix;
        if(safety != Holes.Safety.Bedrock) return Holes.Safety.Obsidian;

        return safety;
    }

    private AxisAlignedBB adjust(AxisAlignedBB bb, Holes.Safety safety){
        double maxY;

        if(safety == Holes.Safety.Obsidian) maxY = bb.minY + oHeight.getValDouble();
        else if(safety == Holes.Safety.Bedrock) maxY = bb.minY + bHeight.getValDouble();
        else maxY = bb.minY + cHeight.getValDouble();

        return new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, maxY, bb.maxZ);
    }

    private FadeRenderingRewritePattern rendererFor(Holes.Safety safety) {
        if(safety == Holes.Safety.Bedrock) return bedrockRenderer;
        if(safety == Holes.Safety.Mix) return customRenderer;
        return obbyRenderer;
    }
}