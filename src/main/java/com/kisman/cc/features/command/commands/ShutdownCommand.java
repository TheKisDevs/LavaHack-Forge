package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import org.jetbrains.annotations.NotNull;

public class ShutdownCommand extends Command {

    public ShutdownCommand(){
        super("shutdown");
    }


    @Override
    public String getDescription() {
        return "shuts down Minecraft";
    }

    @Override
    public String getSyntax() {
        return "shutdown <client/hard/crash>";
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        String mode = args[0];
        if(mode.equals("client")){
            mc.shutdownMinecraftApplet();
            return;
        }
        if(mode.equals("hard")){
            mc.shutdown();
            return;
        }
        if(mode.equals("crash")){
            System.exit(1);
            return;
        }

    }
}
