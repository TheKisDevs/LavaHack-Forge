package com.kisman.cc.module.Debug;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.RainbowUtil;
import com.kisman.cc.util.Rendering;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockOverlay extends Module {

    private final Setting color1 = register(new Setting("Color1", this, "Color1", new Colour(255, 255, 255, 120)));
    private final Setting color2 = register(new Setting("Color2", this, "Color2", new Colour(255, 255, 255, 120)));
    private final Setting color3 = register(new Setting("Color3", this, "Color3", new Colour(255, 255, 255, 120)));

    public BlockOverlay(){
        super("BlockOverlay", Category.DEBUG);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.player == null || mc.world == null)
            return;

        if(!isToggled())
            return;

        BlockPos pos = mc.objectMouseOver.getBlockPos();

        if(pos == null)
            return;

        Rendering.draw(Rendering.correct(new AxisAlignedBB(pos)), 2f, RainbowUtil.rainbow2(0, 100, 50, 120, 1.0), RainbowUtil.rainbow2(50, 100, 50, 120, 1.0), Rendering.Mode.BOTH_GRADIENT);
    }
}
