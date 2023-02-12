package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.dynamic.SwapEnum2;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Cubic
 * @since 12.11.2022
 */
public class SelfTrapRewrite extends Module {

    private final SettingEnum<EventMode> eventMode = new SettingEnum<>("EventMode", this, EventMode.Tick).register();
    private final SettingEnum<Mode> mode = new SettingEnum<>("Mode", this, Mode.Full).register();
    private final SettingEnum<SwapEnum2.Swap> swap = new SettingEnum<>("Switch", this, SwapEnum2.Swap.Silent);
    private final Setting smart = register(new Setting("Smart", this, false));
    private final Setting surroundBlocks = register(new Setting("SurroundBlocks", this, true));
    private final SettingEnum<HelpingBlocks> helpingBlocks = new SettingEnum<>("HelpingBlocks", this, HelpingBlocks.None).register();
    private final Setting down = register(new Setting("Down", this, true));
    private final Setting center = register(new Setting("Center", this, false));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));
    private final Setting disableOnComplete = register(new Setting("DisableOnComplete", this, true));

    public SelfTrapRewrite(){
        super("SelfTrapRewrite", Category.COMBAT, true);
        super.displayName = "SelfTrap";
    }

    private EnumFacing headEnumFacing = null;

    @Override
    public void onEnable() {
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }
        super.onEnable();
        if(!center.getValBoolean())
            return;
        double x = Math.floor(mc.player.posX) + 0.5;
        double z = Math.floor(mc.player.posZ) + 0.5;
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, mc.player.posY, z, mc.player.onGround));
        mc.player.setPosition(x, mc.player.posY, z);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        headEnumFacing = null;
    }

    @Override
    public void update() {
        if(eventMode.getValEnum() != EventMode.Update)
            return;
        doSelfTrap();
    }

    @SubscribeEvent
    public void onTick(TickEvent event){
        if(eventMode.getValEnum() != EventMode.Tick)
            return;
        doSelfTrap();
    }

    private void doSelfTrap(){
        if(mc.player == null || mc.world == null)
            return;

        List<BlockPos> list = getTrapBlocks();

        list.addAll(0, getHelpingBlocks());

        if(down.getValBoolean())
            list.add(0, getPlayerPos().down());

        if(disableOnComplete.getValBoolean() && checkCompleted(list)){
            this.headEnumFacing = null;
            toggle();
            return;
        }

        placeBlocks(list);
    }

    private boolean checkCompleted(List<BlockPos> list){
        for(BlockPos pos : list)
            if(mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos))
                return false;
        return true;
    }

    private void placeBlocks(List<BlockPos> list){
        int slot = InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        if(slot == -1)
            return;
        int oldSlot = mc.player.inventory.currentItem;
        for(BlockPos pos : list){
            if(!mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos) || checkEntities(pos))
                continue;
            swap.getValEnum().getTask().doTask(slot, false);
            BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean());
            swap.getValEnum().getTask().doTask(oldSlot, true);
        }
    }

    private boolean checkEntities(BlockPos pos){
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, aabb)){
            if(entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
            return true;
        }
        return false;
    }

    private List<BlockPos> getTrapBlocks(){
        List<BlockPos> list = new ArrayList<>();
        if(headEnumFacing == null)
            headEnumFacing = mc.player.getHorizontalFacing().getOpposite();
        if(mode.getValEnum() == Mode.Full || surroundBlocks.getValBoolean())
            list.addAll(Arrays.stream(EnumFacing.HORIZONTALS).map(facing -> getPlayerPos().offset(facing)).collect(Collectors.toList()));
        else
            list.add(getPlayerPos().offset(headEnumFacing));
        list.addAll(Arrays.stream(EnumFacing.HORIZONTALS).map(facing -> getPlayerPos().up().offset(facing)).collect(Collectors.toList()));
        if(smart.getValBoolean()){
            if(mc.world.getBlockState(getPlayerPos().up(2)).getBlock().isReplaceable(mc.world, getPlayerPos().up(2))){
                list.add(getPlayerPos().up(2).offset(headEnumFacing));
                list.add(getPlayerPos().up(2));
            }
        } else {
            list.add(getPlayerPos().up(2).offset(headEnumFacing));
            list.add(getPlayerPos().up(2));
        }
        if(mode.getValEnum() == Mode.Full)
            return list;
        list.removeAll(Arrays.stream(EnumFacing.HORIZONTALS).filter(facing -> facing != headEnumFacing).map(facing -> getPlayerPos().up().offset(facing)).collect(Collectors.toList()));
        return list;
    }

    private List<BlockPos> getHelpingBlocks(){
        if(helpingBlocks.getValEnum() == HelpingBlocks.None)
            return Collections.emptyList();
        if(helpingBlocks.getValEnum() == HelpingBlocks.Full)
            return Arrays.stream(EnumFacing.HORIZONTALS).map(facing -> getPlayerPos().down().offset(facing)).collect(Collectors.toList());
        return Arrays.stream(EnumFacing.HORIZONTALS).map(facing -> getPlayerPos().down().offset(facing)).filter(pos -> BlockUtil.getPossibleSides(pos.up()).isEmpty()).collect(Collectors.toList());
    }

    private BlockPos getPlayerPos(){
        return new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
    }

    private enum EventMode {
        Tick,
        Update
    }

    private enum Mode {
        Full,
        Head
    }

    private enum HelpingBlocks {
        None,
        Full,
        Smart
    }
}
