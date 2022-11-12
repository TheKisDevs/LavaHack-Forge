package the.kis.devs.discordbot.feature.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
 import the.kis.devs.discordbot.feature.command.commands.*;
import the.kis.devs.discordbot.permission.IPermission;
import the.kis.devs.discordbot.util.Globals;
import the.kis.devs.discordbot.util.StackTraceUtil;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * @author _kisman_
 * @since 1:58 of 23.06.2022
 */
public class CommandManager implements Globals {
    public static final ArrayList<Command> commands = new ArrayList<>();

    static {
        commands.add(new AvatarCommand());
        commands.add(new TestCommand());
        commands.add(new ConfigCommand());
        commands.add(new EmbedTestCommand());
        commands.add(new RolesListCommand());
    }

    public static void execute(MessageReceivedEvent event) throws ExecutionException, InterruptedException {
        String message = event.getMessage().getContentStripped();

        if(!message.startsWith(PREFIX)) {
            return;
        }

        message = message.substring(1);

        String[] split = message.split(" ");

        for(Command command : commands) {
            if(command.command.equals(split[0])) {
                for(IPermission permission : command.permissions) {
                    if(!permission.valid(event.getMember())) {
                        loggingManager.error("<@" + event.getAuthor().getId() + "> have no permissions to `" + command.command + "` command!");
                        channelsManager.send("Sorry <@" + event.getAuthor().getId() + ">( You have no permissions for `" + command.command + "` command!", event.getChannel().getId());
                        return;
                    }
                }

                try {
                    command.run(split, event);
                } catch (Exception e) {
                    e.getLocalizedMessage();
                    String[] stSplit = StackTraceUtil.getStackTrace(e).split("\n");

                    StringBuilder stNew = new StringBuilder(stSplit[0] + "\n");

                    for(int i = 1; i < stSplit.length; i++) {
                        stNew.append("> " + stSplit[i] + ((i == stSplit.length - 1 ? "" : "\n")));
                    }


                    loggingManager.error("Bot got error by `" + command.command + "` command! \n> Stacktrace: \n> ```" + stNew + "```");
                    return;
                }
                loggingManager.debug("<@" + event.getAuthor().getId() + "> used command `" + split[0] + "`!");
                return;
            }
        }

        loggingManager.debug("<@" + event.getAuthor().getId() + "> tried to use command `" + split[0] + "`");
    }
}
