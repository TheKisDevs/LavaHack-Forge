package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.util.ChromaRenderingPattern;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockOverlay extends Module {
    private final ChromaRenderingPattern rendering = new ChromaRenderingPattern(this).init();

    /*
    private final Setting color1 = register(new Setting("Color1", this, "Color1", new Colour(255, 255, 255, 120)));
    private final Setting color2 = register(new Setting("Color2", this, "Color2", new Colour(255, 255, 255, 120)));
    private final Setting color3 = register(new Setting("Color3", this, "Color3", new Colour(255, 255, 255, 120)));
     */

    public BlockOverlay(){
        super("BlockOverlay", Category.DEBUG);
    }

    //private final RenderPattern render = new ModuleRenderPattern(this).init();

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.player == null || mc.world == null)
            return;

        if(!isToggled())
            return;

        BlockPos pos = mc.objectMouseOver.getBlockPos();

        if(pos == null)
            return;

//        AxisAlignedBB aabb = Rendering.correct(new AxisAlignedBB(pos));
        EnumFacing facing = mc.objectMouseOver.sideHit;

        rendering.drawBlockSide(pos, facing);

        /*long millis = System.currentTimeMillis();

        Color c1 = RainbowUtil.rainbow3(millis, 0, 100, 50, 255, 2.0).getColor();
        Color c2 = RainbowUtil.rainbow3(millis, 90, 100, 50, 255, 2.0).getColor();
        Color c3 = RainbowUtil.rainbow3(millis, 180, 100, 50, 255, 2.0).getColor();
        Color c4 = RainbowUtil.rainbow3(millis, 270, 100, 50, 255, 2.0).getColor();

        Rendering.drawChrome(aabb, facing, c1, c2, c3, c4);*/

        //Rendering.draw(Rendering.correct(new AxisAlignedBB(pos)), 2f, RainbowUtil.rainbow2(0, 100, 50, 120, 1.0), RainbowUtil.rainbow2(50, 100, 50, 120, 1.0), Rendering.Mode.BOTH_GRADIENT);
        //render.getRenderBuilder().pos(pos).render();
    }
}
