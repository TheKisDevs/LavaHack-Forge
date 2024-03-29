package com.kisman.cc.features.schematica.schematica.handler;

import com.kisman.cc.features.schematica.schematica.reference.Names;
import com.kisman.cc.features.schematica.schematica.reference.Reference;
import com.kisman.cc.features.schematica.schematica.world.chunk.SchematicContainer;
import com.kisman.cc.features.schematica.schematica.world.schematic.SchematicFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayDeque;
import java.util.Queue;

public class QueueTickHandler {
    public static final QueueTickHandler INSTANCE = new QueueTickHandler();

    private final Queue<SchematicContainer> queue = new ArrayDeque<SchematicContainer>();

    private QueueTickHandler() {}

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        // TODO: find a better way... maybe?
        try {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player != null && player.connection != null && !player.connection.getNetworkManager().isLocalChannel()) {
                processQueue();
            }
        } catch (Exception e) {
            Reference.logger.error("Something went wrong...", e);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        processQueue();
    }

    private void processQueue() {
        if (queue.size() == 0) {
            return;
        }

        SchematicContainer container = this.queue.poll();
        if (container == null) {
            return;
        }

        if (container.hasNext()) {
            if (container.isFirst()) {
                TextComponentTranslation component = new TextComponentTranslation(String.format(Names.Command.Save.Message.SAVE_STARTED, container.chunkCount, container.file.getName()));
                container.player.sendMessage(component);
            }

            container.next();
        }

        if (container.hasNext()) {
            queue.offer(container);
        } else {
            SchematicFormat.writeToFileAndNotify(container.file, container.format, container.schematic, container.player);
        }
    }

    public void queueSchematic(SchematicContainer container) {
        queue.offer(container);
    }
}
