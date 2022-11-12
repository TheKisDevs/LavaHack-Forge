package the.kis.devs.discordbot.feature.command.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import the.kis.devs.discordbot.feature.command.Command;
import the.kis.devs.discordbot.permission.permissions.RolePermission;
import the.kis.devs.discordbot.test.TestException;

import java.util.concurrent.ExecutionException;

/**
 * @author _kisman_
 * @since 11:11 of 23.06.2022
 */
public class TestCommand extends Command {
    public TestCommand() {
        super("test");
        permissions.add(new RolePermission("964592348963155969"));
    }

    @Override public void run(String[] command, MessageReceivedEvent event) throws ExecutionException, InterruptedException {
        channelsManager.send("Hello <@" + event.getAuthor().getId() + ">!", event.getChannel().getId());

        throw new TestException("Test!");
    }
}
