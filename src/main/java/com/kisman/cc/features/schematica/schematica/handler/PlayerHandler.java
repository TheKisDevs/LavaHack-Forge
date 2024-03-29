package com.kisman.cc.features.schematica.schematica.handler;

import com.kisman.cc.features.schematica.schematica.network.PacketHandler;
import com.kisman.cc.features.schematica.schematica.network.message.MessageCapabilities;
import com.kisman.cc.features.schematica.schematica.reference.Reference;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerHandler {
    public static final PlayerHandler INSTANCE = new PlayerHandler();

    private PlayerHandler() {}

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            try {
                PacketHandler.INSTANCE.sendTo(new MessageCapabilities(ConfigurationHandler.printerEnabled, ConfigurationHandler.saveEnabled, ConfigurationHandler.loadEnabled), (EntityPlayerMP) event.player);
            } catch (Exception ex) {
                Reference.logger.error("Failed to send capabilities!", ex);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            DownloadHandler.INSTANCE.transferMap.remove(event.player);
        }
    }
}
