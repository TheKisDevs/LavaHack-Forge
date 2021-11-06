package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;

public class Criticals extends Module {
    private Setting mode = new Setting("Mode", this, Modes.Packet);
    private Setting onlyKillaura = new Setting("OnlyKillAura", this, false);

    public Criticals() {
        super("Criticals", "", Category.COMBAT);

        setmgr.rSetting(mode);
        setmgr.rSetting(onlyKillaura);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity) event.getPacket()).getAction() != CPacketUseEntity.Action.ATTACK && !(((CPacketUseEntity) event.getPacket()).getEntityFromWorld(mc.world) instanceof EntityEnderCrystal)) {
            if((onlyKillaura.getValBoolean() && KillAura.instance.isToggled()) || !onlyKillaura.getValBoolean()) {
                doCriticals(mode.getValString());
            }
        }
    });

    private void doCriticals(String mode) {
        if (!mode.equals("None")) {
            if (!mc.player.onGround) {
                return;
            }
            if (mc.player.isInWater() || mc.player.isInLava()) {
                return;
            }
            if (mode.equals("Packet")) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + Double.longBitsToDouble(Double.doubleToLongBits(257.1226152887829) ^ 0x7FC011F63B72F4FFL), mc.player.posZ, true));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + Double.longBitsToDouble(Double.doubleToLongBits(247433.60726351582) ^ 0x7FE925D8A756DFC6L), mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            } else if (mode.equals("Bypass")) {
                mc.player.motionY = Double.longBitsToDouble(Double.doubleToLongBits(98.57642382264255) ^ 0x7FE13D7D80BEB8A5L);
                mc.player.fallDistance = Float.intBitsToFloat(Float.floatToIntBits(178.62473f) ^ 0x7EFE5323);
                mc.player.onGround = false;
            } else {
                mc.player.jump();
                if (mode.equals("MiniJump")) {
                    mc.player.motionY /= Double.longBitsToDouble(Double.doubleToLongBits(0.9448694671300228) ^ 0x7FEE3C5EE489FF24L);
                }
            }
        }
    }

    public enum Modes {
        Packet,
        Jump,
        MiniJump,
        Bypass
    }
}
