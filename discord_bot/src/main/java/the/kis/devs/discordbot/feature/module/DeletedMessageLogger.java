package the.kis.devs.discordbot.feature.module;

import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import the.kis.devs.discordbot.DiscordBotConfig;
import the.kis.devs.discordbot.util.Globals;

/**
 * @author _kisman_
 * @since 1:54 of 23.06.2022
 */
public class DeletedMessageLogger implements Globals {
    public static void onMessageDelete(MessageDeleteEvent event) {
        if(DiscordBotConfig.deletedMessagesLogs.get()) {
            loggingManager.debug("Deleted message: `" + event.getMessageId() + "`, in guild `" + event.getGuild().getName() + "`, in channel <#" + event.getChannel().getId() + ">");
        }
    }
}
