package com.kisman.cc.module.combat.autocrystal;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.CrystalUtils;
import com.kisman.cc.util.EntityUtil;
import com.kisman.cc.util.MathUtil;
import com.kisman.cc.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.TreeMap;

import static com.kisman.cc.module.combat.autocrystal.AI.bestCrystalPos;
import static com.kisman.cc.module.combat.autocrystal.AI.placeCalculateAI;

/**
 * @author Halq
 * @since 28/02/22 20:32PM
 */

public class AutoCrystal extends Module {

    public static AutoCrystal instance;

    public final Setting placeRange = new Setting("PlaceRange", this, 4, 1, 6, true);
    public final Setting packetPlace = new Setting("PacketPlace", this, true);
    public final Setting minDMG = new Setting("MinDmg", this, 6, 0, 37, true);
    public final Setting maxSelfDMG = new Setting("MaxSelfDMG", this, 18, 0, 80, true);
    public final Setting placeDelay = new Setting("PlaceDelay", this, 4, 1, 80, true);


    public AutoCrystal() {
        super("AutoCrystal", Category.COMBAT);

        setmgr.rSetting(placeRange);
        setmgr.rSetting(packetPlace);
        setmgr.rSetting(minDMG);
        setmgr.rSetting(maxSelfDMG);
        setmgr.rSetting(placeDelay);

    }

    Timer placeTimer = new Timer();
    EntityPlayer targetPlayer;

    public void update() {
        doPlace();
    }

    public void doPlace() {

        bestCrystalPos = placeCalculateAI();

            if (bestCrystalPos == null) {

                if (placeTimer.passedDms(placeDelay.getValDouble())) {
                    if (packetPlace.getValBoolean()) {
                        mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(placeCalculateAI().getBlockPos(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                    } else {
                        mc.playerController.processRightClickBlock(mc.player, mc.world, placeCalculateAI().getBlockPos(), EnumFacing.UP, new Vec3d(0, 0, 0), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                    }
                    //place end
                }
                placeTimer.reset();
            }
            placeTimer.reset();
        }
}
