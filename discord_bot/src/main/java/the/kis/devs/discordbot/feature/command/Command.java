package the.kis.devs.discordbot.feature.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import the.kis.devs.discordbot.permission.IPermission;
import the.kis.devs.discordbot.util.Globals;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * @author _kisman_
 * @since 1:45 of 23.06.2022
 */
public abstract class Command implements Globals {
    public final String command;
    public final ArrayList<IPermission> permissions = new ArrayList<>();

    public Command(String command) {
        this.command = command;
    }
    
    public abstract void run(String[] message, MessageReceivedEvent event) throws ExecutionException, InterruptedException;
}
