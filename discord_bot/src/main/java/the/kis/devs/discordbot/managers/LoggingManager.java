package the.kis.devs.discordbot.managers;

import the.kis.devs.discordbot.util.Globals;

import java.util.concurrent.ExecutionException;

/**
 * @author _kisman_
 * @since 1:19 of 23.06.2022
 */
public class LoggingManager implements Globals {
    public void info(String info) {
        try {
            channelsManager.send("[INFO] " + info, MAIN_SERVER_LOGS_CHANNEL_ID);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void debug(String debug) {
        try {
            channelsManager.send("[DEBUG] " + debug, MAIN_SERVER_LOGS_CHANNEL_ID);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void error(String error) {
        try {
            channelsManager.send("[ERROR] " + error, MAIN_SERVER_LOGS_CHANNEL_ID);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
