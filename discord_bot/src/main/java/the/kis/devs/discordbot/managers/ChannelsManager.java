package the.kis.devs.discordbot.managers;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import the.kis.devs.discordbot.DiscordBotMain;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * @author _kisman_
 * @since 1:15 of 23.06.2022
 */
public class ChannelsManager {
    public String send(String message, String channelID) throws ExecutionException, InterruptedException {
        return getMessageChannelById(channelID).sendMessage(message).submit().get().getId();
    }

    public String send(String message, String channelID, MessageEmbed... embeds) throws ExecutionException, InterruptedException {
        return getMessageChannelById(channelID).sendMessage(message).addEmbeds(embeds).submit().get().getId();
    }

    public String send(String channelID, MessageEmbed... embeds) throws ExecutionException, InterruptedException {
        return getMessageChannelById(channelID).sendMessageEmbeds(Arrays.asList(embeds)).submit().get().getId();
    }

    public String send(String message, String channelID, FileUpload... files) throws ExecutionException, InterruptedException {
        return getMessageChannelById(channelID).sendMessage(message).addFiles(files).submit().get().getId();
    }

    public String send(String channelID, FileUpload... files) throws ExecutionException, InterruptedException {
        return getMessageChannelById(channelID).sendFiles(files).submit().get().getId();
    }

    public String sendPrivate(String message, String channelID, Member sender) throws InterruptedException, ExecutionException {
        return getMessageChannelById(channelID).sendMessage(message).submit().get().getId();
    }

    public StandardGuildMessageChannel getMessageChannelById(String channelID) throws InterruptedException {
        if(DiscordBotMain.jda.awaitReady().getTextChannelById(channelID) != null) {
            return DiscordBotMain.jda.awaitReady().getTextChannelById(channelID);
        } else if(DiscordBotMain.jda.awaitReady().getNewsChannelById(channelID) != null) {
            return DiscordBotMain.jda.awaitReady().getNewsChannelById(channelID);
        }
        return null;
    }
}
