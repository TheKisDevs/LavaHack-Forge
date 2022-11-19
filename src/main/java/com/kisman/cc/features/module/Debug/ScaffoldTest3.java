package com.kisman.cc.features.module.Debug;

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
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScaffoldTest3 extends Module {

    private final SettingEnum<SwapEnum2.Swap> swap = new SettingEnum<>("Switch", this, SwapEnum2.Swap.Silent).register();
    private final Setting tower = register(new Setting("Tower", this, false));
    private final SettingEnum<TowerMode> towerMode = new SettingEnum<>("TowerMode", this, TowerMode.Vanilla).setVisible(tower::getValBoolean).register();
    private final Setting towerTicks = register(new Setting("TowerTicks", this, 1, 1, 20, true).setVisible(tower::getValBoolean));
    private final Setting towerStrict = register(new Setting("TowerStrict", this, false).setVisible(() -> tower.getValBoolean() && towerMode.getValEnum() == TowerMode.Vanilla));
    private final Setting towerSetBack = register(new Setting("TowerSetBack", this, false).setVisible(() -> tower.getValBoolean() && towerMode.getValEnum() == TowerMode.Vanilla));
    private final Setting towerUpSpoof = register(new Setting("TowerUpSpoof", this, false).setVisible(() -> tower.getValBoolean() && towerMode.getValEnum() == TowerMode.Vanilla));
    private final Setting towerUpSpoofBalance = register(new Setting("TowerUpSpoofBalance", this, 0, 0, 10, true).setVisible(() -> tower.getValBoolean() && towerMode.getValEnum() == TowerMode.Vanilla));
    private final Setting towerUpSpoofStrict = register(new Setting("TowerUpSpoofStrict", this, false).setVisible(() -> tower.getValBoolean() && towerMode.getValEnum() == TowerMode.Vanilla));
    private final Setting towerUpSpoofBack = register(new Setting("TowerUpSpoofBack", this, true).setVisible(() -> tower.getValBoolean() && towerMode.getValEnum() == TowerMode.Vanilla));
    private final Setting towerMotion = register(new Setting("TowerMotion", this,0.42, 0, 1, false).setVisible(() -> tower.getValBoolean() && towerMode.getValEnum() == TowerMode.Motion));
    private final Setting towerGroundSpoof = register(new Setting("TowerGroundSpoof", this, false).setVisible(() -> tower.getValBoolean() && towerMode.getValEnum() == TowerMode.Motion));
    private final Setting restrict = register(new Setting("Restrict", this, true).setVisible(tower::getValBoolean));
    private final Setting settingRestrictTicks = register(new Setting("RestrictTicks", this, 15, 1, 40, true).setVisible(tower::getValBoolean));
    private final Setting towerBind = register(new Setting("TowerBind", this, Keyboard.KEY_SPACE).setTitle("Bind").setVisible(tower::getValBoolean));
    private final Setting downBind = register(new Setting("Down", this, Keyboard.KEY_NONE));
    private final Setting keepY = register(new Setting("KeeY", this, false));
    private final Setting resetY = register(new Setting("ResetY", this, Keyboard.KEY_NONE).setVisible(keepY::getValBoolean));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));

    public ScaffoldTest3(){
        super("ScaffoldTest3", Category.DEBUG);
    }

    private int playerY;

    private int restrictTicks = 0;

    private BlockPos last = null;

    @Override
    public void onEnable() {
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }
        playerY = (int) Math.floor(mc.player.posY);
        super.onEnable();
        MinecraftForge.EVENT_BUS.register(this);
        /*
        int slot = InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        if(slot == -1)
            return;
        BlockPos pos = new BlockPos(mc.player.posX, playerY, mc.player.posZ).down();
        BlockPos connector = getConnector(pos);
        if(clutch.getValInt() > 0 && BlockUtil.getPossibleSides(pos).isEmpty() && connector == null)
            for(BlockPos blockPos : clutch(pos, clutch.getValInt()))
                placeBlock(blockPos, slot);
         */
    }

    @Override
    public void onDisable() {
        super.onDisable();
        MinecraftForge.EVENT_BUS.unregister(this);
        playerY = 0;
        restrictTicks = 0;
        last = null;
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null)
            return;

        int slot = InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        if(slot == -1)
            return;

        BlockPos pos = new BlockPos(mc.player.posX, playerY, mc.player.posZ).down();
        int newY = (int) Math.abs(mc.player.posY);

        boolean flag = last != null && (last.getX() != pos.getX() || last.getZ() != pos.getZ());

        if(placeScaffoldBlocks(pos, newY, slot, Keyboard.isKeyDown(downBind.getKey()) && flag))
            return;

        tower(newY);

        if(Keyboard.isKeyDown(downBind.getKey()) && flag)
            playerY = newY - 1;
        else
            playerY = newY;

        if(flag)
            restrictTicks = 0;
        else
            restrictTicks++;
    }

    private boolean placeScaffoldBlocks(BlockPos pos, int newY, int slot, boolean down){
        if(down)
            placeBlock(last.down(), slot);
        BlockPos connector = getConnector(pos);
        if(BlockUtil.getPossibleSides(down ? pos.down() : pos).isEmpty() && connector != null)
            placeBlock(down ? connector.down() : connector, slot);
        placeBlock(down ? pos.down() : pos, slot);
        if(!keepY.getValBoolean() && !down && newY > playerY)
            placeBlock(pos.up(), slot);
        if(keepY.getValBoolean()){
            if(Keyboard.isKeyDown(resetY.getKey()))
                playerY = newY;
            return true;
        }
        return false;
    }

    private void tower(int newY){
        if(
                tower.getValBoolean()
                        && Keyboard.isKeyDown(towerBind.getKey())
                        && mc.player.ticksExisted % towerTicks.getValInt() == 0
                        && (!restrict.getValBoolean() || restrictTicks >= settingRestrictTicks.getValInt())
        ) {
            if(towerMode.getValEnum() == TowerMode.Motion){
                if(towerGroundSpoof.getValBoolean())
                    mc.player.connection.sendPacket(new CPacketPlayer(true));
                else
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                mc.player.motionY = towerMotion.getValDouble();
                mc.player.fallDistance = 0f;
            } else if(mc.player.posY + (mc.player.motionY - ((mc.player.motionY / 10) * towerUpSpoofBalance.getValDouble())) > Math.ceil(mc.player.posY) && towerUpSpoof.getValBoolean()) {
                if(towerUpSpoofStrict.getValBoolean()){
                    double y = mc.player.posY + (mc.player.motionY - ((mc.player.motionY / 10) * towerUpSpoofBalance.getValDouble()));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, y, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, Math.floor(y), mc.player.posZ, true));
                    mc.player.setPosition(mc.player.posX, Math.floor(y), mc.player.posZ);
                    mc.player.jump();
                    if(towerUpSpoofBack.getValBoolean())
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, y, mc.player.posZ, false));
                } else {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, Math.ceil(mc.player.posY), mc.player.posZ, true));
                    mc.player.setPosition(mc.player.posX, Math.ceil(mc.player.posY), mc.player.posZ);
                    mc.player.jump();
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, Math.ceil(mc.player.posY) + 0.001, mc.player.posZ, false));
                }
            } else if(newY > playerY) {
                final double x = mc.player.posX;
                final double y = mc.player.posY;
                final double z = mc.player.posZ;
                final boolean onGround = mc.player.onGround;
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, newY, mc.player.posZ, true));
                mc.player.setPosition(mc.player.posX, newY, mc.player.posZ);
                mc.player.jump();
                if(!towerStrict.getValBoolean())
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, onGround));
                if(towerSetBack.getValBoolean())
                    mc.player.setPosition(x, y, z);
            }
        }
    }

    /*
    private List<BlockPos> clutch(BlockPos pos, int n){
        List<BlockPos> list = new ArrayList<>();
        clutch0(pos, 0, n, list);
        return list;
    }

    private void clutch0(BlockPos pos, int cur, int n, List<BlockPos> list){
        list.add(pos);
        if(!BlockUtil.getPossibleSides(pos).isEmpty())
            return;
        if(cur >= n){
            list.clear();
            return;
        }
        List<BlockPos> possiblePositions = Stream.of(EnumFacing.HORIZONTALS)
                .map(pos::offset)
                .filter(blockPos -> !BlockUtil.getPossibleSides(blockPos).isEmpty())
                .filter(blockPos -> !checkEntities(blockPos))
                .collect(Collectors.toList());
        for(BlockPos blockPos : possiblePositions)
            clutch0(blockPos, cur + 1, n, new ArrayList<>(list));
    }

     */

    private void placeBlock(BlockPos pos, int slot){
        if(!mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos) || checkEntities(pos))
            return;
        int oldSlot = mc.player.inventory.currentItem;
        swap.getValEnum().getTask().doTask(slot, false);
        BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean());
        swap.getValEnum().getTask().doTask(oldSlot, true);
    }

    private boolean checkEntities(BlockPos pos){
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        for(Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, aabb)){
            if(entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
            return true;
        }
        return false;
    }

    private BlockPos getConnector(BlockPos pos){
        return Stream.of(EnumFacing.HORIZONTALS)
                .map(pos::offset)
                .filter(blockPos -> !BlockUtil.getPossibleSides(blockPos).isEmpty())
                .filter(blockPos -> !checkEntities(blockPos))
                .min(Comparator.comparingDouble(blockPos -> mc.player.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5)))
                .orElse(null);
    }

    private enum TowerMode {
        Vanilla,
        Motion
    }
}
