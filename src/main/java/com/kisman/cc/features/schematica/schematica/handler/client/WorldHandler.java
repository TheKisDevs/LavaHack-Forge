package com.kisman.cc.features.schematica.schematica.handler.client;

import com.kisman.cc.features.schematica.schematica.client.renderer.RenderSchematic;
import com.kisman.cc.features.schematica.schematica.client.world.SchematicWorld;
import com.kisman.cc.features.schematica.schematica.proxy.ClientProxy;
import com.kisman.cc.features.schematica.schematica.reference.Reference;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldHandler {
    @SubscribeEvent
    public void onLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        if (world.isRemote && !(world instanceof SchematicWorld)) {
            RenderSchematic.INSTANCE.setWorldAndLoadRenderers(ClientProxy.schematic);
            addWorldAccess(world, RenderSchematic.INSTANCE);
        }
    }

    @SubscribeEvent
    public void onUnload(WorldEvent.Unload event) {
        World world = event.getWorld();
        if (world.isRemote) {
            removeWorldAccess(world, RenderSchematic.INSTANCE);
        }
    }

    public static void addWorldAccess(World world, IWorldEventListener schematic) {
        if (world != null && schematic != null) {
            Reference.logger.debug("Adding world access to {}", world);
            world.addEventListener(schematic);
        }
    }

    public static void removeWorldAccess(World world, IWorldEventListener schematic) {
        if (world != null && schematic != null) {
            Reference.logger.debug("Removing world access from {}", world);
            world.removeEventListener(schematic);
        }
    }
}
