package the.kis.devs.discordbot.feature.module;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import the.kis.devs.discordbot.DiscordBotConfig;
import the.kis.devs.discordbot.util.Globals;

/**
 * @author _kisman_
 * @since 1:59 of 23.06.2022
 */
public class SenddedMessagesLogger implements Globals {
    public static void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot() || event.getAuthor().isSystem()) {
            return;
        }

        if(DiscordBotConfig.senddedMessagesLogs.get()) {
            loggingManager.debug("<@" + event.getAuthor().getId() + "> sent message `" + event.getMessage().getContentDisplay() + "`, in guild `" + event.getGuild().getName() + "`, in channel <#" + event.getChannel().getId() + ">");
        }
    }
}
