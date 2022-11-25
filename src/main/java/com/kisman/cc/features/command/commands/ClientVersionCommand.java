package com.kisman.cc.features.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.command.Command;
import com.kisman.cc.util.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ClientVersionCommand extends Command {

    public static String VERSION = Kisman.getVersion();

    public ClientVersionCommand(){
        super("clientversion");
    }

    @Override
    public String getDescription() {
        return "Changes the version of the client";
    }

    @Override
    public String getSyntax() {
        return "clientversion <version>";
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        VERSION = StringUtils.merge(args, 0, args.length).toString();
    }
}
