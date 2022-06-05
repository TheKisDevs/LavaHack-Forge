package com.kisman.cc.features.module.misc;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;

public class AntiAutoJump extends Module {

    public AntiAutoJump(){
        super("AntiAutoJump", Category.MISC);
    }

    private boolean autoJump;

    @Override
    public void onEnable(){
        autoJump = mc.gameSettings.autoJump;
    }

    @Override
    public void update(){

        if(mc.gameSettings.autoJump)
            mc.gameSettings.autoJump = false;
    }

    @Override
    public void onDisable(){
        mc.gameSettings.autoJump = autoJump;
    }
}

