package com.kisman.cc.features.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.command.Command;
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
    }
}
