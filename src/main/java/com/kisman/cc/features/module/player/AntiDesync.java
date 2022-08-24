package com.kisman.cc.features.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.PingBypassModule;
import com.kisman.cc.settings.Setting;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketEntityAction;
import org.cubic.dynamictask.AbstractTask;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Cubic
 * @since 02.07.2022
 */
@PingBypassModule
public class AntiDesync extends Module {

    private final Setting syncPlayItem = register(new Setting("SyncItem", this, false));
    private final Setting processPackets = register(new Setting("ProcessPackets", this, true));
    private final Setting sneak = register(new Setting("Sneak", this, SneakModeEnum.SneakModes.Off));

    public static final AntiDesync INSTANCE = new AntiDesync();

    public AntiDesync(){
        super("AntiDesync", Category.PLAYER);
    }

    /**
     * Invoked from mixins.
     * This is so it runs at the very end of every tick to
     * ensure that all packets have been send in that tick.
     * @see com.kisman.cc.mixin.mixins.MixinMinecraft#runTickPost(CallbackInfo)
     */
    public void onClientTickPost(){
        if(mc.player == null || mc.world == null)
            return;

        Kisman.LOGGER.debug("AntiDesync on client tick post");

        if(syncPlayItem.getValBoolean())
            mc.playerController.syncCurrentPlayItem();

        ((SneakModeEnum.SneakModes) sneak.getValEnum()).getTask().doTask();

        if(processPackets.getValBoolean())
            processPackets();
    }

    private void processPackets(){
        NetworkManager networkManager = mc.playerController.connection.getNetworkManager();
        if(networkManager.isChannelOpen()){
            networkManager.processReceivedPackets();
            return;
        }
        networkManager.checkDisconnected();
    }

    private static class SneakModeEnum {

        private static final AbstractTask.DelegateAbstractTask<Void> task = AbstractTask.types(Void.class);

        public enum SneakModes {
            Off(task.task(arg -> null)),
            Packet(task.task(arg -> {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                return null;
            })),
            Vanilla(task.task(arg -> {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.player.setSneaking(true);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                mc.player.setSneaking(false);
                return null;
            }));

            private final AbstractTask<Void> abstractTask;

            SneakModes(AbstractTask<Void> task){
                this.abstractTask = task;
            }

            public AbstractTask<Void> getTask(){
                return abstractTask;
            }
        }
    }
}
