package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.ArrayList;

public class Fly extends Module {
    private String mode;

    float flySpeed;

    public Fly() {
        super("Fly", "Your flying", Category.MOVEMENT);
        ArrayList<String> options = new ArrayList<>();
        options.add("Hypixel");
        options.add("Vanilla");
        //Kisman.instance.settingsManager.rSetting(new Setting("FlyMode", this, "Vanilla", options));
        Kisman.instance.settingsManager.rSetting(new Setting("FlySpeed", this, 0.1f, 0.1f, 100.0f, false));
    }

    public void update() {
        flySpeed = (float) Kisman.instance.settingsManager.getSettingByName(this, "FlySpeed").getValDouble();
        mode  = Kisman.instance.settingsManager.getSettingByName("FlyMode").getValString();

        if(Kisman.instance.settingsManager.getSettingByName("FlyMode").getValString() == "Hypixel") {//.equalsIgnoreCase("Hypixel")
//            double y;
//            double y1;
//            mc.player.motionY = 0;
//            if(mc.player.ticksExisted % 3 == 0) {
//                y = mc.player.posY - 1.0E-10D;
//                CPacketPlayer.Position;
//                mc.player.player
//                mc.player.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, y, mc.player.posZ, true));
//            }
//            y1 = mc.player.posY + 1.0E-10D;
//            mc.player.setPosition(mc.player.posX, y1, mc.player.posZ);
            mc.player.sendChatMessage("[" + Kisman.NAME + "] " + mc.player.getName() + ", this fly mode in dev");
        }

        if(Kisman.instance.settingsManager.getSettingByName("FlyMode").getValString() == "Vanilla") {//mode.equalsIgnoreCase("Vanilla")
            mc.player.capabilities.isFlying = true;
            mc.player.capabilities.setFlySpeed(flySpeed);
        }
    }

    public void onDisable() {
        if(Kisman.instance.settingsManager.getSettingByName("FlyMode").getValString() == "Vanilla") {
            mc.player.capabilities.isFlying = false;
            mc.player.capabilities.setFlySpeed(0.1f);
        }
    }
}
