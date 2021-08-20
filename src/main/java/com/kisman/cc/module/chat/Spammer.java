package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

import com.kisman.cc.settings.Setting;

import i.gishreloaded.gishcode.utils.TimerUtils;

public class Spammer extends Module{
    String[] msg = new String[] {
        "xlllGhostlllx on top!",
        "_kisman_ on top!",
        "mudonna gay",
        "NO team ezz",
        "_kisman_ the best kisman.cc user!",
        "magisteroff lox",
        "NO team lox",
        "Softik on top!",
        "mudonna ez log!",
        "Azazel ez run!",
        "kisman.cc on top!",
        "DenYoyo on top!",
        "kisman.cc the best crystal pvp client!",
        "Buy RusherHack with code \"Robertoss\"!",
        "Softik the best duper!",
        "xelab4ik on top!",
        "_kisman_ the best konas user!",
        "Robertoss on top!",
        "doxxxxxxxx the best packet fly user!",
        "NO team ezzzzz",
        "No team = Gay++ team!!!"
    };

    public TimerUtils timer;

    int count = 0;

    public Spammer() {
        super("Spammer", "chat spammer", Category.CHAT);

        timer = new TimerUtils();

        Kisman.instance.settingsManager.rSetting(new Setting("GlobalMode", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("Delay", this, 5.0, 1.0, 10.0, true));
    }

    public void update() {
        boolean globalMode = Kisman.instance.settingsManager.getSettingByName(this, "GlobalMode").getValBoolean();
        int delay = (int) Kisman.instance.settingsManager.getSettingByName(this, "Delay").getValDouble();

        if(timer.isDelay((long) (100 * delay))) {
            mc.player.sendChatMessage(globalMode ? "!" + msg[count] : msg[count]);
        }
        timer.setLastMS();
    
        count = count == msg.length - 1 ? 0 : count++;
    }
}
