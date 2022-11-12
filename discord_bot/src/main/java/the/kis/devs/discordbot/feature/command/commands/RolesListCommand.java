package the.kis.devs.discordbot.feature.command.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import the.kis.devs.discordbot.feature.command.Command;

import java.util.concurrent.ExecutionException;

/**
 * @author _kisman_
 * @since 15:10 of 23.06.2022
 */
public class RolesListCommand extends Command {
    public RolesListCommand() {
        super("roleslist");
    }

    @Override
    public void run(String[] message, MessageReceivedEvent event) throws ExecutionException, InterruptedException {
        Member member;

        if(message.length == 1) {
            member = event.getMember();
        } else if(message.length == 2) {
            return;
//            member = ;
        } else {
            return;
        }

        //Some code

        StringBuilder toSend = new StringBuilder("<@" + event.getAuthor().getId() + ">'s roles: \n");

        for(Role role : member.getRoles()) {
            toSend.append("> Role{Name: `").append(role.getName()).append("` | ID: `").append(role.getId()).append("`}\n");
        }

        channelsManager.send(toSend.toString(), event.getChannel().getId());
    }
}
