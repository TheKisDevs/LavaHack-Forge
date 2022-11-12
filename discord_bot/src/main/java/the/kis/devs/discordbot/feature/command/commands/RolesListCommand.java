package the.kis.devs.discordbot.feature.command.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import the.kis.devs.discordbot.feature.command.Command;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author _kisman_
 * @since 15:10 of 23.06.2022
 */
public class RolesListCommand extends Command {
    public RolesListCommand() {
        super("roleslist");
    }

    private void sendRoles(Member member, MessageReceivedEvent event) {
        StringBuilder toSend = new StringBuilder("<@" + member.getId() + ">'s roles: \n");

        member.getRoles().forEach(role -> toSend.append("> Role{Name: `").append(role.getName()).append("` | ID: `").append(role.getId()).append("`}\n"));

        try {
            channelsManager.send(toSend.toString(), event.getChannel().getId());
        } catch (ExecutionException | InterruptedException ignored) {

        }
    }

    @Override
    public void run(String[] message, MessageReceivedEvent event) {
        if(message.length == 1) {
            sendRoles(event.getMember(), event);
        } else if(message.length >= 2) {
            List<Member> members = event.getMessage().getMentions().getMembers();
            members.forEach(member -> sendRoles(member, event));
        }
    }
}
