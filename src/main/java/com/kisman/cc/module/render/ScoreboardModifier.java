package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

public class ScoreboardModifier extends Module {
    public Setting yPos = new Setting("Y Pos", this, 0, 0, mc.displayHeight, true);

    public static ScoreboardModifier instance;

    public ScoreboardModifier() {
        super("ScoreboardModifier", Category.RENDER);

        instance = this;

        setmgr.rSetting(yPos);
    }
}
