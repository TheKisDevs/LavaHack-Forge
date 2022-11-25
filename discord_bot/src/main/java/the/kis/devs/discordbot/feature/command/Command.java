package the.kis.devs.discordbot.feature.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import the.kis.devs.discordbot.permission.IPermission;
import the.kis.devs.discordbot.util.Globals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * @author _kisman_
 * @since 1:45 of 23.06.2022
 */
public abstract class Command<T extends Event> implements Globals {
    public final String command;
    public final String description;
    public final ArrayList<IPermission> permissions = new ArrayList<>();
    public final ArrayList</*SerializableData*/OptionData> options = new ArrayList<>();

    public Command(String command, String description, OptionData... options) {
        this.command = command;
        this.description = description;
        this.options.addAll(Arrays.asList(options));
    }
    
    public abstract void run(String[] message, T event) throws ExecutionException, InterruptedException;

    public void registerSlashCommand(Guild guild) {
        guild.upsertCommand(command, description).addOptions(options).queue();
        /*CommandCreateAction action = guild.upsertCommand(command, description);

        for(SerializableData option : options) {
            if(option instanceof OptionData) {
                action.addOptions((OptionData) option);
            }
        }

        for(SerializableData option : options) {
            if(option instanceof SubcommandGroupData) {
                action.addSubcommandGroups((SubcommandGroupData) option);
//                ReflectionsKt.setField(ReflectionsKt.getField(action, "data"), "allowOption", true);
            }
        }

        for(SerializableData option : options) {
            if(option instanceof SubcommandData) {
                action.addSubcommands((SubcommandData) option);
//                ReflectionsKt.setField(ReflectionsKt.getField(action, "data"), "allowOption", true);
//                action.data.allowOption = true;
            }
        }

        action.queue();*/
    }
}
