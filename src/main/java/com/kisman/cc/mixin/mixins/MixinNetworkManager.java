package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventDisconnect;
import com.kisman.cc.event.events.NetworkPacketEvent;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.mixin.accessors.INetworkManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetworkManager.class, priority = 10000)
public abstract class MixinNetworkManager implements INetworkManager {
    @Shadow public boolean isChannelOpen() {return false;}

    @Shadow private void flushOutboundQueue() {}

    @Shadow private void dispatchPacket(Packet<?> par1, GenericFutureListener<? extends Future<? super Void>>[] par2) {}

    @Shadow private Channel channel;

    @Shadow public void setConnectionState(EnumConnectionState newState) {}

    @Shadow public void sendPacket(Packet<?> packetIn) {}

    @Shadow @Final private EnumPacketDirection direction;

    @Shadow protected abstract void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet<?> p_channelRead0_2_) throws Exception;

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> p_Packet, CallbackInfo callbackInfo) {
        NetworkPacketEvent event = new NetworkPacketEvent(p_Packet);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) callbackInfo.cancel();
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void preSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        PacketEvent.Send event = new PacketEvent.Send(packet);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) callbackInfo.cancel();
    }

    @Inject(method = "channelRead0*", at = @At("HEAD"), cancellable = true)
    private void preChannelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
        PacketEvent.Receive event = new PacketEvent.Receive(packet);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) callbackInfo.cancel();
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("TAIL"), cancellable = true)
    private void postSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        PacketEvent.PostSend event = new PacketEvent.PostSend(packet);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) callbackInfo.cancel();
    }

    @Inject(method = "channelRead0*", at = @At("TAIL"), cancellable = true)
    private void postChannelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
        PacketEvent.PostReceive event = new PacketEvent.PostReceive(packet);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) callbackInfo.cancel();
    }

    @Inject(method = "closeChannel", at = @At(value = "INVOKE", target = "Lio/netty/channel/Channel;isOpen()Z", remap = false))
    public void doCloseChannel(ITextComponent message, CallbackInfo ci) {
        if(isChannelOpen()) Kisman.EVENT_BUS.post(new EventDisconnect(message));
    }

    @Override
    public void sendPacketNoEvent(Packet<?> packet, boolean post) {
        if(isChannelOpen()) {
            flushOutboundQueue();

            if(post) dispatchPacket(packet, null);
            else dispatchSilently(packet);
        }
    }

    private void dispatchSilently(Packet<?> packet) {
        EnumConnectionState state = EnumConnectionState.getFromPacket(packet);
        EnumConnectionState protocolState = channel.attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get();

        boolean flag = state != protocolState;

        if(flag) channel.config().setAutoRead(false);
        if(channel.eventLoop().inEventLoop()) {
            if(flag) setConnectionState(state);

            ChannelFuture channelFuture = channel.writeAndFlush(packet);
            channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            channel.eventLoop().execute(() -> {
                if(flag) setConnectionState(state);

                ChannelFuture channelFuture = channel.writeAndFlush(packet);
                channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }

    @Override
    public void sendPacketNoEvent(Packet<?> packet) {
        sendPacketNoEvent(packet, true);
    }

    @Override
    public void channelRead00(ChannelHandlerContext channel, @NotNull Packet<?> packet) {
        try {
            channelRead0(channel, packet);
        } catch (Exception ignored) { }
    }
}
