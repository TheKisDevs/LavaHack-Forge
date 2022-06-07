package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.manager.RotationManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;

public class AntiBow extends Module {
    private final Setting packet = register(new Setting("Packet", this, false));
    private final Setting range = register(new Setting("Range", this, 40, 0, 40, false));
    private final Setting checkUse = register(new Setting("CheckUse", this, false));
    private final Setting maxUse = register(new Setting("MaxUse", this, 10, 0, 20, true));
    private final Setting bowInHandCheck = register(new Setting("BowInHandCheck", this, true));

    private boolean bool;
    private int oldSlot;

    public AntiBow() {
        super("AntiBow", Category.COMBAT);
    }

    public void onEnable() {
        bool = false;
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        EntityPlayer target = EntityUtil.getTarget(range.getValFloat());

        if(target == null) {
            if(bool) {
                mc.gameSettings.keyBindUseItem.pressed = false;

                if(oldSlot != -1) InventoryUtil.switchToSlot(oldSlot, true);

                bool = false;
            }
        } else {
            int shieldSlot = InventoryUtil.findItem(Items.SHIELD, 0, 9);

            if(shieldSlot == -1) {
                target = null;
                return;
            }

            oldSlot = mc.player.inventory.currentItem;

            if(bowInHandCheck.getValBoolean()) {
                if(!target.getHeldItemMainhand().getItem().equals(Items.BOW)) {
                    return;
                }
            }

            if(checkUse.getValBoolean()) {
                if(target.getItemInUseMaxCount() <= maxUse.getValDouble()) {
                    return;
                }
            }

            if(!mc.player.getHeldItemMainhand().getItem().equals(Items.SHIELD)) InventoryUtil.switchToSlot(shieldSlot, true);

            mc.gameSettings.keyBindUseItem.pressed = true;
            RotationManager.look(target, packet.getValBoolean());
            bool = true;
        }
    }
}
