package com.kisman.cc.features.macro.impl.impls;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.macro.impl.MacroImpl;

public class CommandMacro extends MacroImpl {

    public CommandMacro(String arguments) {
        super("cmd", arguments);
    }

    @Override
    protected void exec() {
        String[] commands = arguments.split(";");
        for(String command : commands)
            Kisman.instance.commandManager.runCommandsNoPrefix(command);
    }
}
