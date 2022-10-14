package com.kisman.cc.features.module.combat.blocker;

import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.world.RotationUtils;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CrystalPushBlocker extends BlockerModule {

    private final Setting yRange = register(new Setting("YRange", blocker, 4, 1, 6, false));
    private final Setting yDown = register(new Setting("YDown", blocker, false));
    private final Setting rotate = register(new Setting("Rotate", blocker, false));
    private final Setting packet = register(new Setting("Packet", blocker, true));
    private final Setting clientSide = register(new Setting("ClientSide", blocker, ClientSide.Off));

    public CrystalPushBlocker(SettingGroup group){
        super(group, "CrystalPushBlocker", false, false);
    }

    @Override
    public void update() {
        if(mc.player == null || mc.world == null)
            return;

        AxisAlignedBB axisAlignedBB = getAABB();

        for(EnumFacing facing : EnumFacing.HORIZONTALS){
            AxisAlignedBB aabb = axisAlignedBB.offset(new Vec3d(facing.getDirectionVec()));
            for(EntityEnderCrystal crystal : mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, aabb)){
                BlockPos pos = new BlockPos(crystal.posX, crystal.posY, crystal.posZ);
                pos.offset(facing.getOpposite());
                IBlockState blockState = mc.world.getBlockState(pos);
                if(!(blockState.getBlock() instanceof BlockPistonBase))
                    continue;
                EnumFacing enumFacing = blockState.getValue(BlockDirectional.FACING);
                ChatUtility.info().printClientModuleMessage(enumFacing.toString());
                if(enumFacing != facing.getOpposite())
                    continue;
                AxisAlignedBB bb = new AxisAlignedBB(crystal.posX + 0.5, crystal.posY, crystal.posZ + 0.5, crystal.posX - 0.5, crystal.posY + 1, crystal.posZ - 0.5);
                if(aabb.intersects(bb))
                    attack(crystal);
            }
        }
    }

    private void attack(EntityEnderCrystal crystal){
        float[] oldRots = new float[] {mc.player.rotationYaw, mc.player.rotationPitch};
        float[] rots = RotationUtils.getRotation(crystal);
        if(rotate.getValBoolean())
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], mc.player.onGround));
        if(packet.getValBoolean())
            mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        else
            mc.playerController.attackEntity(mc.player, crystal);
        switch((ClientSide) clientSide.getValEnum()){
            case Off:
                break;
            case SetDead:
                crystal.setDead();
                break;
            case RemoveEntity:
                mc.world.removeEntityFromWorld(crystal.entityId);
                break;
            case Both:
                crystal.setDead();
                mc.world.removeEntityFromWorld(crystal.entityId);
                break;
        }
        if(rotate.getValBoolean())
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(oldRots[0], oldRots[1], mc.player.onGround));
    }

    private AxisAlignedBB getAABB(){
        BlockPos pos = getPlayerPos();
        double x1 = pos.getX();
        double y1 = yDown.getValBoolean() ? pos.getY() - yRange.getValDouble() : pos.getY();
        double z1 = pos.getZ();
        double x2 = pos.getX() + 1;
        double y2 = pos.getY() + yRange.getValDouble();
        double z2 = pos.getZ() + 1;
        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    private BlockPos getPlayerPos(){
        return new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
    }

    private enum ClientSide {
        Off,
        SetDead,
        RemoveEntity,
        Both
    }
}
