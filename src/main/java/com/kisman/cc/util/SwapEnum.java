package com.kisman.cc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import org.cubic.dynamictask.AbstractEnum;
import org.cubic.dynamictask.AbstractTask;

public class SwapEnum extends AbstractEnum<SwapEnum, AbstractTask<Void>> {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final Class<Void> retType = Void.class;

    private static final Class<?>[] types = {Integer.class, Boolean.class};

    public static final SwapEnum NORMAL = new SwapEnum("Normal", AbstractTask.types(retType, types).task(arg -> {
        int slot = arg.fetch(0);
        boolean swapBack = arg.fetch(1);
        if(swapBack)
            return null;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        return null;
    }));

    public static final SwapEnum SILENT = new SwapEnum("Silent", AbstractTask.types(retType, types).task(arg -> {
        int slot = arg.fetch(0);
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        return null;
    }));

    public static final SwapEnum PACKET = new SwapEnum("Packet", AbstractTask.types(retType, types).task(arg -> {
        int slot = arg.fetch(0);
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        return null;
    }));

    public SwapEnum(String name, AbstractTask<Void> task) {
        super(name, task);
    }

    @Override
    public AbstractEnum<SwapEnum, AbstractTask<Void>>[] values() {
        return new SwapEnum[]{NORMAL, SILENT, PACKET};
    }
}
