package com.kisman.cc.module.modules.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity extends Module{
    public Velocity() {
        super("Velocity", "i hate being knocked back", Category.PLAYER);
        Kisman.instance.settingsManager.rSetting(new Setting("Horizontal", this, 90, 0, 100, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Vertical", this, 100, 0, 100, true));
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent e) {
        float horizontal = (float) Kisman.instance.settingsManager.getSettingByName(this, "Horizontal").getValDouble();
        float vertical = (float) Kisman.instance.settingsManager.getSettingByName(this, "Vertical").getValDouble();

        if (mc.player.hurtTime == mc.player.maxHurtTime && mc.player.maxHurtTime > 0) {
            mc.player.motionX *= (float) horizontal / 100;
            mc.player.motionY *= (float) vertical / 100;
            mc.player.motionZ *= (float) horizontal / 100;
        }
    }

//    @EventHandler
//    private final Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
//        if(event.getPacket() instanceof SPacketEntityVelocity) {
//            if(((SPacketEntityVelocity) event.getPacket()).getEntityID() == Minecraft.getMinecraft().player.getEntityId()) {
//                event.cancel();
//            }
//        }
//        if(event.getPacket() instanceof SPacketExplosion) {
//            event.cancel();
//        }
//    });
}
