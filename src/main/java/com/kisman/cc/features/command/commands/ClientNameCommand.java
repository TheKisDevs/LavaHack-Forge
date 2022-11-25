package com.kisman.cc.features.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.command.Command;
import com.kisman.cc.util.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ClientNameCommand extends Command {

    public static String NAME = Kisman.getName();

    public ClientNameCommand() {
        super("clientname");
    }

    @Override
    public String getDescription() {
        return "Changes the name of the client";
    }

    @Override
    public String getSyntax() {
        return "clientname <name>";
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        NAME = StringUtils.merge(args, 0, args.length).toString();
    }
}
