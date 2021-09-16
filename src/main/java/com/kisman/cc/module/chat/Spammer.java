package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

import com.kisman.cc.settings.Setting;

import i.gishreloaded.gishcode.utils.TimerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Spammer extends Module{
    ArrayList<String> spam;

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
        Kisman.instance.settingsManager.rSetting(new Setting("Delay", this, 5000, 1000, 10000, true));

        this.spam = new ArrayList<>(Arrays.asList(msg));
    }

    public void update() {
        boolean globalMode = Kisman.instance.settingsManager.getSettingByName(this, "GlobalMode").getValBoolean();
        long delay = (int) Kisman.instance.settingsManager.getSettingByName(this, "Delay").getValDouble();

        if (timer.delay(delay)) {
            Random r = new Random();
            int index = r.nextInt(spam.size());
            String message = spam.get(index);
            mc.player.sendChatMessage(globalMode ? "!" + message : message);
            timer.setLastMS();
        }
    }
}
