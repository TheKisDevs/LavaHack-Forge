package com.kisman.cc.features.module.misc;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;

public class ClickDelayTimer extends Module {

    private final Setting leftClick = register(new Setting("LeftClick", this, false));
    private final Setting leftClickDelay = register(new Setting("LeftClickDelay", this, 4, 0, 40, true));
    private final Setting rightClick = register(new Setting("RightClick", this, true));
    private final Setting rightClickDelay = register(new Setting("RightClickDelay", this, 4, 0, 40, true));

    public ClickDelayTimer(){
        super("ClickDelayTimer", Category.MISC);
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null)
            return;
        leftClick();
        rightClick();
    }

    private void leftClick(){
        if(!leftClick.getValBoolean())
            return;
        if(mc.player.ticksExisted % leftClickDelay.getValInt() != 0)
            return;
    }

    private void rightClick(){
        if(!rightClick.getValBoolean())
            return;
        if(mc.player.ticksExisted % rightClickDelay.getValInt() != 0)
            return;
        // right click here???
    }
}
