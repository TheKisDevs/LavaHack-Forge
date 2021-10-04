package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.RandomUtils;

public class NoSlow extends Module {
    //TODO: update noslow

    public NoSlow() {
        super("NoSlow", "NoSlow", Category.MOVEMENT);

        Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));
    }

    public void update() {

        if (this.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {

            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(Math.random() * 100, Math.random() * 100, Math.random() * 100), EnumFacing.DOWN));

            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock((new BlockPos(-1, -1, -1)), EnumFacing.EAST, EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));

        }
    }
}
