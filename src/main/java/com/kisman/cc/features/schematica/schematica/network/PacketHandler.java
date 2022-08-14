package com.kisman.cc.features.schematica.schematica.network;

import com.kisman.cc.features.schematica.schematica.network.message.MessageCapabilities;
import com.kisman.cc.features.schematica.schematica.network.message.MessageDownloadBegin;
import com.kisman.cc.features.schematica.schematica.network.message.MessageDownloadBeginAck;
import com.kisman.cc.features.schematica.schematica.network.message.MessageDownloadChunk;
import com.kisman.cc.features.schematica.schematica.network.message.MessageDownloadChunkAck;
import com.kisman.cc.features.schematica.schematica.network.message.MessageDownloadEnd;
import com.kisman.cc.features.schematica.schematica.reference.Reference;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);

    public static void init() {
        INSTANCE.registerMessage(MessageCapabilities.class, MessageCapabilities.class, 0, Side.CLIENT);

        INSTANCE.registerMessage(MessageDownloadBegin.class, MessageDownloadBegin.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(MessageDownloadBeginAck.class, MessageDownloadBeginAck.class, 2, Side.SERVER);
        INSTANCE.registerMessage(MessageDownloadChunk.class, MessageDownloadChunk.class, 3, Side.CLIENT);
        INSTANCE.registerMessage(MessageDownloadChunkAck.class, MessageDownloadChunkAck.class, 4, Side.SERVER);
        INSTANCE.registerMessage(MessageDownloadEnd.class, MessageDownloadEnd.class, 5, Side.CLIENT);
    }
}
