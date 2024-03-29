package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.render.Rendering;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class BlockOverlay extends Module {
//    private final ChromaRenderingPattern rendering = new ChromaRenderingPattern(this).init();
//    private final RenderingRewritePattern renderer = new RenderingRewritePattern(this, true).init();

    /*
    private final Setting color1 = register(new Setting("Color1", this, "Color1", new Colour(255, 255, 255, 120)));
    private final Setting color2 = register(new Setting("Color2", this, "Color2", new Colour(255, 255, 255, 120)));
    private final Setting color3 = register(new Setting("Color3", this, "Color3", new Colour(255, 255, 255, 120)));
     */

    private final Setting thing = register(new Setting("Thing", this, false));
    private final Setting thing2 = register(new Setting("Thing2", this, false));
    private final Setting thing3 = register(new Setting("Thing3", this, false));
    private final Setting depth = register(new Setting("Depth", this, false));
    private final Setting mode = register(new Setting("Mode", this, Mode.Curve));
    private final Setting speed = register(new Setting("Speed", this, 20, 1, 100, true));

    private volatile boolean canDraw = false;

    private volatile long start;

    private volatile BlockPos pos;

    public BlockOverlay(){
        super("BlockOverlay", Category.DEBUG);
    }

    //private final RenderPattern render = new ModuleRenderPattern(this).init();

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event){
        if(mc.player == null || mc.world == null)
            return;

        if(!isToggled())
            return;

        pos = event.getPos();
        start = System.currentTimeMillis();
        canDraw = true;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.player == null || mc.world == null || mc.objectMouseOver == null)
            return;

        BlockPos pos = mc.objectMouseOver.getBlockPos();

        if(pos == null)
            return;

        /*if(thing.getValBoolean()) {
            renderer.draw(Rendering.correct(new AxisAlignedBB(pos)), DirectionVertexes.Xp, DirectionVertexes.Yp, DirectionVertexes.Zp);

            return;
        }*/

//        AxisAlignedBB aabb = Rendering.correct(new AxisAlignedBB(pos));
        if(thing.getValBoolean()) {
            EnumFacing facing = mc.objectMouseOver.sideHit;

//            rendering.drawBlockSide(pos, facing);
        } else if(!thing3.getValBoolean()) {
            Rendering.Mode.CHROMABOX.draw(Rendering.correct(mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos)), new Color(255, 0, 0, 255), new Color(0, 255, 0, 255), new Color(0, 0, 255, 255), new Color(255, 255, 255, 255), thing2.getValBoolean() ? null : new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), depth.getValBoolean(), false, null);
        } else {
            Rendering.Mode.CHROMAOUTLINE.draw(Rendering.correct(mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos)), new Color(255, 0, 0, 255), new Color(0, 255, 0, 255), new Color(0, 0, 255, 255), new Color(255, 255, 255, 255), thing2.getValBoolean() ? null : new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), new Color(-1), depth.getValBoolean(), false, null, 1f);
        }

        /*if(!canDraw)
            return;

        double cur = System.currentTimeMillis() - start;
        double progress = cur / (speed.getValInt() * 50.0);

        if(progress > 1.0){
            canDraw = false;
            return;
        }

        AxisAlignedBB aabb = Rendering.correct(Rendering.scale(this.pos, mutateProgress(progress)));
        Rendering.draw(aabb, 3.0f, new Colour(255, 255, 255, 120), Rendering.DUMMY_COLOR, Rendering.Mode.BOX_OUTLINE);*/

        /*long millis = System.currentTimeMillis();

        Color c1 = RainbowUtil.rainbow3(millis, 0, 100, 50, 255, 2.0).getColor();
        Color c2 = RainbowUtil.rainbow3(millis, 90, 100, 50, 255, 2.0).getColor();
        Color c3 = RainbowUtil.rainbow3(millis, 180, 100, 50, 255, 2.0).getColor();
        Color c4 = RainbowUtil.rainbow3(millis, 270, 100, 50, 255, 2.0).getColor();

        Rendering.drawChrome(aabb, facing, c1, c2, c3, c4);*/

        //Rendering.draw(Rendering.correct(new AxisAlignedBB(pos)), 2f, RainbowUtil.rainbow2(0, 100, 50, 120, 1.0), RainbowUtil.rainbow2(50, 100, 50, 120, 1.0), Rendering.Mode.BOTH_GRADIENT);
        //render.getRenderBuilder().pos(pos).render();
    }

    /*private double mutateProgress(double progress){
        switch ((Mode) mode.getValEnum()){
            case Curve:
                return MathUtil.curve(progress);
            case Curve2:
                return MathUtil.curve2(progress);
            case Sin:
                return Math.sin(progress * (Math.PI / 2.0));
            case FullCurve:
                return MathUtil.curve(progress * 2.0);
            case FullSin:
                return Math.sin(2.0 * progress * (Math.PI / 2.0));
            case ReverseCurve:
                return MathUtil.curve(progress + 1.0);
        }
        return progress;
    }*/

    enum Mode {
        Curve,
        Curve2,
        Sin,
        FullCurve,
        FullSin,
        ReverseCurve,
    }
}
