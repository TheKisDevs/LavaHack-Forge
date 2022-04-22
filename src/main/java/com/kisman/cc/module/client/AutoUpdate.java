package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;

public class AutoUpdate extends Module {
    public AutoUpdate() {super("AutoUpdate", Category.CLIENT);}
    public void onEnable() {Kisman.autoUpdate = true;}
    public void onDisable() {Kisman.autoUpdate = false;}
}
