package the.kis.devs.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import the.kis.devs.discordbot.util.Globals;

/**
 * @author _kisman_
 * @since 1:02 of 23.06.2022
 */
public class DiscordBotMain implements Globals {
    public static JDA jda;
    public static JDABuilder builder;

    public static final DiscordBotController controller = new DiscordBotController();

    public static void main(String[] args) throws InterruptedException {
        builder = JDABuilder.createDefault(TOKEN)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT);

        controller.initBuilder(builder);

        jda = builder.build().awaitReady();

        controller.initSlashCommands(jda);

        if(DiscordBotConfig.startupLogs.get()) {
            loggingManager.info("Startup!");
        }
    }
}
