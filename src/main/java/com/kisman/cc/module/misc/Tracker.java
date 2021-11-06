package com.kisman.cc.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.event.events.EventSpawnEntity;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.combat.AutoCrystalBypass;
import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.utils.TimerUtils;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Tracker extends Module {
    private Setting autoEnable = new Setting("AutoEnable", this, false);
    private Setting autoDisable = new Setting("AutoDisable", this, true);

    private final TimerUtils timer = new TimerUtils();
    private final Set<BlockPos> manuallyPlaced = new HashSet<BlockPos>();
    private EntityPlayer trackedPlayer;
    private int usedExp = 0;
    private int usedStacks = 0;
    private int usedCrystals = 0;
    private int usedCStacks = 0;
    private boolean shouldEnable = false;

    public Tracker() {
        super("Tracker", "Tracks players in 1v1s. Only good in duels tho!", Category.MISC);

        Kisman.EVENT_BUS.subscribe(listener2);
        Kisman.EVENT_BUS.subscribe(listener3);

        setmgr.rSetting(autoEnable);
        setmgr.rSetting(autoDisable);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(listener2);

        this.manuallyPlaced.clear();
//        AntiTrap.placedPos.clear();
        this.shouldEnable = false;
        this.trackedPlayer = null;
        this.usedExp = 0;
        this.usedStacks = 0;
        this.usedCrystals = 0;
        this.usedCStacks = 0;
    }

    public void onDisable() {
        this.manuallyPlaced.clear();
//        AntiTrap.placedPos.clear();
        this.shouldEnable = false;
        this.trackedPlayer = null;
        this.usedExp = 0;
        this.usedStacks = 0;
        this.usedCrystals = 0;
        this.usedCStacks = 0;

        Kisman.EVENT_BUS.unsubscribe(listener1);
        Kisman.EVENT_BUS.unsubscribe(listener2);
    }

    public void update() {
        if(trackedPlayer == null) {

        } else {
            if (this.usedStacks != this.usedExp / 64) {
                this.usedStacks = this.usedExp / 64;
                ChatUtils.message(this.trackedPlayer.getName() + " used: " + this.usedStacks + " Stacks of EXP.");
            }

            if (this.usedCStacks != this.usedCrystals / 64) {
                this.usedCStacks = this.usedCrystals / 64;
                ChatUtils.message(this.trackedPlayer.getName() + " used: " + this.usedCStacks + " Stacks of Crystals.");
            }
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if(autoDisable.getValBoolean()) {
            super.setToggled(false);
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if(event.getEntity().equals(mc.player) || event.getEntity().equals(trackedPlayer)) {
            this.usedExp = 0;
            this.usedStacks = 0;
            this.usedCrystals = 0;
            this.usedCStacks = 0;

            if(autoDisable.getValBoolean()) {
                super.setToggled(false);
            }
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener1 = new Listener<>(event -> {
        if (mc.player != null && mc.world != null && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
            if (Tracker.mc.player.getHeldItem(packet.hand).getItem() == Items.END_CRYSTAL && !AntiTrap.placedPos.contains(packet.position) && !AutoCrystalBypass.instance.placedCrystal.contains(packet.position)) {
                this.manuallyPlaced.add(packet.position);
            }
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> listener2 = new Listener<>(event -> {
        if (mc.player != null && mc.world != null && (this.autoEnable.getValBoolean() || this.autoDisable.getValBoolean()) && event.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = (SPacketChat)event.getPacket();
            final String message = packet.getChatComponent().getFormattedText();
            if (this.autoEnable.getValBoolean() && (message.contains("has accepted your duel request") || message.contains("Accepted the duel request from")) && !message.contains("<")) {
                ChatUtils.message("Tracker will enable in 5 seconds.");
                this.timer.reset();
                this.shouldEnable = true;
            }
            else if (this.autoDisable.getValBoolean() && message.contains("has defeated") && message.contains(Tracker.mc.player.getName()) && !message.contains("<")) {
                super.setToggled(false);
            }
        }
    });

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener3 = new Listener<>(event -> {
        if(shouldEnable && timer.passedSec(5L) && !super.isToggled()) {
            super.setToggled(true);
        }
    });

    @EventHandler
    private final Listener<EventSpawnEntity> listener4 = new Listener<>(event -> {
        if (event.entity instanceof EntityExpBottle && Objects.equals(Tracker.mc.world.getClosestPlayerToEntity(entity, 3.0), this.trackedPlayer)) {
            ++this.usedExp;
        }

        if (event.entity instanceof EntityEnderCrystal) {
            if (AntiTrap.placedPos.contains(event.entity.getPosition().down())) {
                AntiTrap.placedPos.remove(event.entity.getPosition().down());
            } else if (this.manuallyPlaced.contains(event.entity.getPosition().down())) {
                this.manuallyPlaced.remove(event.entity.getPosition().down());
            } else if (!AutoCrystalBypass.instance.placedCrystal.contains(event.entity.getPosition().down())) {
                ++this.usedCrystals;
            }
        }
    });
}