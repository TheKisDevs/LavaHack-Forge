package the.kis.devs.api.features.command;

import com.kisman.cc.features.command.Command;
import com.kisman.cc.features.command.CommandManager;

import java.util.ArrayList;

/**
 * @author _kisman_
 * @since 17:07 of 08.06.2022
 */
public class CommandManagerAPI {
    public static ArrayList<Command> getCommands() {
        return CommandManager.commands;
    }
}
