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

    private Setting clickLine = new Setting("ClickLine", this, "ClickGui");
    public Setting scrollSpeed = new Setting("ScrollSpeed", this, 15, 1, 100, true);

    private Setting chatLine = new Setting("ChatLine", this, "Chat");
//    public Setting dPrefix = new Setting("ChatPrefix", this, Kisman.instance.commandManager.cmdPrefixStr, Kisman.instance.commandManager.cmdPrefixStr, true, true);

    public ClickGUI() {
        super("ClickGUI", "ClickGUI", Category.CLIENT);
        this.setKey(Keyboard.KEY_U);

//        Kisman.EVENT_BUS.subscribe(listener);

        instance = this;

        setmgr.rSetting(clickLine);
        setmgr.rSetting(scrollSpeed);

//        setmgr.rSetting(chatLine);
//        setmgr.rSetting(dPrefix);

        Kisman.instance.settingsManager.rSetting(new Setting("TestButton", this, false));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.displayGuiScreen(Kisman.instance.clickGui);
        this.setToggled(false);
    }

    @EventHandler
    private final Listener<StringEvent> listener = new Listener<>(event -> {
        if(event.str.length() == 1) {
            Kisman.instance.commandManager.cmdPrefix = toString().charAt(0);

            event.cancel();
        }
    });
}
