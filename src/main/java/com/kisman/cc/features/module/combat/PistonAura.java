package com.kisman.cc.features.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.subsystem.subsystems.Target;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.dynamic.SwapEnum2;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.render.Rendering;
import com.kisman.cc.util.thread.ThreadUtils;
import com.kisman.cc.util.world.BlockUtil2;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

// don't make this targetable because this will use another custom system
// yes i am talking to you kisman
/**
 * @author Cubic
 * @since 30.03.2023
 */
public class PistonAura extends Module {

    private final Setting enemyRange = register(new Setting("Enemy Range", this, 8, 1, 16, false));
    private final SettingEnum<SwapEnum2.Swap> swap = register(new SettingEnum<>("Swap", this, SwapEnum2.Swap.Silent));
    private final Setting cycleDelay = register(new Setting("Cycle Delay", this, 1, 0, 10, true));

    private final SettingGroup placeGroup = register(new SettingGroup(new Setting("PlaceGroup", this).setTitle("Place")));
    private final SettingEnum<PlaceMode> place = placeGroup.add(new SettingEnum<>("Place", this, PlaceMode.Flexible));
    //TODO: For now only blocks are supported
    //private final SettingEnum<RedstoneMode> redstone = placeGroup.add(new SettingEnum<>("Redstone", this, RedstoneMode.Both));
    private final Setting placeRange = placeGroup.add(new Setting("Place Range", this, 5, 1, 6, false));
    private final Setting placeDelay = placeGroup.add(new Setting("Place Delay", this, 0, 0, 10, true));
    private final Setting placeRotate = placeGroup.add(new Setting("PlaceRotate", this, false).setTitle("Rotate"));
    private final Setting placeRaytrace = placeGroup.add(new Setting("PlaceRaytrace", this, false).setTitle("Raytrace"));

    private final SettingGroup breakGroup = register(new SettingGroup(new Setting("BreakGroup", this).setTitle("Break")));
    private final Setting breakDelay = breakGroup.add(new Setting("Break Delay", this, 4, 0, 20, true));
    private final Setting breakRotate = breakGroup.add(new Setting("BreakRotate", this, false).setTitle("Rotate"));
    private final Setting breakRaytrace = breakGroup.add(new Setting("BreakRaytrace", this, false).setTitle("Raytrace"));

    public PistonAura(){
        super("PistonAura", Category.COMBAT, true);
    }

    private enum PlaceMode {
        Flexible,
        Strict
    }

    //TODO:
    //private enum RedstoneMode {
    //    Block,
    //    Torch,
    //    Both
    //}

    @Target
    private Entity target;

    private PlaceInfo placeInfo = null;

    private boolean canPlace = true;

    private int stage = 0;

    private boolean thread = false;

    private final Set<BlockPos> tmpSet = new HashSet<>();

    @Override
    public void onEnable() {
        placeInfo = null;
        canPlace = true;
        stage = 0;
        thread = false;
        Kisman.EVENT_BUS.subscribe(packetListener);
    }

    @Override
    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(packetListener);
    }

    private final Listener<PacketEvent.PostReceive> packetListener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketSoundEffect){
            if(placeInfo == null)
                return;
            if(!thread)
                return;
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if(packet.getCategory() != SoundCategory.BLOCKS || packet.getSound() != SoundEvents.ENTITY_GENERIC_EXPLODE)
                return;
            double pX = placeInfo.pistonPos.getX() + 0.5;
            double pY = placeInfo.pistonPos.getY() + 0.5;
            double pZ = placeInfo.pistonPos.getZ() + 0.5;
            ChatUtility.info().printClientModuleMessage("" + Math.sqrt(Math.pow(packet.getX() - pX, 2) + Math.pow(packet.getY() - pY, 2) + Math.pow(packet.getZ() - pZ, 2)));
            if(Math.sqrt(Math.pow(packet.getX() - pX, 2) + Math.pow(packet.getY() - pY, 2) + Math.pow(packet.getZ() - pZ, 2)) <= 5){
                thread = false;
                new Thread(() -> {
                    try {
                        ThreadUtils.sleep(cycleDelay.getValLong() * 50);
                    } catch (InterruptedException e) {
                        Kisman.LOGGER.error("[PistonAura] Error resetting cycle", e);
                    }
                    canPlace = true;
                    stage = 0;
                }).start();
            }
        }
    });

    public static EntityPlayer getTarget(final float range, float wallRange) {
        EntityPlayer currentTarget = null;
        for (int size = mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            final EntityPlayer player = mc.world.playerEntities.get(i);
            if (!isntValid(player, range, wallRange)) {
                if (currentTarget == null) currentTarget = player;
                else if (mc.player.getDistanceSq(player) < mc.player.getDistanceSq(currentTarget)) currentTarget = player;
            }
        }
        return currentTarget;
    }

    public static boolean isntValid(final EntityLivingBase entity, final double range, double wallRange) {
        return (mc.player.getDistance(entity) > (mc.player.canEntityBeSeen(entity) ? range : wallRange)) || entity == mc.player || entity.getHealth() <= 0.0f || entity.isDead || FriendManager.instance.isFriend(entity.getName());
    }

    /*
    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(placeInfo == null)
            return;
        if(placeInfo.pistonPos != null)
            Rendering.draw(Rendering.correct(new AxisAlignedBB(placeInfo.pistonPos)), 2.0f, new Colour(0, 0, 255, 120), new Colour(0, 0, 255, 120), Rendering.Mode.BOX_OUTLINE);
        if(placeInfo.crystalPos != null)
            Rendering.draw(Rendering.correct(new AxisAlignedBB(placeInfo.crystalPos)), 2.0f, new Colour(255, 0, 0, 120), new Colour(255, 0, 0, 120), Rendering.Mode.BOX_OUTLINE);
        if(placeInfo.redstonePos != null)
            Rendering.draw(Rendering.correct(new AxisAlignedBB(placeInfo.redstonePos)), 2.0f, new Colour(0, 255, 0, 120), new Colour(0, 255, 0, 120), Rendering.Mode.BOX_OUTLINE);
        for(BlockPos pos : tmpSet){
            Rendering.draw(Rendering.correct(new AxisAlignedBB(pos)), 2.0f, new Colour(255, 255, 255, 120), new Colour(0, 255, 0, 120), Rendering.Mode.BOX_OUTLINE);
        }
    }
     */

    @Override
    public void update() {
        if(mc.player == null || mc.world == null)
            return;

        target = getTarget(enemyRange.getValFloat(), enemyRange.getValFloat());

        if(target == null){
            return;
        }

        int pistonSlot = InventoryUtil.getBlockInHotbar(Blocks.PISTON);
        if(pistonSlot == -1)
            pistonSlot = InventoryUtil.getBlockInHotbar(Blocks.STICKY_PISTON);
        if(pistonSlot == -1){
            toggle();
            return;
        }

        int redstoneSlot = InventoryUtil.getBlockInHotbar(Blocks.REDSTONE_BLOCK);
        if(redstoneSlot == -1){
            toggle();
            return;
        }

        int crystalSlot = InventoryUtil.getHotbarItemSlot(Items.END_CRYSTAL);
        if(crystalSlot == -1 && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL){
            toggle();
            return;
        }

        if(canPlace && placeDelay.getValDouble() == 0){
            placeInfo = calculatePlace();
            if(placeInfo == null)
                return;
            placePiston(placeInfo, pistonSlot);
            placeRedstone(placeInfo, redstoneSlot);
            placeCrystal(placeInfo, crystalSlot);
            canPlace = false;
            stage = 1;
            return;
        }
        if(placeDelay.getValDouble() == 1){
            if(canPlace){
                placeInfo = calculatePlace();
                if(placeInfo == null)
                    return;
                placePiston(placeInfo, pistonSlot);
                canPlace = false;
                stage = 1;
                return;
            }
            if(stage == 1){
                placeRedstone(placeInfo, redstoneSlot);
                placeCrystal(placeInfo, crystalSlot);
                stage = 2;
                return;
            }
        }
        if(placeDelay.getValDouble() > 1){
            if(canPlace){
                placeInfo = calculatePlace();
                if(placeInfo == null)
                    return;
                placePiston(placeInfo, pistonSlot);
                canPlace = false;
                stage = 1;
                return;
            }
            if(stage == 1){
                placeCrystal(placeInfo, crystalSlot);
                stage = 2;
                return;
            }
            if(stage == 2){
                placeRedstone(placeInfo, redstoneSlot);
                stage = 3;
                return;
            }
        }

        if(
                placeDelay.getValDouble() == 0 && stage == 1
                || placeDelay.getValDouble() == 1 && stage == 2
                || placeDelay.getValDouble() > 1 && stage == 3
        ){
            stage++;
            new Thread(() -> {
                final PlaceInfo info = placeInfo;
                try {
                    ThreadUtils.sleep(breakDelay.getValLong() * 50);
                } catch (InterruptedException e) {
                    Kisman.LOGGER.error("[PistonAura] Error breaking crystal", e);
                }
                AxisAlignedBB aabb = getAABB(info.pistonPos, info.pistonPos.offset(info.facing.getOpposite(), 2)).expand(0.1, 0.1, 0.1);
                EntityEnderCrystal crystal = mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, aabb).stream().findFirst().orElse(null);
                if(crystal == null)
                    return;
                breakCrystal(crystal);
                thread = true;
            }).start();
        }
    }

    private AxisAlignedBB getAABB(BlockPos pos1, BlockPos pos2){
        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxX = Math.max(pos1.getX() + 1, pos2.getX() + 1);
        double maxY = Math.max(pos1.getY() + 1, pos2.getY() + 1);
        double maxZ = Math.max(pos1.getZ() + 1, pos2.getZ() + 1);
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private void placeCrystal(PlaceInfo placeInfo, int crystalSlot){
        if(crystalSlot == -1){
            BlockUtil2.placeBlock2(placeInfo.crystalPos, EnumHand.OFF_HAND, true, false, placeRotate.getValBoolean(), true);
            return;
        }
        int oldSlot = mc.player.inventory.currentItem;
        swap.getValEnum().getTask().doTask(crystalSlot, false);
        BlockUtil2.placeBlock2(placeInfo.crystalPos, EnumHand.MAIN_HAND, false, false, placeRotate.getValBoolean(), true);
        swap.getValEnum().getTask().doTask(oldSlot, true);
    }

    private void placeRedstone(PlaceInfo placeInfo, int redstoneSlot){
        int oldSlot = mc.player.inventory.currentItem;
        swap.getValEnum().getTask().doTask(redstoneSlot, false);
        BlockUtil2.placeBlock2(placeInfo.redstonePos, EnumHand.MAIN_HAND, false, false, placeRotate.getValBoolean(), true);
        swap.getValEnum().getTask().doTask(oldSlot, true);
    }

    private void placePiston(PlaceInfo placeInfo, int pistonSlot){
        int oldSlot = mc.player.inventory.currentItem;
        float yaw = 0;
        float pitch = 0;
        switch (placeInfo.facing.getOpposite()) {
            case SOUTH:
                yaw = 180;
                pitch = 0;
                break;
            case NORTH:
                yaw = 0;
                pitch = 0;
                break;
            case EAST:
                yaw = 90;
                pitch = 0;
                break;
            case WEST:
                yaw = -90;
                pitch = 0;
                break;
            case UP:
            case DOWN:
                pitch = 90;
                break;
        }
        float[] oldRots = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
        BlockPos pos = placeInfo.pistonPos;
        if(place.getValEnum() == PlaceMode.Strict){
            float[] rots = calculateAngle(mc.player.getPositionVector().addVector(0, mc.player.eyeHeight, 0), new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], mc.player.onGround));
        } else {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, mc.player.onGround));
        }
        swap.getValEnum().getTask().doTask(pistonSlot, false);
        BlockUtil2.placeBlock2(pos, EnumHand.MAIN_HAND, false, false, false, true);
        swap.getValEnum().getTask().doTask(oldSlot, true);
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(oldRots[0], oldRots[1], mc.player.onGround));
    }

    private void breakCrystal(EntityEnderCrystal crystal){
        float[] oldRots = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
        Vec3d vec3d = raytrace(crystal);
        if(breakRotate.getValBoolean()){
            if(breakRaytrace.getValBoolean()){
                float[] rots;
                if(vec3d == null){
                    rots = calculateAngle(mc.player.getPositionVector().addVector(0, mc.player.eyeHeight, 0), crystal.getPositionVector());
                } else {
                    rots = calculateAngle(mc.player.getPositionVector().addVector(0, mc.player.eyeHeight, 0), vec3d);
                }
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], mc.player.onGround));
            } else {
                float[] rots = calculateAngle(mc.player.getPositionVector().addVector(0, mc.player.eyeHeight, 0), crystal.getPositionVector());
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], mc.player.onGround));
            }
        }
        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        mc.player.swingArm(EnumHand.MAIN_HAND);
        if(breakRotate.getValBoolean())
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(oldRots[0], oldRots[1], mc.player.onGround));
    }

    private Vec3d raytrace(Entity entity){
        for(double x = entity.boundingBox.minX + 0.05; x < entity.boundingBox.maxX; x += 0.475){
            for(double y = entity.boundingBox.minY + 0.05; y < entity.boundingBox.maxY; y += 0.475){
                for(double z = entity.boundingBox.minZ + 0.05; z < entity.boundingBox.maxZ; z += 0.475){
                    Vec3d vec3d = new Vec3d(x, y, z);
                    RayTraceResult result = mc.world.rayTraceBlocks(mc.player.getPositionVector().addVector(0, mc.player.eyeHeight, 0), vec3d);
                    if(result == null && isInDistance(vec3d))
                        return vec3d;
                }
            }
        }
        return null;
    }

    private PlaceInfo calculatePlace(){
        BlockPos targetPos = new BlockPos(target.posX, target.posY, target.posZ);
        for(
                EnumFacing facing : EnumFacing.HORIZONTALS
        ){
            BlockPos pos = targetPos.offset(facing);
            IBlockState state = mc.world.getBlockState(pos);
            if(state.getBlock() != Blocks.OBSIDIAN && state.getBlock() != Blocks.BEDROCK)
                continue;
            if(
                    !mc.world.getBlockState(pos.up()).getBlock().isReplaceable(mc.world, pos.up())
                    || !mc.world.getBlockState(pos.up(2)).getBlock().isReplaceable(mc.world, pos.up(2))
            ){
                continue;
            }
            PlaceInfo redstonePos = getPlacePos(pos.offset(facing), facing);
            if(redstonePos == null)
                continue;
            if(!isInDistance(redstonePos.pistonPos) || !isInDistance(redstonePos.redstonePos))
                continue;
            return new PlaceInfo(redstonePos.pistonPos, redstonePos.redstonePos, targetPos.offset(facing).up(), redstonePos.facing);
        }
        return null;
    }

    private boolean isInDistance(BlockPos pos){
        return mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= placeRange.getValDouble();
    }

    private boolean isInDistance(Vec3d vec3d){
        return mc.player.getDistance(vec3d.x, vec3d.y, vec3d.z) <= placeRange.getValDouble();
    }

    private PlaceInfo getPlacePos(BlockPos pos, EnumFacing facing){
        List<PlaceInfo> placeInfos = new ArrayList<>();
        placeInfos.add(checkPlace(pos.offset(facing.rotateYCCW()), facing));
        placeInfos.add(checkPlace(pos.offset(facing.rotateYCCW().getOpposite()), facing));
        placeInfos.add(checkPlace(pos.up(), facing));
        placeInfos.add(checkPlace(pos.up().offset(facing.rotateYCCW()), facing));
        placeInfos.add(checkPlace(pos.up().offset(facing.rotateYCCW().getOpposite()), facing));
        PlaceInfo placeInfo = placeInfos.stream()
                .filter(Objects::nonNull)
                .min(Comparator.comparingDouble(info -> mc.player.getDistance(info.pistonPos.getX() + 0.5, info.pistonPos.getY() + 0.5, info.pistonPos.getZ() + 0.5)))
                .orElse(null);
        if(placeInfo != null)
            return placeInfo;
        return checkPlace(pos, facing);
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        final ArrayList<EnumFacing> facings = new ArrayList<>();
        if (mc.world == null || pos == null) return facings;
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (blockState != null && blockState.getBlock().canCollideCheck(blockState, false)) if (!blockState.getMaterial().isReplaceable()) facings.add(side);
        }
        return facings;
    }

    private PlaceInfo checkPlace(BlockPos pos, EnumFacing facing){
        tmpSet.add(pos);
        if(
                !mc.world.getBlockState(pos.up()).getBlock().isReplaceable(mc.world, pos.up())
                || !mc.world.getBlockState(pos.up().offset(facing.getOpposite())).getBlock().isReplaceable(mc.world, pos.up().offset(facing.getOpposite()))
                || getPossibleSides(pos.up()).isEmpty()
        ){
            return null;
        }
        Vec3d vec3d = raytrace(pos);
        if(raytrace(pos) == null){
            if(place.getValBoolean())
                return null;
            vec3d = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }
        float[] rots = calculateAngle(mc.player.getPositionVector().addVector(0, mc.player.eyeHeight, 0), vec3d);
        EnumFacing horizontalFacing = EnumFacing.getHorizontal(MathHelper.floor((double)(rots[0] * 4.0F / 360.0F) + 0.5D) & 3);
        if(place.getValEnum() == PlaceMode.Strict && facing != horizontalFacing)
            return null;
        BlockPos redstonePos = getRedstonePos(pos.up(), facing);
        if(redstonePos == null)
            return null;
        return new PlaceInfo(pos.up(), redstonePos, null, facing);
    }

    private BlockPos getRedstonePos(BlockPos pos, EnumFacing facing){
        if(placeDelay.getValDouble() > 1){
            if(!mc.world.getBlockState(pos.offset(facing)).getBlock().isReplaceable(mc.world, pos.offset(facing)))
                return null;
            return raytrace(pos.offset(facing)) == null ? null : pos.offset(facing);
        }
        for(EnumFacing enumFacing : EnumFacing.VALUES){
            if(
                    enumFacing == EnumFacing.DOWN
                    || enumFacing == facing.getOpposite()
            ){
                continue;
            }
            if(!mc.world.getBlockState(pos.offset(enumFacing)).getBlock().isReplaceable(mc.world, pos.offset(enumFacing)))
                continue;
            if(placeRaytrace.getValBoolean() && raytrace(pos.offset(enumFacing)) == null)
                continue;
            return pos.offset(enumFacing);
        }
        return null;
    }

    private Vec3d raytrace(BlockPos pos){
        for(double x = pos.getX() + 0.05; x < pos.getX() + 1; x += 0.45){
            for(double y = pos.getY() + 0.05; y < pos.getY() + 1; y += 0.45){
                for(double z = pos.getZ() + 0.05; z < pos.getZ() + 1; z += 0.45){
                    Vec3d vec3d = new Vec3d(x, y, z);
                    RayTraceResult result = mc.world.rayTraceBlocks(mc.player.getPositionVector().addVector(0, mc.player.eyeHeight, 0), vec3d);
                    if(result != null && result.getBlockPos() == pos && isInDistance(vec3d))
                        return vec3d;
                }
            }
        }
        return null;
    }

    private float[] calculateAngle(Vec3d from, Vec3d to) {
        return new float[] {
                (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(to.z - from.z, to.x - from.x)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2((to.y - from.y) * -1.0, MathHelper.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.z - from.z, 2)))))
        };
    }


    private static final class PlaceInfo {

        public final BlockPos pistonPos;
        public final BlockPos redstonePos;
        public final BlockPos crystalPos;
        public final EnumFacing facing;

        public PlaceInfo(BlockPos pistonPos, BlockPos redstonePos, BlockPos crystalPos, EnumFacing facing) {
            this.pistonPos = pistonPos;
            this.redstonePos = redstonePos;
            this.crystalPos = crystalPos;
            this.facing = facing;
        }
    }
}
