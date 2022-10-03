package com.kisman.cc.features.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.command.Command;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.features.macro.Macro;
import com.kisman.cc.features.macro.MacroManager;
import com.kisman.cc.features.module.Module;
import org.jetbrains.annotations.NotNull;

public class Panic extends Command {

    public Panic(){
        super("panic");
    }

    @Override
    public String getDescription() {
        return "turns off all module";
    }

    @Override
    public String getSyntax() {
        return "panic";
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        for(Module m : Kisman.instance.moduleManager.getEnabledModules())
            m.setToggled(false);
        for(HudModule m : Kisman.instance.hudModuleManager.modules)
            if(m.isToggled())
                m.setToggled(false);
        for(Macro m : MacroManager.getMacros().values())
            if(m.isEnable())
                m.disable();
    }
}
