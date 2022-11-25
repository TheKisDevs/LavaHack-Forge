package the.kis.devs.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import the.kis.devs.discordbot.feature.command.Command;
import the.kis.devs.discordbot.feature.command.CommandManager;
import the.kis.devs.discordbot.handlers.MessageHandler;

/**
 * @author _kisman_
 * @since 1:13 of 23.06.2022
 */
public class DiscordBotController {
    public JDABuilder initBuilder(JDABuilder builder) {
        builder.addEventListeners(new MessageHandler());

        builder.setActivity(Activity.playing("Enjoy LavaHack"));

        return builder;
    }

    public void initSlashCommands(JDA jda) throws InterruptedException {
//        jda.upsertCommand("uwu", "yooo").queue();

//        Guild guild = jda.getGuildById("868099963161313330");

//        if(guild != null) {
//            guild.upsertCommand("test", "test command").queue();
//            guild.upsertCommand("kill", "shut up").queue();
//        }

        jda.getGuilds().forEach(guild -> {
            for(Command command : CommandManager.commands) {
                command.registerSlashCommand(guild);
            }
        });

    }
}
