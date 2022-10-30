package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.thread.RepeatedTask;

public class LavaHackOwns extends Module {

    private final Setting count = register(new Setting("Count", this, 5, 1, 50, true));
    private final Setting delay = register(new Setting("Delay", this, 500, 0, 2000, NumberType.TIME));

    public LavaHackOwns(){
        super("LavaHackOwns", Category.DEBUG);
    }

    private RepeatedTask repeatedTask;

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null)
            return;
        repeatedTask = new RepeatedTask(
                RepeatedTask.Mode.Concurrent,
                RepeatedTask.WaitMode.After,
                delay.getValInt(),
                count.getValInt(),
                () -> ChatUtility.info().printMessage("LavaHack owns!")
        );
        repeatedTask.getThread().start();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        repeatedTask.getThread().stop();
    }
}
