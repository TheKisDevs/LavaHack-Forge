package com.kisman.cc.command.commands;

import com.kisman.cc.command.Command;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class Help extends Command {
    public Help() {
        super("help");
    }

    public void runCommand(String s, String[] args) {
        ChatUtils.message("Commands:");
        ChatUtils.message("bind <key> <module>");
        ChatUtils.message("bind list");
        ChatUtils.message("credits");
        ChatUtils.message("flip - this command only for Hypixel Skyblock");
        ChatUtils.message("friend <add/remove> <player's name>");
        ChatUtils.message("friend list");
        ChatUtils.message("help");
        ChatUtils.message("loadconfig");
        ChatUtils.message("opendir");
        ChatUtils.message("saveconfig");
        ChatUtils.message("setkey - this command only for Hypixel Skyblock");
        ChatUtils.message("slider <module> <slider's name> <value>");
        ChatUtils.message("toggle <module>");
        ChatUtils.message("tp <x> <y> <z>");
        ChatUtils.message("tp <player's nickname>");
    }

    public String getDescription() {
        return "help of commands";
    }

    public String getSyntax() {
        return "help";
    }
}
