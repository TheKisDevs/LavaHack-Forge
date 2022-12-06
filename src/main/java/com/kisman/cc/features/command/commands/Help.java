package com.kisman.cc.features.command.commands;

import com.kisman.cc.event.events.client.console.ConsoleMessageEvent;
import com.kisman.cc.features.command.Command;
import com.kisman.cc.features.command.CommandManager;
import com.kisman.cc.gui.console.ConsoleGui;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.text.TextFormatting;
import org.luaj.vm2.ast.Str;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Help extends Command {
    public Help() {
        super("help");
    }

    public void runCommand(String s, String[] args) {
        /*
        message("Commands:");
        message("bind <key> <module>");
        message("bind list");
        message("credits");
        message("flip - this command only for Hypixel Skyblock");
        message("friend <add/remove> <player's name>");
        message("friend list");
        message("help");
        message("loadconfig");
        message("opendir");
        message("saveconfig");
        message("setkey - this command only for Hypixel Skyblock");
        message("slider <module> <slider's name> <value>");
        message("toggle <module>");
        message("tp <x> <y> <z>");
        message("tp <player's nickname>");
         */
        /*
        List<Command> commands = CommandManager.commands.values().stream().sorted(Comparator.comparing(Command::getCommand)).collect(Collectors.toList());
        message("Commands:");
        for(Command cmd : commands){
            String name = valOf(cmd.getCommand());
            String usage = valOf(cmd.getSyntax());
            String description = valOf(cmd.getDescription());
            message("Name: " + name + " Syntax: " + usage + " Description: " + description);
        }
         */
        if(mc.currentScreen instanceof ConsoleGui){
            List<Command> commands = CommandManager.commands.values().stream().sorted(Comparator.comparing(Command::getCommand)).collect(Collectors.toList());
            complete("Command:");
            for(Command cmd : commands){
                String name = valOf(cmd.getCommand());
                String usage = valOf(cmd.getSyntax());
                String description = valOf(cmd.getDescription());
                new ConsoleMessageEvent(TextFormatting.GOLD + "> Command: " + name ).post();
                new ConsoleMessageEvent(TextFormatting.GREEN + ">> Usage: " + TextFormatting.RESET + usage).post();
                new ConsoleMessageEvent(TextFormatting.DARK_GREEN + ">> Description: " + TextFormatting.RESET + description).post();
            }
            return;
        }
        List<Command> commands = CommandManager.commands.values().stream().sorted(Comparator.comparing(Command::getCommand)).collect(Collectors.toList());
        ChatUtility.complete().printClientClassMessage("Commands:");
        for(Command cmd : commands){
            String name = valOf(cmd.getCommand());
            String usage = valOf(cmd.getSyntax());
            String description = valOf(cmd.getDescription());
            ChatUtility.message().printMessage(ChatFormatting.GOLD + "> Command: " + name);
            ChatUtility.message().printMessage(ChatFormatting.GREEN + ">> Usage: " + ChatFormatting.RESET + usage);
            ChatUtility.message().printMessage(ChatFormatting.DARK_GREEN + ">> Description: " + ChatFormatting.RESET + description);
        }
    }

    private static String valOf(String s){
        return s == null ? "(not available)" : s;
    }

    public String getDescription() {
        return "help of commands";
    }

    public String getSyntax() {
        return "help";
    }
}
