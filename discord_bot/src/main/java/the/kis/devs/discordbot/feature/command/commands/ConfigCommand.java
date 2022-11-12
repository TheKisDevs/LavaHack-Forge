package the.kis.devs.discordbot.feature.command.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import the.kis.devs.discordbot.DiscordBotConfig;
import the.kis.devs.discordbot.feature.command.Command;
import the.kis.devs.discordbot.permission.permissions.RolePermission;

import java.util.concurrent.ExecutionException;

/**
 * @author _kisman_
 * @since 1:45 of 23.06.2022
 */
public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config");
        permissions.add(new RolePermission(STAFF_ROLE_ID));

        //Pattern: -config name true/false | -config list
    }

    @Override
    public void run(String[] message, MessageReceivedEvent event) throws ExecutionException, InterruptedException {
        if(message.length == 3) {
            if(DiscordBotConfig.values.get(message[1]) != null) {
                if(message[2].equals("true") || message[2].equals("false")) {
                    DiscordBotConfig.values.get(message[1]).set(Boolean.parseBoolean(message[2]));
                    channelsManager.send("Done! New value for setting{`" + message[1] + "`} is `" + message[2] + "`!", event.getChannel().getId());
                } else {
                    channelsManager.send("Illegal value{`" + message[2] + "`}! Allows only `true` or `false`", event.getChannel().getId());
                }
            } else {
                channelsManager.send("Illegal value's name{`" + message[1] + "`}!", event.getChannel().getId());
            }
        } else if(message.length == 2) {
            StringBuilder toSend = new StringBuilder("Allows settings with values:\n");

            for(String key : DiscordBotConfig.values.keySet()) {
                toSend.append("> Value{Name: `" + key + "` | Value: `" + DiscordBotConfig.values.get(key).value + "`}\n");
            }

            channelsManager.send(toSend.toString(), event.getChannel().getId());
        } else {
            //Idk.
        }
    }
}
