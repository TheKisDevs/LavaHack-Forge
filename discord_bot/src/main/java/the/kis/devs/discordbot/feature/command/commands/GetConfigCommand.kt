package the.kis.devs.discordbot.feature.command.commands

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.api.utils.FileUpload
import the.kis.devs.discordbot.feature.command.Command
import the.kis.devs.discordbot.feature.command.SlashCommand
import java.io.File

/**
 * @author _kisman_
 * @since 20:43 of 25.11.2022
 */
class GetConfigCommand : SlashCommand(
    "getconfig",
    "sends the config for the server you need :)"
) {

    private val configs = HashMap<String, File>()

    override fun registerSlashCommand(
        guild : Guild
    ) {
        guild.upsertCommand(command, description)
            .addSubcommands(
                SubcommandData("action", "does some actions!")
                    .addOptions(
                        OptionData(OptionType.STRING, "action", "does some actions!")
                            .addChoice("List", "list")
                            .addChoice("Reload", "reload")
                            .also { it.isRequired = true }
                    )
            )
            .addSubcommands(
                SubcommandData("config", "configs")
                    .addOptions(
                        OptionData(OptionType.STRING, "config", "sends you the config you need")
                            .also { it.isRequired = true }
                    )
            )
            .queue()
    }

    override fun run(
        event : SlashCommandInteractionEvent
    ) {
        event.deferReply().queue()

        when(event.subcommandName) {
            "action" -> {
                when(event.getOption("action")!!.asString) {
                    "list" -> {
                        if (configs.isEmpty()) {
                            for (file in (File("discord_bot/files/configs/").listFiles() ?: throw IllegalArgumentException("Exception in getconfig command"))) {
                                if (file.name.endsWith(".kis")) {
                                    configs[file.name.removeSuffix(".kis")] = file
                                }
                            }
                        }

                        var output = "Available configs: "

                        for ((i, config) in configs.keys.withIndex()) {
                            output += "`${
                                config
                            }`${
                                if (i != configs.size - 1) {
                                    ", "
                                } else {
                                    ""
                                }
                            }"
                        }

                        event.hook.sendMessage(output).queue()
                    }

                    "reload" -> {
                        configs.clear()

                        for (file in (File("discord_bot/files/configs/").listFiles() ?: throw IllegalArgumentException("Exception in getconfig command"))) {
                            if (file.name.endsWith(".kis")) {
                                configs[file.name.removeSuffix(".kis").toLowerCase()] = file
                            }
                        }

                        event.hook.sendMessage("Successfully reloaded configs!").queue()
                    }
                }
            }
            "config" -> {
                if(configs.isEmpty()) {
                    for (file in (File("discord_bot/files/configs/").listFiles() ?: throw IllegalArgumentException("Exception in getconfig command"))) {
                        if (file.name.endsWith(".kis")) {
                            configs[file.name.removeSuffix(".kis")] = file
                        }
                    }
                }

                val config = event.getOption("config")!!.asString.toLowerCase()

                if(configs.contains(config)) {
                    event.hook.sendMessage("Config `$config`: ").setEphemeral(true).addFiles(FileUpload.fromData(configs[config]!!)).queue()
                } else {
                    event.hook.sendMessage("Config with name `$config` does not exists!").setEphemeral(true).queue()
                }
            }
        }
    }
}