package com.kisman.cc.event.events;

import com.kisman.cc.Kisman;
import com.kisman.cc.catlua.util.PacketUtil;
import com.kisman.cc.event.Event;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;

@SuppressWarnings("rawtypes")
public class PacketEvent extends Event {
	private final EnumPacketDirection direction;
	private final ChannelHandlerContext context;
	private final Packet packet;

	public PacketEvent(Packet packet) {
		this(packet, EnumPacketDirection.CLIENTBOUND);
	}

	public PacketEvent(Packet packet, EnumPacketDirection direction) {
		this(packet, direction, null);
	}

	public PacketEvent(Packet packet, EnumPacketDirection direction, ChannelHandlerContext context) {
		this.direction = direction;
		this.packet = packet;
		this.context = context;
	}

	public Packet getPacket() {
		return this.packet;
	}

	public EnumPacketDirection getDirection() {
		return this.direction;
	}
 
	public static class Receive extends PacketEvent {
		public Receive(Packet packet) {
			super(packet, EnumPacketDirection.CLIENTBOUND);
		}

		public String getName() {
			return "packet_receive";
		}

		public boolean is(String packet) {
			if(Kisman.remapped) return PacketUtil.cache.containsKey(packet) && PacketUtil.cache.get(packet).getSimpleName().equalsIgnoreCase(getPacket().getClass().getSimpleName());
			return getPacket().getClass().getSimpleName().equalsIgnoreCase(packet);
		}
	}
 
	public static class Send extends PacketEvent {
		public Send(Packet packet) {
			super(packet, EnumPacketDirection.SERVERBOUND);
		}

		public String getName() {
			return "packet_send";
		}

		public boolean is(String packet) {
			if(Kisman.remapped) return PacketUtil.cache.containsKey(packet) && PacketUtil.cache.get(packet).getSimpleName().equalsIgnoreCase(getPacket().getClass().getSimpleName());
			return getPacket().getClass().getSimpleName().equalsIgnoreCase(packet);
		}
	}
 
	public static class PostReceive extends PacketEvent {
		public PostReceive(Packet packet) {
			super(packet);
		}
	}
 
	public static class PostSend extends PacketEvent {
		public PostSend(Packet packet) {
			super(packet);
		}
	}
}
