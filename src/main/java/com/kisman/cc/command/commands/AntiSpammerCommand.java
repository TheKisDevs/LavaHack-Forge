package com.kisman.cc.command.commands;

import com.kisman.cc.command.Command;
import com.kisman.cc.module.chat.AntiSpammer;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class AntiSpammerCommand extends Command {
    public AntiSpammerCommand() {
        super("antispam");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            if(args[0].equalsIgnoreCase("add")) {
                AntiSpammer.instance.strings.add(args[1]);
                ChatUtils.complete(args[1] + " added to AntiSpammer list");
            } else if(args[0].equalsIgnoreCase("remove")) {
                AntiSpammer.instance.strings.remove(args[1]);
                ChatUtils.complete(args[1] + " removed from AntiSpammer list");
            } else if(args[0].equalsIgnoreCase("clear")) {
                AntiSpammer.instance.strings.clear();
                ChatUtils.complete("AntiSpammer list has been cleared");
            } else if(args[0].equalsIgnoreCase("list")) {
                ChatUtils.simpleMessage("AntiSpammer list:");
                for(String str : AntiSpammer.instance.strings) ChatUtils.simpleMessage(str);
            }
        } catch (Exception e) {
            ChatUtils.error("Usage:" + getDescription());
        }
    }

    @Override
    public String getDescription() {
        return "null";
    }

    @Override
    public String getSyntax() {
        return "antispam <add/remove/list>";
    }
}
