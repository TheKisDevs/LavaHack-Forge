package the.kis.devs.discordbot.feature.module;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import the.kis.devs.discordbot.DiscordBotConfig;
import the.kis.devs.discordbot.util.Globals;

/**
 * @author _kisman_
 * @since 1:46 of 23.06.2022
 */
public class NicknameModifier implements Globals {
    public static void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot() || event.getAuthor().isSystem()) {
            return;
        }

        if(DiscordBotConfig.makeNicknameLikeLastMessage.get()) {
            try {
                event.getMember().modifyNickname(event.getMessage().getContentDisplay()).submit();
            } catch(HierarchyException e) {
                loggingManager.error("Cant modify nick name for <@" + event.getAuthor().getId() + ">!");
                return;
            }
            loggingManager.debug("Modified nick name for <@" + event.getAuthor().getId() + ">, to `" + event.getMessage().getContentDisplay() + "`");
        }
    }
}
