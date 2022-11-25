package the.kis.devs.discordbot.handlers;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import the.kis.devs.discordbot.feature.command.CommandManager;
import the.kis.devs.discordbot.feature.module.DeletedMessageLogger;
import the.kis.devs.discordbot.feature.module.NicknameModifier;
import the.kis.devs.discordbot.feature.module.SenddedMessagesLogger;
import the.kis.devs.discordbot.util.Globals;

import java.util.concurrent.ExecutionException;

/**
 * @author _kisman_
 * @since 1:30 of 23.06.2022
 */
public class MessageHandler extends ListenerAdapter implements Globals {
    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        DeletedMessageLogger.onMessageDelete(event);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getAuthor().isBot() || event.getAuthor().isSystem()) {
            return;
        }

        try {CommandManager.execute(event);} catch (ExecutionException | InterruptedException ignored) {}
        NicknameModifier.onMessageReceived(event);
        SenddedMessagesLogger.onMessageReceived(event);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        try {CommandManager.execute(event);} catch (ExecutionException | InterruptedException ignored) {}
    }
}
