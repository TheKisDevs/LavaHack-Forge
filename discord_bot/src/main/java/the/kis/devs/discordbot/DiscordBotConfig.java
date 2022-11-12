package the.kis.devs.discordbot;

import the.kis.devs.discordbot.feature.config.Setting;

import java.util.HashMap;

/**
 * @author _kisman_
 * @since 1:31 of 23.06.2022
 */
public class DiscordBotConfig {
    public static HashMap<String, Setting<?>> values = new HashMap<>();

    public static Setting<Boolean> startupLogs = new Setting<>(true);
    public static Setting<Boolean> deletedMessagesLogs = new Setting<>(true);
    public static Setting<Boolean> makeNicknameLikeLastMessage = new Setting<>(false);
    public static Setting<Boolean> senddedMessagesLogs = new Setting<>(false);

    static {
        values.put("startuplogs", startupLogs);
        values.put("delmsgslogs", deletedMessagesLogs);
        values.put("makenamelikelastmsg", makeNicknameLikeLastMessage);
        values.put("senddedmsgslogs", senddedMessagesLogs);
    }
}
