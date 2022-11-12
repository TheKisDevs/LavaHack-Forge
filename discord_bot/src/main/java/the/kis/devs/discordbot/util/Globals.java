package the.kis.devs.discordbot.util;

import the.kis.devs.discordbot.managers.ChannelsManager;
import the.kis.devs.discordbot.managers.LoggingManager;

/**
 * @author _kisman_
 * @since 1:15 of 23.06.2022
 */
public interface Globals {
    String TOKEN = "OTg5Mjg0OTM1ODMzNjI0NTk2.GZ_h2u.d6LOiBLtNryFn-hTnZ6EFVYuLP1ebJKU-jsOOA";

    String PREFIX = "-";

    String MAIN_SERVER_ID = "868099963161313330";
    String ENJOYERS_SERVER_ID = "955736148091748393";

    String MAIN_SERVER_LOGS_CHANNEL_ID = "989292950439751720";
    String ENJOYERS_SERVER_LOGS_CHANNEL_ID = null;

    String STAFF_ROLE_ID = "964592348963155969";


    ChannelsManager channelsManager = new ChannelsManager();
    LoggingManager loggingManager = new LoggingManager();


}
