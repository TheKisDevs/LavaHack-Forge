package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.VecRotation;
import com.kisman.cc.util.pyro.CrystalUtils;
import com.kisman.cc.util.pyro.CrystalUtils2;
import com.kisman.cc.util.pyro.Rotation;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class AutoCrystalBypass extends Module {
    private Setting ticks = new Setting("Ticks", this, 2, 0, 20, true);
    private Setting placeRange = new Setting("PlaceRange", this, 4.2f, 0, 6, false);
    private Setting breakRange = new Setting("BreakRange", this, 4.2f, 0, 6, false);
    private Setting minDMG = new Setting("MinDMG", this, 4, 0, 20, true);
    private Setting maxSelfDMG = new Setting("MaxSelfDMG", this, 4, 0, 20, true);
    private Setting pauseWhileEating = new Setting("PauseWhileEating", this, false);
    private Setting pauseIfHittingBlock = new Setting("PauseIfHittingBlock", this, false);

    EntityEnderCrystal crystal;
    AxisAlignedBB bb;
    VecRotation rot;
    Rotation r;
    ArrayList<EntityPlayer> playerTargets;
    Iterator<EntityPlayer> iterator;
    EntityPlayer player;
    int minX;
    int minY;
    int minZ;
    int maxX;
    int maxY;
    int maxZ;
    BlockPos selected;
    float selDmg;
    int x;
    int y;
    int z;
    BlockPos pos;
    IBlockState state;
    Iterator<EntityPlayer> iterator2;
    EntityPlayer player2;
    float selfDamage;
    float dmg;
    int i;
    ItemStack stack;
    VecRotation rot2;
    CPacketPlayerTryUseItemOnBlock cPacketPlayerTryUseItemOnBlock;
    Object o;
    int _ticks;

    public AutoCrystalBypass() {
        super("AutoCrystalBypass", "AutoCrystalBypass", Category.COMBAT);
        
        setmgr.rSetting(ticks);
        setmgr.rSetting(placeRange);
        setmgr.rSetting(breakRange);
        setmgr.rSetting(minDMG);
        setmgr.rSetting(maxSelfDMG);
        setmgr.rSetting(pauseWhileEating);
        setmgr.rSetting(pauseIfHittingBlock);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {
        if (event.getEra() == Event.Era.PRE) {
            if (!event.isCancelled()) {
                if (!AutoCrystal.instance.needPause()) {
                    if (_ticks > 0) {
                        --_ticks;
                    }
                    else {
                        crystal = (EntityEnderCrystal)this.mc.world.getLoadedEntityList().stream().filter(e -> e instanceof EntityEnderCrystal).map(e -> (EntityEnderCrystal)e).min(Comparator.comparing(e -> this.mc.player.getDistance(e))).orElse(null);
                        if (crystal != null && crystal.getDistance((Entity)this.mc.player) <= breakRange.getValDouble()) {
                            bb = crystal.getEntityBoundingBox();
                            rot = Kisman.instance.rotationUtils.searchCenter(bb, false, true, false, true);
                            if (rot != null) {
                                r = Kisman.instance.rotationUtils.limitAngleChange(Kisman.instance.rotationUtils.serverRotation, rot.getRotation(), 180.0f);
                                if (r != null) {
                                    event.setYaw(r.getYaw());
                                    event.setPitch(r.getPitch());
                                    event.cancel();
                                    this.mc.playerController.attackEntity((EntityPlayer)this.mc.player, (Entity)crystal);
                                    this.mc.player.swingArm(EnumHand.MAIN_HAND);
                                    this._ticks = (int) ticks.getValDouble();
                                    return;
                                }
                            }
                        }
                        playerTargets = new ArrayList<EntityPlayer>();
                        this.mc.world.playerEntities.iterator();
                        while (iterator.hasNext()) {
                            player = iterator.next();
                            if (!(player instanceof EntityPlayerSP) && player.getDistance((Entity)this.mc.player) < 25.0f && !player.isDead && player.getHealth() + player.getAbsorptionAmount() > 0.0f) {
                                playerTargets.add(player);
                            }
                        }
                        minX = (int)(this.mc.player.posX - placeRange.getValDouble());
                        minY = (int)(this.mc.player.posY - placeRange.getValDouble());
                        minZ = (int)(this.mc.player.posZ - placeRange.getValDouble());
                        maxX = (int)(this.mc.player.posX + placeRange.getValDouble());
                        maxY = (int)(this.mc.player.posY + placeRange.getValDouble());
                        maxZ = (int)(this.mc.player.posZ + placeRange.getValDouble());
                        selected = null;
                        selDmg = 0.0f;
                        for (x = minX; x <= maxX; ++x) {
                            for (y = minY; y <= maxY; ++y) {
                                for (z = minZ; z <= maxZ; ++z) {
                                    pos = new BlockPos(x, y, z);
                                    state = this.mc.world.getBlockState(pos);
                                    if ((state.getBlock() == Blocks.OBSIDIAN || state.getBlock() == Blocks.BEDROCK) && CrystalUtils2.canPlaceCrystalAt(pos, state)) {
                                        playerTargets.iterator();
                                        while (iterator2.hasNext()) {
                                            player2 = iterator2.next();
                                            selfDamage = CrystalUtils.calculateDamage((World)this.mc.world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, (Entity)this.mc.player, 0);
                                            dmg = CrystalUtils.calculateDamage((World)this.mc.world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, (Entity)player2, 0);
                                            if (selfDamage <= maxSelfDMG.getValDouble() && dmg > selDmg && dmg > minDMG.getValDouble()) {
                                                selected = pos;
                                                selDmg = dmg;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (selected != null) {
                            if (this.mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL && this.mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
                                i = 0;
                                while (i < 9) {
                                    stack = this.mc.player.inventory.getStackInSlot(i);
                                    if (!stack.isEmpty() && stack.getItem() == Items.END_CRYSTAL) {
                                        this.mc.player.inventory.currentItem = i;
                                        this.mc.playerController.updateController();
                                        break;
                                    }
                                    else {
                                        ++i;
                                    }
                                }
                            }
                            rot2 = Kisman.instance.rotationUtils.faceBlock(selected);
                            if (rot2 != null) {
                                event.setYaw(rot2.getRotation().getYaw());
                                event.setPitch(rot2.getRotation().getPitch());
                                event.cancel();
                                this.mc.getConnection();
                                new CPacketPlayerTryUseItemOnBlock(selected, EnumFacing.UP, (this.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f);
                                ((NetHandlerPlayClient)o).sendPacket((Packet)cPacketPlayerTryUseItemOnBlock);
                                this._ticks = (int) ticks.getValDouble();
                            }
                        }
                    }
                }
            }
        }
    });
}
