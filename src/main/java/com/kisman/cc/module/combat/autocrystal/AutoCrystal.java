package com.kisman.cc.module.combat.autocrystal;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

/**
 * @author Halq
 * @since 28/02/22 20:32PM
 */

public class AutoCrystal extends Module {

    public final Setting placeRange = new Setting("PlaceRange", this, 4, 1, 6, true);
    public final Setting packetPlace = new Setting("PacketPlace", this, true);
    public final Setting minDMG = new Setting("MinDmg", this, 6, 0, 37, true);
    public final Setting maxSelfDMG = new Setting("MaxSelfDMG", this, 18, 0, 80, true);
    public final Setting placeDelay = new Setting("PlaceDelay", this, 4, 1, 80, true);

    BlockPos pos = new BlockPos(Objects.requireNonNull(AI.placeCalculate(placeRange.getValFloat(), minDMG.getValFloat(), maxSelfDMG.getValFloat())));

    public AutoCrystal() {
        super("AutoCrystal", Category.COMBAT);
    }

    Timer placeTimer = new Timer();

    public void update() {
        doPlace();
    }

    public void doBreak(){

    }

    public void doPlace() {
        if(placeTimer.passedMs(placeDelay.getValLong())  && mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal || mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal){
            if (pos == null)
                return;

            if(placeTimer.passedDms(placeDelay.getValDouble())) {
                if (packetPlace.getValBoolean()) {
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, AI.getEnumFacing(true, pos), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                } else {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, pos, AI.getEnumFacing(true, pos), new Vec3d(0, 0, 0), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                }
                //place end
            }
            placeTimer.reset();
        }
        placeTimer.reset();
    }

}
