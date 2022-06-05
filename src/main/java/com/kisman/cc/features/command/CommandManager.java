package com.kisman.cc.features.command;

import com.kisman.cc.features.command.commands.*;

import java.util.*;

public class CommandManager {
    public static ArrayList<Command> commands = new ArrayList<Command>();
	
	public char cmdPrefix = '-';
	public String cmdPrefixStr = "" + cmdPrefix;

	public CommandManager()
	{
		addCommands();
	}

	public void addCommands() {
		commands.add(new AntiSpammerCommand());
		commands.add(new Bind());
		commands.add(new ConfigCommand());
		commands.add(new DDOSCommand());
		commands.add(new Flip());
		commands.add(new FriendCommand());
		commands.add(new Help());
		commands.add(new LoadConfigCommand());
		commands.add(new LuaCommand());
        commands.add(new Slider());
        commands.add(new OpenDir());
		commands.add(new RollBackCommand());
        commands.add(new SaveConfigCommand());
		commands.add(new SetKey());
        commands.add(new Toggle());
		commands.add(new Tp());
	}

	public void runCommands(String s) {
		String readString = s.trim().substring(Character.toString(cmdPrefix).length()).trim();
		boolean commandResolved = false;
		boolean hasArgs = readString.trim().contains(" ");
		String commandName = hasArgs ? readString.split(" ")[0] : readString.trim();
		String[] args = hasArgs ? readString.substring(commandName.length()).trim().split(" ") : new String[0];

		for(Command command : commands) {
			if(command.getCommand().trim().equalsIgnoreCase(commandName.trim())) {
				command.runCommand(readString, args);
				commandResolved = true;
				break;
			}
		}
		if(!commandResolved) Command.error("Cannot resolve internal command: \u00a7c" + commandName);
	}
}
