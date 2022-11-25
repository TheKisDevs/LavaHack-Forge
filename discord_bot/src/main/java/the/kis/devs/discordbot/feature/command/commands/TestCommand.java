package the.kis.devs.discordbot.feature.command.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import the.kis.devs.discordbot.feature.command.SlashCommand;
import the.kis.devs.discordbot.permission.permissions.RolePermission;
import the.kis.devs.discordbot.test.TestException;

import java.util.concurrent.ExecutionException;

/**
 * @author _kisman_
 * @since 11:11 of 23.06.2022
 */
public class TestCommand extends SlashCommand {
    public TestCommand() {
        super("test", "just a test command!");
        permissions.add(new RolePermission("964592348963155969"));
    }

    @Override public void run(SlashCommandInteractionEvent event) throws ExecutionException, InterruptedException {
        channelsManager.send("Hello <@" + event.getUser().getId() + ">!", event.getChannel().getId());

        throw new TestException("Test!");
    }
}
