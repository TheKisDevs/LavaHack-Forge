package com.kisman.cc.features.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.command.Command;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import org.jetbrains.annotations.NotNull;

public class ModCountCommand extends Command {

    public ModCountCommand() {
        super("modcount");
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        ChatUtility.info().printClientClassMessage("LavaHack has " + Kisman.instance.moduleManager.modules.size() + " modules");
    }
}
