package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.subsystem.subsystems.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;

@Targetable
@TargetsNearest
public class BowAimBot extends Module {
    @Target
    public EntityPlayer target;

    public BowAimBot() {
        super("BowAimBot", "", Category.COMBAT);
        super.setDisplayInfo(() -> "[" + (target == null ? "no target no fun" : target.getName()) + "]");
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        target = EnemyManagerKt.nearest();

        if(target != null && mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) RotationSystem.handleRotate(target);
    }
}
