package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.WorkInProgress;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.util.RenderingRewritePattern;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.world.CrystalUtils;
import com.kisman.cc.util.world.HoleUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@WorkInProgress
public class CityESP extends Module {
    private final Setting range = register(new Setting("Range", this, 20, 1, 30, true));
    private final Setting down = register(new Setting("Down", this, 1, 0, 3, true));
    private final Setting sides = register(new Setting("Sides", this, 1, 0, 4, true));
    private final Setting depth = register(new Setting("Depth", this, 3, 0, 10, true));
    private final Setting minDMG = register(new Setting("Min DMG", this, 10, 0, 20, true));
    private final Setting maxSelfDMG = register(new Setting("Max Self DMG", this, 7, 0, 20, true));
    private final Setting ignoreCrystals = register(new Setting("Ignore Crystals", this, true));
    private final Setting mine = register(new Setting("Mine", this, false));
    private final Setting mineKey = register(new Setting("Mine Key", this, Keyboard.KEY_LSHIFT));
    private final Setting switchPick = register(new Setting("Switch Pick", this, true));
    private final Setting mineDist = register(new Setting("Mine Dist", this, 5, 0, 10, true));
    private final Setting mineMode = register(new Setting("Mine Mode", this, MineMode.Packet));
    private final Setting targetMode = register(new Setting("Target Mode", this, TargetMode.Single));
    private final Setting selectMode = register(new Setting("Select Mode", this, SelectMode.Closest));

    private final SettingGroup rendererGroup = register(new SettingGroup(new Setting("Renderer", this)));
    private final RenderingRewritePattern renderer = new RenderingRewritePattern(this).group(rendererGroup).preInit().init();

    private final HashMap<EntityPlayer, List<BlockPos>> cityable = new HashMap<>();
    private boolean packetMined = false;
    private BlockPos coordsPacketMined = new BlockPos(-1, -1, -1);

    public CityESP() {
        super("CityESP", "CityESP", Category.RENDER);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        cityable.clear();

        List<EntityPlayer> players = mc.world.playerEntities.stream()
                .filter(entityPlayer -> entityPlayer != mc.player)
                .filter(entityPlayer -> entityPlayer.getDistanceSq(mc.player) <= range.getValDouble() * range.getValDouble())
                .collect(Collectors.toList());

        for(EntityPlayer player : players) {
            List<BlockPos> blocks = EntityUtil.getBlocksIn(player);
            if(blocks.size() == 0) continue;
            int minY = Integer.MAX_VALUE;
            for (BlockPos block : blocks) {
                int y = block.getY();
                if (y < minY) minY = y;
            }
            if (player.posY % 1 > .2) minY++;
            int finalMinY = minY;
            blocks = blocks.stream().filter(blockPos -> blockPos.getY() == finalMinY).collect(Collectors.toList());
            Optional<BlockPos> any = blocks.stream().findAny();
            if (!any.isPresent()) continue;
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(any.get(), false, true);
            if (holeInfo.getType() == HoleUtil.HoleType.NONE || holeInfo.getSafety() == HoleUtil.BlockSafety.UNBREAKABLE) continue;
            List<BlockPos> sides = new ArrayList<>();
            for (BlockPos block : blocks) sides.addAll(cityableSides(block, HoleUtil.getUnsafeSides(block).keySet(), player));
            if (sides.size() > 0) cityable.put(player, sides);
        }

        if(mine.getValBoolean()) {
            if(mineKey.getKey() != Keyboard.KEY_NONE && Keyboard.isKeyDown(mineKey.getKey())) {
                for(List<BlockPos> poss : cityable.values()) {
                    boolean found = false;
                    for(BlockPos block : poss) {
                        if (mc.player.getDistance(block.getX(), block.getY(), block.getZ()) <= mineDist.getValInt()) {
                            found = true;
                            if (packetMined && coordsPacketMined == block) break;

                            if (mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_PICKAXE && switchPick.getValBoolean()) {
                                int slot = InventoryUtil.findFirstItemSlot(ItemPickaxe.class, 0, 9);
                                if (slot != 1) mc.player.inventory.currentItem = slot;
                            }

                            if(mineMode.getValString().equals(MineMode.Packet.name())) {
                                mc.player.swingArm(EnumHand.MAIN_HAND);
                                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, block, EnumFacing.UP));
                                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block, EnumFacing.UP));
                                packetMined = true;
                                coordsPacketMined = block;
                            } else {
                                mc.player.swingArm(EnumHand.MAIN_HAND);
                                mc.playerController.onPlayerDamageBlock(block, EnumFacing.UP);
                            }
                            break;
                        }
                    }
                    if (found) break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        AtomicBoolean noRender = new AtomicBoolean(false);

        cityable.entrySet().stream().sorted((entry, entry1) -> (int) entry.getKey().getDistanceSq(entry1.getKey())).forEach((entry) -> {
            if (noRender.get()) return;
            render(entry.getValue());
            if (targetMode.getValString().equalsIgnoreCase(TargetMode.All.name())) noRender.set(true);
        });
    }

    private List<BlockPos> cityableSides(BlockPos centre, Set<HoleUtil.BlockOffset> weakSides, EntityPlayer player) {
        List<BlockPos> cityableSides = new ArrayList<>();
        HashMap<BlockPos, HoleUtil.BlockOffset> directions = new HashMap<>();
        for (HoleUtil.BlockOffset weakSide : weakSides) {
            BlockPos pos = weakSide.offset(centre);
            if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR) directions.put(pos, weakSide);
        }

        try {
            directions.forEach(((blockPos, blockOffset) -> {
                if (blockOffset == HoleUtil.BlockOffset.DOWN) return;
                BlockPos pos1 = blockOffset.left(blockPos.down(down.getValInt()), sides.getValInt());
                BlockPos pos2 = blockOffset.forward(blockOffset.right(blockPos, sides.getValInt()), depth.getValInt());
                List<BlockPos> square = getSquare(pos1, pos2);
                IBlockState holder = mc.world.getBlockState(blockPos);
                mc.world.setBlockToAir(blockPos);

                for (BlockPos pos : square) {
                    if (CrystalUtils.canPlaceCrystal(pos.down(), true, ignoreCrystals.getValBoolean())) {
                        if (CrystalUtils.calculateDamage(mc.world, (double) pos.getX() + 0.5d, pos.getY(), (double) pos.getZ() + 0.5d, player, false) >= minDMG.getValInt()) {
                            if (CrystalUtils.calculateDamage(mc.world, (double) pos.getX() + 0.5d, pos.getY(), (double) pos.getZ() + 0.5d, mc.player, false) <= maxSelfDMG.getValInt())
                                cityableSides.add(blockPos);
                            break;
                        }
                    }
                }

                mc.world.setBlockState(blockPos, holder);
            }));
        } catch (Exception ignored) {}

        return cityableSides;
    }

    private void render(List<BlockPos> posList) {
        switch (selectMode.getValString()) {
            case "Closest": {
                posList.stream().min(Comparator.comparing(blockPos -> blockPos.distanceSq((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ))).ifPresent(renderer::draw);
                break;
            }
            case "All": {
                posList.forEach(renderer::draw);
                break;
            }
        }
    }

    public List<BlockPos> getSquare(BlockPos pos1, BlockPos pos2) {
        List<BlockPos> squareBlocks = new ArrayList<>();
        int x1 = pos1.getX();
        int y1 = pos1.getY();
        int z1 = pos1.getZ();
        int x2 = pos2.getX();
        int y2 = pos2.getY();
        int z2 = pos2.getZ();
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x += 1) {
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z += 1) {
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y += 1) {
                    squareBlocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return squareBlocks;
    }

    public enum MineMode {Packet, Vanilla}
    public enum TargetMode {Single, All}
    public enum SelectMode {Closest, All}
}
