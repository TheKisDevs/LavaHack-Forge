package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.AngleUtil;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.math.MathUtil;
import com.kisman.cc.util.math.Trigonometric;
import com.kisman.cc.util.render.Rendering;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ObjectMouseOver extends Module {

    private final Setting range = register(new Setting("Range", this, 5, 1, 50, false));

    public ObjectMouseOver(){
        super("ObjectMouseOver", Category.DEBUG, true);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.player == null || mc.world == null)
            return;

        if(!isToggled())
            return;

        Vec3d eyePos = BlockUtil.getEyesPos();

        Vec3d offset = Trigonometric.position(MathUtil.absNormalize(mc.player.rotationYaw, 360) + 90, mc.player.rotationPitch + 90, range.getValDouble());
        RayTraceResult rayTraceResult = mc.world.rayTraceBlocks(eyePos, eyePos.add(offset));

        RayTraceResult whyDidIPutSoMuchEffortIntoThisWhenItCouldBetThisEasy = mc.player.rayTrace(range.getValDouble(), event.getPartialTicks());

        if(rayTraceResult == null)
            return;

        BlockPos pos = rayTraceResult.getBlockPos();

        Rendering.draw(Rendering.correct(new AxisAlignedBB(pos)), 2.0f, new Colour(255, 255, 255, 120), Rendering.DUMMY_COLOR, Rendering.Mode.BOTH);
    }
}
