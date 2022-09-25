package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.AngleUtil;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.entity.TargetFinder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.math.Vec3d;

public class BowAimBot extends Module {
    private final Setting maxDist = register(new Setting("Max Distance", this, 20, 1, 50, true));
    private final MultiThreaddableModulePattern threads = threads();
    private final TargetFinder targets = new TargetFinder(maxDist::getValDouble, threads.getDelay()::getValLong, threads.getMultiThread()::getValBoolean);

    public BowAimBot() {
        super("BowAimBot", "", Category.COMBAT);
    }

    public void onEnable() {
        targets.reset();
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
            EntityPlayer player = targets.getTarget();
            targets.update();
            if (player != null) {
                Vec3d pos = EntityUtil.getInterpolatedPos(player, mc.getRenderPartialTicks());
                float[] angels = AngleUtil.calculateAngle(EntityUtil.getInterpolatedPos(mc.player, mc.getRenderPartialTicks()), pos);
                mc.player.rotationYaw = angels[0];
                mc.player.rotationPitch = angels[1];
            }
        }
    }
}
