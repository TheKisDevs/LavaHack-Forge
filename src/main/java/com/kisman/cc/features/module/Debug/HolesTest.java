package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.Rendering;
import com.kisman.cc.util.world.Holes;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class HolesTest extends Module {

    private final Setting range = register(new Setting("Range", this, 1, 15, 30, false));

    public HolesTest(){
        super("HoleTest", Category.DEBUG, true);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event){
        if(mc.player == null || mc.world == null)
            return;
        List<Holes.Hole> holes = Holes.getHoles(range.getValDouble());
        for(Holes.Hole hole : holes){
            Rendering.draw(Rendering.correct(hole.getAabb()), 2f, new Colour(255, 255, 255, 120), new Colour(255, 255, 255, 120), Rendering.Mode.BOX_OUTLINE);
        }
    }
}
