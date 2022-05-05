package com.kisman.cc.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.Rendering;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cubic.eventsystem.Subscribe;

/**
 * Work in progress
 * @author Cubic
 */
public class ChorusDelay extends Module {

    public ChorusDelay(){
        super("ChorusDelay", Category.PLAYER);
    }

    private CPacketConfirmTeleport teleport = null;

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.world == null || mc.player == null)
            return;

        if(!isToggled())
            return;

        AxisAlignedBB aabb = mc.player.getEntityBoundingBox();

        Rendering.draw(Rendering.correct(aabb), 2f, new Colour(255, 255, 255, 120), Rendering.DUMMY_COLOR, Rendering.Mode.BOTH);
    }

    @EventHandler
    private final Listener<PacketEvent.Send> packetEvent = new Listener<>(event -> {
        if(!(event.getPacket() instanceof CPacketConfirmTeleport))
            return;
        teleport = (CPacketConfirmTeleport) event.getPacket();
        event.cancel();
    });

    @Override
    public void onEnable(){
        Kisman.EVENT_BUS.subscribe(packetEvent);
    }

    @Override
    public void onDisable(){
        Kisman.EVENT_BUS.unsubscribe(packetEvent);
        if(mc.player == null || mc.world == null)
            return;
        if(teleport == null)
            return;
        mc.player.connection.sendPacket(teleport);
        mc.playerController.updateController();
    }
}
