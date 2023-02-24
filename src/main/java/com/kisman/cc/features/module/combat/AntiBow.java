package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.subsystem.subsystems.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.entity.player.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;

@Targetable
@TargetsNearest
public class AntiBow extends Module {
    private final Setting checkUse = register(new Setting("CheckUse", this, false));
    private final Setting maxUse = register(new Setting("MaxUse", this, 10, 0, 20, true));
    private final Setting bowInHandCheck = register(new Setting("BowInHandCheck", this, true));

    private boolean flag;
    private int oldSlot;

    @Target
    public EntityPlayer target = null;

    public AntiBow() {
        super("AntiBow", Category.COMBAT);
        super.setDisplayInfo(() -> "[" + (target == null ? "no target no fun" : target.getName()) + "]");
    }

    public void onEnable() {
        flag = false;
        target = null;
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        target = EnemyManagerKt.nearest();

        if(target == null) {
            if(flag) {
                mc.gameSettings.keyBindUseItem.pressed = false;

                if(oldSlot != -1) InventoryUtil.switchToSlot(oldSlot, true);

                flag = false;
            }
        } else {
            int shieldSlot = InventoryUtil.findItem(Items.SHIELD, 0, 9);

            if(shieldSlot == -1) return;

            oldSlot = mc.player.inventory.currentItem;

            if((bowInHandCheck.getValBoolean() && !target.getHeldItemMainhand().getItem().equals(Items.BOW)) || (checkUse.getValBoolean() && target.getItemInUseMaxCount() <= maxUse.getValDouble())) return;

            if(!mc.player.getHeldItemMainhand().getItem().equals(Items.SHIELD)) InventoryUtil.switchToSlot(shieldSlot, true);

            mc.gameSettings.keyBindUseItem.pressed = true;

            RotationSystem.handleRotate(target);

            flag = true;
        }
    }
}