package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventIngameOverlay;
import com.kisman.cc.event.events.EventSetupFog;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRender extends Module {
    public static NoRender instance;

    public Setting fog = register(new Setting("Fog", this, false));
    public Setting hurtCam = register(new Setting("HurtCam", this, false));
    public Setting armor = register(new Setting("Armor", this, false));
    public Setting overlay = register(new Setting("Overlay", this, false));
    public Setting guiOverlay = register(new Setting("Gui Overlay", this, false));
    public Setting book = register(new Setting("Book", this, false));
    public Setting chatBackground = register(new Setting("Chat Background", this, false));
    public Setting bossBar = register(new Setting("Boss Bar", this, false));
    public Setting scoreboard = /*register*/(new Setting("Scoreboard", this, false));
    public Setting particle = register(new Setting("Particle", this, ParticleMode.None));
    public Setting portal = register(new Setting("Portal", this, false));
    public Setting items = register(new Setting("Items", this, false));
    public Setting defaultBlockHighlight = register(new Setting("Default Block Highlight", this, false));
    public Setting handItemsTex  = /*register*/(new Setting("Hand Items Texture", this, false));
    public Setting enchantGlint = register(new Setting("Enchant Glint", this, false));
    private final Setting swing = register(new Setting("Swing", this, SwingMode.None));
    public Setting sway = register(new Setting("Sway",  this, false));
    private final Setting glow = register(new Setting("Glow", this, false));
    public final Setting potion = register(new Setting("Potion", this, false));
    private final Setting weather = register(new Setting("Weather", this, false));
    private final Setting block = register(new Setting("Block", this, false));
    private final Setting fire = register(new Setting("Fire", this, false));
    private final Setting lava = register(new Setting("Lava", this, false));
    private final Setting water = register(new Setting("Water", this, false));

    public NoRender() {
        super("NoRender", "no render", Category.RENDER);
        instance = this;
    }

    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(setupFog);
        Kisman.EVENT_BUS.subscribe(bossBar_);
        Kisman.EVENT_BUS.subscribe(pumpkin);
        Kisman.EVENT_BUS.subscribe(portal_);
        Kisman.EVENT_BUS.subscribe(overlay_);
        Kisman.EVENT_BUS.subscribe(send);
    }

    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(send);
        Kisman.EVENT_BUS.unsubscribe(overlay_);
        Kisman.EVENT_BUS.unsubscribe(portal_);
        Kisman.EVENT_BUS.unsubscribe(pumpkin);
        Kisman.EVENT_BUS.unsubscribe(bossBar_);
        Kisman.EVENT_BUS.unsubscribe(setupFog);
    }

    @EventHandler private final Listener<EventIngameOverlay.BossBar> bossBar_ = new Listener<>(event -> {
        if(bossBar.getValBoolean()) event.cancel();
    });

    @EventHandler private final Listener<EventSetupFog> setupFog = new Listener<>(event -> {
        if(fog.getValBoolean()) event.cancel();
    });

    @EventHandler private final Listener<EventIngameOverlay.Pumpkin> pumpkin = new Listener<>(event -> {
        if(overlay.getValBoolean()) event.cancel();
    });

    @EventHandler private final Listener<EventIngameOverlay.Portal> portal_ = new Listener<>(event -> {
        if(portal.getValBoolean()) event.cancel();
    });

    @EventHandler private final Listener<EventIngameOverlay.Overlay> overlay_ = new Listener<>(event -> {
        if(overlay.getValBoolean()) event.cancel();
    });

    @EventHandler private final Listener<PacketEvent.Send> send = new Listener<>(event -> {
        if(swing.checkValString("Server") && event.getPacket() instanceof CPacketAnimation) event.cancel();
    });

    public void update() {
        if(mc.player == null && mc.world == null) return;

        if(potion.getValBoolean()) {
            if(mc.player.isPotionActive(Potion.getPotionById(25))) mc.player.removeActivePotionEffect(Potion.getPotionById(25));
            if(mc.player.isPotionActive(Potion.getPotionById(2))) mc.player.removeActivePotionEffect(Potion.getPotionById(2));
            if(mc.player.isPotionActive(Potion.getPotionById(4))) mc.player.removeActivePotionEffect(Potion.getPotionById(4));
            if(mc.player.isPotionActive(Potion.getPotionById(9))) mc.player.removeActivePotionEffect(Potion.getPotionById(9));
            if(mc.player.isPotionActive(Potion.getPotionById(15))) mc.player.removeActivePotionEffect(Potion.getPotionById(15));
            if(mc.player.isPotionActive(Potion.getPotionById(17))) mc.player.removeActivePotionEffect(Potion.getPotionById(17));
            if(mc.player.isPotionActive(Potion.getPotionById(18))) mc.player.removeActivePotionEffect(Potion.getPotionById(18));
            if(mc.player.isPotionActive(Potion.getPotionById(27))) mc.player.removeActivePotionEffect(Potion.getPotionById(27));
            if(mc.player.isPotionActive(Potion.getPotionById(20))) mc.player.removeActivePotionEffect(Potion.getPotionById(20));
        }

        if(weather.getValBoolean()) mc.world.setRainStrength(0.0f);

        if(swing.checkValString("Client")) {
            mc.player.isSwingInProgress = false;
            mc.player.swingProgressInt = 0;
            mc.player.swingProgress = 0;
            mc.player.prevSwingProgress = 0;
        }

        if(glow.getValBoolean()) {
            for(Entity entity : mc.world.loadedEntityList) {
                entity.glowing = false;
            }
        }
    }

    @SubscribeEvent
    public void renderBlockEvent(RenderBlockOverlayEvent event) {
        /*
        if(mc.player != null && mc.world != null) {
            if(block.getValBoolean() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK) event.setCanceled(true);
            if(lava.getValBoolean() && event.getBlockForOverlay().getBlock().equals(Blocks.LAVA)) event.setCanceled(true);
        }
         */
        if(mc.player == null || mc.world == null)
            return;

        RenderBlockOverlayEvent.OverlayType overlayType = event.getOverlayType();

        if(block.getValBoolean() && overlayType == RenderBlockOverlayEvent.OverlayType.BLOCK) event.setCanceled(true);
        if(fire.getValBoolean() && overlayType == RenderBlockOverlayEvent.OverlayType.FIRE) event.setCanceled(true);
        if(lava.getValBoolean() && event.getBlockForOverlay().getBlock().equals(Blocks.LAVA)) event.setCanceled(true);
        if(water.getValBoolean() && overlayType == RenderBlockOverlayEvent.OverlayType.WATER) event.setCanceled(true);
    }

    public enum ParticleMode {None, All, AllButIgnorePops}
    public enum SwingMode {None, Client, Server}
}
