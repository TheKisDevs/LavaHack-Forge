package com.kisman.cc.module.misc;

import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.utils.TimerUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.Arrays;

public class GlitchBlockFix extends Module {
    private final Setting rangeXZ = new Setting("Range XZ", this, 20, 0, 50, true);
    private final Setting rangeY = new Setting("Range Y", this, 10, 0, 50, true);
    private final Setting delay = new Setting("Delay", this, 500, 0, 2000, Slider.NumberType.TIME);
    private final Setting hand = new Setting("Hand", this, "MainHand", Arrays.asList("MainHand", "OffHand"));
    private final Setting antiPlace = new Setting("Anti Place", this, false);

    private final TimerUtils timer = new TimerUtils();

    public GlitchBlockFix() {
        super("GlitchBlockFix", Category.MISC);

        setmgr.rSetting(rangeXZ);
        setmgr.rSetting(rangeY);
        setmgr.rSetting(delay);
        setmgr.rSetting(hand);
        setmgr.rSetting(antiPlace);
    }

    public void onEnable() {
        timer.reset();
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        if(timer.passedMillis(delay.getValLong())) {
            timer.reset();
            for(int x = -rangeXZ.getValInt(); x <= rangeXZ.getValInt(); x++) {
                for(int z = -rangeXZ.getValInt(); z <= rangeXZ.getValInt(); z++) {
                    for(int y = -rangeY.getValInt(); y <= rangeY.getValInt(); y++) {
                        BlockPos pos = mc.player.getPosition().add(new Vec3i(x, y, z));
                        if(mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                            if((mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock || mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) && antiPlace.getValBoolean()) return;
                            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, EnumFacing.DOWN, new Vec3d(0.5, 0.5, 0.5), hand.checkValString("MainHand") ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                        }
                    }
                }
            }
        }
    }
}
