package com.kisman.cc.util;

import com.kisman.cc.Kisman;
import com.kisman.cc.settings.SettingsManager;
import net.minecraft.client.Minecraft;

import java.util.Random;

public interface Globals {
    Minecraft mc = Minecraft.getMinecraft();
    Random random = new Random();
    SettingsManager setmgr = Kisman.instance.settingsManager;
}