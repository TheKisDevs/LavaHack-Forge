package com.kisman.cc.features.schematica.schematica.network.message;

import com.kisman.cc.features.schematica.schematica.handler.DownloadHandler;
import com.kisman.cc.features.schematica.schematica.network.transfer.SchematicTransfer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDownloadBeginAck implements IMessage, IMessageHandler<MessageDownloadBeginAck, IMessage> {
    @Override
    public void fromBytes(final ByteBuf buf) {
        // NOOP
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        // NOOP
    }

    @Override
    public IMessage onMessage(final MessageDownloadBeginAck message, final MessageContext ctx) {
        final EntityPlayerMP player = ctx.getServerHandler().player;
        final SchematicTransfer transfer = DownloadHandler.INSTANCE.transferMap.get(player);
        if (transfer != null) {
            transfer.setState(SchematicTransfer.State.CHUNK_WAIT);
        }

        return null;
    }
}
