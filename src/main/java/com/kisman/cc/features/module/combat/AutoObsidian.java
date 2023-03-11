package com.kisman.cc.features.module.combat;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.features.subsystem.subsystems.EnemyManagerKt;
import com.kisman.cc.features.subsystem.subsystems.Target;
import com.kisman.cc.features.subsystem.subsystems.Targetable;
import com.kisman.cc.features.subsystem.subsystems.TargetsNearest;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.render.Rendering;
import com.kisman.cc.util.world.BlockUtil;
import com.kisman.cc.util.world.BlockUtil2;
import com.kisman.cc.util.world.WorldUtilKt;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Work in progress, not sure if i wanna keep it
 * Don't add to module manager
 * @author Cubic
 */
@Targetable
@TargetsNearest
@ModuleInfo(
        name = "AutoObsidian",
        category = Category.COMBAT,
        wip = true
)
public class AutoObsidian extends Module {
    private final Setting circleRange = register(new Setting("CircleRange", this, 2, 1, 5, false));
    private final Setting singlePlace = register(new Setting("SinglePlace", this, true));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));
    private final Setting swap = register(new Setting("Switch", this, "Silent", Arrays.asList("Normal", "Silent", "Packet")));
    private final Setting updateController = register(new Setting("UpdateController", this, false));

    @ModuleInstance
    public static AutoObsidian instance;

    @Target
    public EntityPlayer target;

    private List<BlockPos> positions = new ArrayList<>();

    private BlockPos last = null;

    public boolean isBeta(){
        return true;
    }

    public void update(){
        if(mc.world == null || mc.player == null)
            return;

        positions.clear();

        int slot = InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        if(slot == -1)
            return;

        int oldSlot = mc.player.inventory.currentItem;

        target = EnemyManagerKt.nearest();

        if(target == null)
            return;

        List<BlockPos> blockPos = getBlocks(target);

        if(blockPos.isEmpty()){
            last = null;
            return;
        }

        if(singlePlace.getValBoolean() && last == blockPos.get(0))
            return;

        swap(slot, false);

        if(singlePlace.getValBoolean()){
            BlockUtil2.placeBlock(blockPos.get(0), EnumHand.MAIN_HAND, packet.getValBoolean(), false, rotate.getValBoolean());
            last = blockPos.get(0);
            ChatUtility.message().printClientModuleMessage(last.getX() + " " + last.getY() + " " + last.getZ());
        } else {
            for(BlockPos pos : blockPos){
                BlockUtil2.placeBlock(pos, EnumHand.MAIN_HAND, packet.getValBoolean(), false, rotate.getValBoolean());
            }
            last = null;
        }

        swap(oldSlot, true);

        positions = blockPos;
    }

    public BlockPos getBlockPos(int index){
        try {
            return positions.get(index);
        } catch (Exception ignored){
            return null;
        }
    }

    public void swap(int slot, boolean swapBack){
        switch (swap.getValString()){
            case "Normal":
                if(swapBack)
                    break;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                mc.player.inventory.currentItem = slot;
                break;
            case "Silent":
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                mc.player.inventory.currentItem = slot;
                break;
            case "Packet":
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                break;
        }

        if(updateController.getValBoolean())
            mc.playerController.updateController();
    }

    // unused
    public void onRender(RenderWorldLastEvent event){
        if(mc.world == null || mc.player == null)
            return;

        if(!isToggled())
            return;

        if(positions.isEmpty())
            return;

        /*
        for(BlockPos pos : positions){
            AxisAlignedBB aabb = Rendering.correct(new AxisAlignedBB(pos));
            Rendering.draw(aabb, 2f, new Colour(255, 255, 255, 120), Rendering.DUMMY_COLOR, Rendering.Mode.BOTH);
        }
         */
        AxisAlignedBB aabb = Rendering.correct(new AxisAlignedBB(positions.get(0)));
        Rendering.draw(aabb, 2f, new Colour(255, 255, 255, 120), Rendering.DUMMY_COLOR, Rendering.Mode.BOX_OUTLINE);
    }

    public static List<BlockPos> getBlocks(Entity entity){
        List<BlockPos> blocks = WorldUtilKt.sphere(entity, instance.circleRange.getValInt());
        return blocks.stream().filter(pos -> check(pos, entity)).sorted((o1, o2) -> {
            double d1 = entity.getDistanceSq(o1.getX() + 0.5, o1.getY() + 0.5, o1.getZ() + 0.5);
            double d2 = entity.getDistanceSq(o2.getX() + 0.5, o2.getY() + 0.5, o2.getZ() + 0.5);
            return Double.compare(d1, d2);
        }).collect(Collectors.toList());
    }

    public static boolean check(BlockPos pos, Entity entity){
        //if(pos == instance.last)
        //    return true;
        if(instance.singlePlace.getValBoolean() && !mc.world.getEntitiesWithinAABB(entity.getClass(), new AxisAlignedBB(pos.up())).isEmpty())
            return false;
        IBlockState state1 = mc.world.getBlockState(pos);
        if(!state1.getBlock().isReplaceable(mc.world, pos))
            return false;
        if(BlockUtil.getPossibleSides(pos).isEmpty())
            return false;
        IBlockState state2 = mc.world.getBlockState(pos.up());
        Block block = state2.getBlock();
        boolean b = block == Blocks.AIR;
        IBlockState state3 = mc.world.getBlockState(pos.up(2));
        return state3.getBlock() == Blocks.AIR && b;
    }
}
