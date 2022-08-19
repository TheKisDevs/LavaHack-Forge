package com.kisman.cc.features.module.player;

import com.kisman.cc.features.module.Beta;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;

@Beta
public class Octopus extends Module {

    private static final Mode DEFAULT = Mode.LeftClick;

    private final SettingGroup slot1 = register(new SettingGroup(new Setting("Slot 1")));
    private final Setting slot1Active = register(slot1.add(new Setting("Slot1Active", this, true).setTitle("Active")));
    private final Setting slot1Mode = register(slot1.add(new Setting("Slot1Mode", this, DEFAULT)).setTitle("Mode"));
    private final Setting slot1Key = register(slot1.add(new Setting("Slot1Key", this, 0).setTitle("Keybind")));

    public Octopus(){
        super("Octopus", Category.PLAYER);
    }

    @Override
    public void update(){
        if(mc.player == null || mc.world == null)
            return;
    }

    enum Mode {
        LeftClick,
        RightClick,
        MiddleClick
    }
}
