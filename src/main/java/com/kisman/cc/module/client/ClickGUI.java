package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.StringEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.sun.org.apache.xpath.internal.operations.String;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import org.lwjgl.input.Keyboard;

public class ClickGUI extends Module {
    public static ClickGUI instance;


    public ClickGUI() {
        super("ClickGUI", "ClickGUI", Category.CLIENT);
        this.setKey(Keyboard.KEY_U);

        instance = this;

        Kisman.instance.settingsManager.rSetting(new Setting("TestButton", this, false));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.displayGuiScreen(Kisman.instance.clickGui);
        this.setToggled(false);
    }
}
