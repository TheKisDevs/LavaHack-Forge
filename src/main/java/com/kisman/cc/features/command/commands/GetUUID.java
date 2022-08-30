package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import com.kisman.cc.util.minecraft.GameProfiles;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class GetUUID extends Command {

    public GetUUID(){
        super("getuuid");
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        if(args.length < 2){
            error("Too few arguments");
            return;
        }
        boolean copy = args[0].split(":")[1].equalsIgnoreCase("true");
        String player = args[1];
        String uuid = GameProfiles.getUUIDString(player);
        if(uuid == null){
            error("The player " + player + " does not exist");
            return;
        }
        if(copy)
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(uuid), null);
        complete("The UUID of " + player + " is " + uuid);
    }

    @Override
    public String getDescription() {
        return "gets the uuid of the specified player";
    }

    @Override
    public String getSyntax() {
        return "uuid copy:<true/false> <player>";
    }
}
