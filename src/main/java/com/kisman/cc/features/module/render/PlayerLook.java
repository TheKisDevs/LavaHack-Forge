package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.features.subsystem.subsystems.EnemyManagerKt;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.RenderingRewritePattern;
import com.kisman.cc.util.math.Trigonometric;
import com.kisman.cc.util.render.Rendering;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(
        name = "PlayerLook",
        category = Category.EXPLOIT,
        wip = true
)
public class PlayerLook extends Module {
    private final Setting single = register(new Setting("Single", this, true));
    private final Setting self = register(new Setting("Self", this, true));
    private final Setting radius = register(new Setting("Radius", this, 5.5, 1.0, 20.0, false));
    private final Setting range = register(new Setting("Range", this, 8.0, 1.0, 15.0, false));
    private final Setting raytrace = register(new Setting("Raytrace", this, true));
    private final Setting displayName = register(new Setting("Display Name", this, false));
    private final RenderingRewritePattern pattern = new RenderingRewritePattern(this).preInit().init();

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        double range = this.range.getValDouble();

        List<Entity> entities;

        if(single.getValBoolean()){
            Entity target = EnemyManagerKt.nearest();
            if(target == null)
                return;
            entities = Collections.singletonList(target);
        } else {
            entities = mc.world.loadedEntityList.stream().filter(entity -> entity != mc.player && entity.getDistanceSq(mc.player) <= (range * range)).collect(Collectors.toList());
        }

        if(self.getValBoolean() && !single.getValBoolean()) entities.add(mc.player);

        if(entities.isEmpty())
            return;

        HashMap<BlockPos, ArrayList<Entity>> posses = new HashMap<>();

        for(Entity entity : entities) {
            if(entity instanceof EntityItem) continue;

            BlockPos pos = Trigonometric.entityObjectMouseOver(entity, radius.getValDouble(), raytrace.getValBoolean());

            if(pos == null) continue;

            if(posses.containsKey(pos)) {
                posses.get(pos).add(entity);
            } else {
                posses.put(
                        pos,
                        new ArrayList<>(Collections.singletonList(entity))
                );
            }
        }

        for(BlockPos pos : posses.keySet()) {
            pattern.draw(mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos));

            if(displayName.getValBoolean()) {
                StringBuilder text = new StringBuilder();

                for(
                        int i = 0;
                        i < posses.get(pos).size();
                        i++
                ) {
                    text.append(posses.get(pos).get(i).getName());

                    if(i != posses.get(pos).size() - 1) text.append("\n");
                }

                Rendering.TextRendering.drawText(
                        pos,
                        text.toString(),
                        Color.WHITE.getRGB()
                );
            }
        }
    }
}
