package com.kisman.cc.features.schematica.schematica.handler;

import com.kisman.cc.features.schematica.schematica.api.ISchematic;
import com.kisman.cc.features.schematica.schematica.network.PacketHandler;
import com.kisman.cc.features.schematica.schematica.network.message.MessageDownloadBegin;
import com.kisman.cc.features.schematica.schematica.network.message.MessageDownloadChunk;
import com.kisman.cc.features.schematica.schematica.network.message.MessageDownloadEnd;
import com.kisman.cc.features.schematica.schematica.network.transfer.SchematicTransfer;
import com.kisman.cc.features.schematica.schematica.reference.Constants;
import com.kisman.cc.features.schematica.schematica.reference.Reference;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class DownloadHandler {
    public static final DownloadHandler INSTANCE = new DownloadHandler();

    public ISchematic schematic = null;

    public final Map<EntityPlayerMP, SchematicTransfer> transferMap = new LinkedHashMap<EntityPlayerMP, SchematicTransfer>();

    private DownloadHandler() {}

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        processQueue();
    }

    private void processQueue() {
        if (transferMap.size() == 0) {
            return;
        }

        EntityPlayerMP player = transferMap.keySet().iterator().next();
        SchematicTransfer transfer = transferMap.remove(player);

        if (transfer == null) {
            return;
        }

        if (!transfer.state.isWaiting()) {
            if (++transfer.timeout >= Constants.Network.TIMEOUT) {
                if (++transfer.retries >= Constants.Network.RETRIES) {
                    Reference.logger.warn("{}'s download was dropped!", player.getName());
                    return;
                }

                Reference.logger.warn("{}'s download timed out, retrying (#{})", player.getName(), transfer.retries);

                sendChunk(player, transfer);
                transfer.timeout = 0;
            }
        } else if (transfer.state == SchematicTransfer.State.BEGIN_WAIT) {
            sendBegin(player, transfer);
        } else if (transfer.state == SchematicTransfer.State.CHUNK_WAIT) {
            sendChunk(player, transfer);
        } else if (transfer.state == SchematicTransfer.State.END_WAIT) {
            sendEnd(player, transfer);
            return;
        }

        transferMap.put(player, transfer);
    }

    private void sendBegin(EntityPlayerMP player, SchematicTransfer transfer) {
        transfer.setState(SchematicTransfer.State.BEGIN);

        MessageDownloadBegin message = new MessageDownloadBegin(transfer.schematic);
        PacketHandler.INSTANCE.sendTo(message, player);
    }

    private void sendChunk(EntityPlayerMP player, SchematicTransfer transfer) {
        transfer.setState(SchematicTransfer.State.CHUNK);

        Reference.logger.trace("Sending chunk {},{},{}", transfer.baseX, transfer.baseY, transfer.baseZ);
        MessageDownloadChunk message = new MessageDownloadChunk(transfer.schematic, transfer.baseX, transfer.baseY, transfer.baseZ);
        PacketHandler.INSTANCE.sendTo(message, player);
    }

    private void sendEnd(EntityPlayerMP player, SchematicTransfer transfer) {
        MessageDownloadEnd message = new MessageDownloadEnd(transfer.name);
        PacketHandler.INSTANCE.sendTo(message, player);
    }
}
