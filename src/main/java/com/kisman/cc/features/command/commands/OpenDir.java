package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.File;

public class OpenDir extends Command {
    public OpenDir() {
        super("opendir");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            File file = new File(Minecraft.getMinecraft().mcDataDir + "kisman.cc/");
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "opening minecraft's directory";
    }

    @Override
    public String getSyntax() {
        return "opendir";
    }
}
