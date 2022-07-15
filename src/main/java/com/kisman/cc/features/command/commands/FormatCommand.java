package com.kisman.cc.features.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.command.Command;
import com.kisman.cc.features.command.SubCommand;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Cubic
 * @since 10.7.2022
 */
public class FormatCommand extends Command {

    private static final File CONFIG_FILE = new File(Minecraft.getMinecraft().mcDataDir + "kisman.cc/", "fmtcmd.txt");

    public FormatCommand() {
        super("fmt");
        if(!CONFIG_FILE.exists())
            writeDefaultConfig(CONFIG_FILE, false);
        addInstances(
                new CommandConfig(this),
                new CommandFmt(this),
                new CommandResetConfig(this)
        );
    }

    @Override
    public String getDescription() {
        return "Format chat messages";
    }

    @Override
    public String getSyntax() {
        return "-fmt <fmt/config> <message to be formatted (only when used with fmt)>";
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        runSubCommands(s, args);
    }

    private static class CommandConfig extends SubCommand {

        public CommandConfig(@NotNull Command instance) {
            super("config", instance);
        }

        @Override
        public void runCommand(@NotNull String s, @NotNull String[] args) {
            if(!CONFIG_FILE.exists())
                writeDefaultConfig(CONFIG_FILE, false);
            try {
                Desktop.getDesktop().open(CONFIG_FILE);
            } catch (IOException e){
                Kisman.LOGGER.error("Could not open: " + CONFIG_FILE.getAbsolutePath(), e);
            }
        }
    }

    private static class CommandFmt extends SubCommand {

        public CommandFmt(@NotNull Command instance) {
            super("fmt", instance);
        }

        @Override
        public void runCommand(@NotNull String s, @NotNull String[] args) {
            // TODO: Implement it (this is going to be painful) - Cubic
            ChatUtility.info().printClientClassMessage("To be implemented");
        }
    }

    private static class CommandResetConfig extends SubCommand {

        public CommandResetConfig(@NotNull Command instance) {
            super("resetconfig", instance);
        }

        @Override
        public void runCommand(@NotNull String s, @NotNull String[] args) {
            writeDefaultConfig(CONFIG_FILE, CONFIG_FILE.exists());
        }
    }

    private static void writeDefaultConfig(File file, boolean exists){
        String lineSeparator = System.lineSeparator();
        if(exists)
            file.delete();
        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write("CommandPrefix:%" + lineSeparator +
                             "QuotePrefix:Q" + lineSeparator +
                             "LookupPrefix:L" + lineSeparator +
                             "InsertPrefix:I" + lineSeparator +
                             "ContentBounders:{}"
            );
        } catch (IOException e){
            Kisman.LOGGER.error("IOError in FormatCommand::writeDefaultConfig", e);
        }
    }
}
