package com.kisman.cc.features.schematica.schematica.handler.client;

import com.kisman.cc.features.schematica.schematica.Schematica;
import com.kisman.cc.features.schematica.schematica.client.printer.SchematicPrinter;
import com.kisman.cc.features.schematica.schematica.client.world.SchematicWorld;
import com.kisman.cc.features.schematica.schematica.proxy.ClientProxy;
import com.kisman.cc.features.schematica.schematica.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class TickHandler {
    public static final TickHandler INSTANCE = new TickHandler();

    private final Minecraft minecraft = Minecraft.getMinecraft();

    private TickHandler() {}

    @SubscribeEvent
    public void onClientDisconnect(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Reference.logger.info("Scheduling client settings reset.");
        ClientProxy.isPendingReset = true;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.minecraft.isGamePaused() || event.phase != TickEvent.Phase.END) {
            return;
        }

        minecraft.mcProfiler.startSection("schematica");
        WorldClient world = this.minecraft.world;
        EntityPlayerSP player = this.minecraft.player;
        SchematicWorld schematic = ClientProxy.schematic;
        if (world != null && player != null && schematic != null && schematic.isRendering) {
            minecraft.mcProfiler.startSection("printer");
            SchematicPrinter printer = SchematicPrinter.INSTANCE;
            if (printer.isEnabled() && printer.isPrinting()) {
                printer.print(world, player);
            }

            minecraft.mcProfiler.endSection();
        }

        if (ClientProxy.isPendingReset) {
            Schematica.proxy.resetSettings();
            ClientProxy.isPendingReset = false;
            Reference.logger.info("Client settings have been reset.");
        }

        minecraft.mcProfiler.endSection();
    }
}
