package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import com.kisman.cc.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.Display;

public class DisplayTitleCommand extends Command {

    public DisplayTitleCommand(){
        super("displaytitle");
    }

    @Override
    public String getDescription() {
        return "Changes the name of the display";
    }

    @Override
    public String getSyntax() {
        return "displaytitle <title>";
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        Display.setTitle(StringUtils.merge(args, 0, args.length).toString());
    }
}
