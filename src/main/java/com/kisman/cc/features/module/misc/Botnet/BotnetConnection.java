package com.kisman.cc.features.module.misc.Botnet;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;

import javax.security.auth.login.LoginException;

public class BotnetConnection extends Module {
    public BotnetConnection() {
        super("BotnetConnection", "Connects you into a botnet", Category.MISC);
    }

    ConnectionProvider cp;

    @Override
    public void onEnable() {
        cp = new ConnectionProvider(token.getValString(), name.getValString());
    }


    Setting token = register(new Setting("Discord bot token", this, ""));
    Setting name = register(new Setting("Input bot name", this, ""));



}
