package the.kis.devs.discordbot.managers;

import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import the.kis.devs.discordbot.DiscordBotMain;

import java.util.concurrent.ExecutionException;

/**
 * @author _kisman_
 * @since 1:15 of 23.06.2022
 */
public class ChannelsManager {
    public String send(String message, String channelID) throws ExecutionException, InterruptedException {
        return getMessageChannelById(channelID).sendMessage(message).submit().get().getId();
    }

    public BaseGuildMessageChannel getMessageChannelById(String channelID) throws InterruptedException {
        if(DiscordBotMain.jda.awaitReady().getTextChannelById(channelID) != null) {
            return DiscordBotMain.jda.awaitReady().getTextChannelById(channelID);
        } else if(DiscordBotMain.jda.awaitReady().getNewsChannelById(channelID) != null) {
            return DiscordBotMain.jda.awaitReady().getNewsChannelById(channelID);
        }
        return null;
    }
}
