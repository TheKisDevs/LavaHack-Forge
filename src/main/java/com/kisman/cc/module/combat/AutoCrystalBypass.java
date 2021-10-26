package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.kisman.cc.util.pyro.CrystalUtils2;
import com.kisman.cc.util.pyro.Rotation;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class AutoCrystalBypass extends Module {
    private Setting ticks = new Setting("Ticks", this, 2, 0, 20, true);

    private Setting rangeLine = new Setting("RangeLine", this, "Range");

    private Setting placeRange = new Setting("PlaceRange", this, 4.2f, 0, 6, false);
    private Setting breakRange = new Setting("BreakRange", this, 4.2f, 0, 6, false);
    private Setting targetRange = new Setting("TargetRange", this, Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(1.5514623f) ^ 0x7EB69651)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(2.1071864E38f) ^ 0x7F1E86EF)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(0.59863883f) ^ 0x7EE94065)), false);


    private Setting damageLine = new Setting("DanageLine", this, "Damage");

    private Setting minDMG = new Setting("MinDMG", this, 4, 0, 20, true);
    private Setting maxSelfDMG = new Setting("MaxSelfDMG", this, 4, 0, 20, true);


    private Setting placeLine = new Setting("PlaceLine", this, "Place");

    private Setting place = new Setting("Place", this, true);
    private Setting placeDelay = new Setting("PlaceDelay", this, 1, 0, 20, true);
    private Setting placeUnderBlock = new Setting("PlaceUnderBlock", this, false);
    private Setting multiPlace = new Setting("MultiPlace", this, MultiPlaceModes.None);
    private Setting holePlace = new Setting("HolePlace", this, true);
    private Setting 


    private Setting breakLine = new Setting("BreakLine", this, "Break");

    private Setting _break = new Setting("Break", this, true);
    private Setting breakDelay = new Setting("BreakDelay", this, 1, 0, 20, true);


    private Setting pauseLine = new Setting("PauseLine", this, "Pause");

    private Setting pauseWhileEating = new Setting("PauseWhileEating", this, false);
    private Setting pauseIfHittingBlock = new Setting("PauseIfHittingBlock", this, false);


    private int placeTicks;
    private int breakTicks;

    private double maxPositionDamage;
    private float damageNumber = Float.intBitsToFloat(Float.floatToIntBits(5.4387783E37f) ^ 0x7E23AAD3);

    public EntityEnderCrystal targetCrystal = null;
    public EntityPlayer target = null;
    public BlockPos targetPos = null;

    private NonNullList<BlockPos> positions = NonNullList.create();

    public AutoCrystalBypass() {
        super("AutoCrystalBypass", "AutoCrystalBypass", Category.COMBAT);
        
        setmgr.rSetting(ticks);

        setmgr.rSetting(rangeLine);
        setmgr.rSetting(placeRange);
        setmgr.rSetting(breakRange);
        setmgr.rSetting(targetRange);

        setmgr.rSetting(damageLine);
        setmgr.rSetting(minDMG);
        setmgr.rSetting(maxSelfDMG);

        setmgr.rSetting(placeLine);
        setmgr.rSetting(place);
        setmgr.rSetting(placeDelay);
        setmgr.rSetting(placeUnderBlock);
        setmgr.rSetting(multiPlace);
        setmgr.rSetting(holePlace);

        setmgr.rSetting(breakLine);
        setmgr.rSetting(_break);
        setmgr.rSetting(breakDelay);

        setmgr.rSetting(pauseLine);
        setmgr.rSetting(pauseWhileEating);
        setmgr.rSetting(pauseIfHittingBlock);
    }

    public void onEnable() {
        targetCrystal = null;
        target = null;
        targetPos = null;

        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);

        targetCrystal = null;
        target = null;
        targetPos = null;
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {
        doAutoCrystal();
    });

    private void doAutoCrystal() {
        if(mc.player == null && mc.world == null) return;

        double maxCrystalDamage = Double.longBitsToDouble(Double.doubleToLongBits(1.4087661019685725E308) ^ 9216919749093210788L);
        maxPositionDamage = Double.longBitsToDouble(Double.doubleToLongBits(2.5986735741899057E307) ^ 9206062218073306167L);


    }

    private void placeCrystal() {
        if(placeTicks++ <= placeDelay.getValInt()) return;

        placeTicks = 0;

        for(BlockPos pos : BlockInteractionHelper.getSphere(PlayerUtil.GetLocalPlayerPosFloored(), (float) placeRange.getValDouble(), placeRange.getValInt(), false, true, 0)) {
            if(
                    mc.world.getBlockState(pos).getBlock() == Blocks.AIR ||
                            !CrystalUtils.canPlaceCrystal(pos, placeUnderBlock.getValBoolean(), multiPlace.getValEnum().equals((Object)MultiPlaceModes.Static) != false ||
                                    multiPlace.getValEnum().equals((Object)MultiPlaceModes.Dynamic) != false &&
                                            CrystalUtils.isEntityMoving(mc.player) != false &&
                                            CrystalUtils.isEntityMoving((EntityLivingBase)this.target) != false,
                                    holePlace.getValBoolean()
                                    )
            ) {
                continue;
            }

            positions.add(pos);
        }

        for(EntityPlayer player : mc.world.playerEntities) {
            if(mc.player.getDistanceSq(player) > MathUtil.square(targetRange.getValDouble()) || player == mc.player || player.isDead || player.getHealth() <= Float.intBitsToFloat(Float.floatToIntBits(2.0385629E38f) ^ 2132368715)) {
                continue;
            }

            Iterator<BlockPos> iterator = positions.iterator();

            while(iterator.hasNext()) {
                BlockPos pos = iterator.next();

                float targetDamage = filterPosition(pos, player);

                if(targetDamage == Float.intBitsToFloat(Float.floatToIntBits(-4.9015f) ^ 2132597015) || !((double)targetDamage > maxPositionDamage)) {
                    continue;
                }

                maxPositionDamage = targetDamage;
                targetPos = pos;
                damageNumber = targetDamage;
                target = player;
            }
        }

        if(targetPos == null) {
            return;
        }

//        slot = InventoryUtil.findAllBlockSlots()
    }

    public float filterCrystal(EntityEnderCrystal crystal, EntityPlayer target) {
        if (mc.player.canEntityBeSeen((Entity)crystal) ? mc.player.getDistanceSq((Entity)crystal) > (double)MathUtil.square(destroyRange.getValDouble()) : mc.player.getDistanceSq((Entity)crystal) > (double)MathUtil.square(wallsRange.getValDouble())) {
            return Float.intBitsToFloat(Float.floatToIntBits(-5.0406475f) ^ 0x7F214CFC);
        }
        if (crystal.isDead) {
            return Float.intBitsToFloat(Float.floatToIntBits(-208.54588f) ^ 0x7CD08BBF);
        }
        float targetDamage = DamageUtil.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, (EntityLivingBase)target);
        float selfDamage = DamageUtil.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, (EntityLivingBase)mc.player);
        return this.returnDamage((EntityPlayer)target, targetDamage, selfDamage);
    }

    public float filterPosition(BlockPos position, EntityPlayer target) {
        if (CrystalUtils.canSeePos((BlockPos)position) ? mc.player.getDistanceSq((BlockPos)position) > (double)MathUtil.square(placeRange.getValDouble()) : mc.player.getDistanceSq((BlockPos)position) > (double)MathUtil.square(wallsRange.getValDouble())) {
            return Float.intBitsToFloat(Float.floatToIntBits(-7.1987925f) ^ 0x7F665C82);
        }
        float targetDamage = DamageUtil.calculateDamage((double)position.getX() + Double.longBitsToDouble(Double.doubleToLongBits(2.926604140248566) ^ 0x7FE769AF6E75A574L), (double)position.getY() + Double.longBitsToDouble(Double.doubleToLongBits(10.872572781808893) ^ 0x7FD5BEC1DC127F75L), (double)position.getZ() + Double.longBitsToDouble(Double.doubleToLongBits(20.94846926721453) ^ 0x7FD4F2CEE1C3F28FL), target);
        float selfDamage = DamageUtil.calculateDamage((double)position.getX() + Double.longBitsToDouble(Double.doubleToLongBits(3.800241793394989) ^ 0x7FEE66E52B5C30E1L), (double)position.getY() + Double.longBitsToDouble(Double.doubleToLongBits(8.346756871161473) ^ 0x7FD0B18A1DDA9A87L), (double)position.getZ() + Double.longBitsToDouble(Double.doubleToLongBits(16.53449891037378) ^ 0x7FD088D4EBABCD93L), mc.player);
        return this.returnDamage(target, targetDamage, selfDamage);
    }

    public float returnDamage(EntityPlayer target, float targetDamage, float selfDamage) {
        if (targetDamage < this.getMinimumDamage((EntityLivingBase)target)) {
            if (targetDamage < target.getHealth() + target.getAbsorptionAmount()) {
                return Float.intBitsToFloat(Float.floatToIntBits(-5.012834f) ^ 0x7F206923);
            }
        }
        if (selfDamage > maxSelfDMG.getValDouble()) {
            return Float.intBitsToFloat(Float.floatToIntBits(-17.910734f) ^ 0x7E0F492F);
        }
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= selfDamage) {
            return Float.intBitsToFloat(Float.floatToIntBits(-6.6046715f) ^ 0x7F535978);
        }
        return targetDamage;
    }

    public float getMinimumDamage(EntityLivingBase entity) {
        if (facePlace.getValBoolean() && entity.getHealth() + entity.getAbsorptionAmount() < facePlaceHP.getValDouble() || armorBreaker.getValBoolean() && DamageUtil.shouldBreakArmor((EntityLivingBase)entity, armorPercent.getValInt())) {
            return Float.intBitsToFloat(Float.floatToIntBits(15.796245f) ^ 0x7EFCBD6B);
        }
        return (float) minDMG.getValDouble();
    }

    private void findNewTarget() {

    }

    public static enum Renders {
        None,
        Normal;

    }

    public static enum RenderModes {
        None,
        Normal,
        Fade,
        Size;

    }

    public static enum MultiPlaceModes {
        None,
        Dynamic,
        Static;

    }

    public static enum Hands {
        None,
        Mainhand,
        Offhand;

    }

    public static enum SwitchModes {
        None,
        Normal,
        Silent;

    }

    public static enum Timings {
        Break,
        Place;

    }
}
