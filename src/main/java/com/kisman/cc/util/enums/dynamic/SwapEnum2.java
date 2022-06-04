package com.kisman.cc.util.enums.dynamic;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import org.cubic.dynamictask.AbstractTask;

public class SwapEnum2 {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final AbstractTask.DelegateAbstractTask<Void> task = AbstractTask.types(Void.class, Integer.class, Boolean.class);

    public enum Swap {
        Normal(task.task(arg -> {
            if(arg.fetch(1))
                return null;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(arg.fetch(0)));
            mc.player.inventory.currentItem = arg.fetch(0);
            return null;
        })),

        Silent(task.task(arg -> {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(arg.fetch(0)));
            mc.player.inventory.currentItem = arg.fetch(1);
            return null;
        })),

        Packet(task.task(arg -> {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(arg.fetch(0)));
            return null;
        }));

        private final AbstractTask<Void> abstractTask;

        Swap(AbstractTask<Void> task) {
            this.abstractTask = task;
        }

        public AbstractTask<Void> getTask(){
            return abstractTask;
        }
    }
}
