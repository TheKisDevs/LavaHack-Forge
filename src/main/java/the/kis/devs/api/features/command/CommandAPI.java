package the.kis.devs.api.features.command;

import com.kisman.cc.features.command.Command;

/**
 * @author _kisman_
 * @since 18:01 of 08.06.2022
 */
public abstract class CommandAPI extends Command {
    public CommandAPI(String command) {super(command);}
    public void runCommand(String s, String[] strings) {}
    public String getDescription() {return null;}
    public String getSyntax() {return null;}
}
