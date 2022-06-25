package com.kisman.cc.features.command;

import com.kisman.cc.features.command.exceptions.SimilarCommandNamesException;
import com.kisman.cc.util.chat.ChatHandler;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class Command extends ChatHandler implements ICommand {
	protected static Minecraft mc = Minecraft.getMinecraft();
	
	private final String command;
	private String execute;
	private int key;

	private final HashMap<String, SubCommand> instances = new HashMap<>();

	protected SubCommand getInstance(String name) {
		return instances.get(name);
	}
	
	public Command(String command) {
		this.command = command;
		this.key = -1;
	}

	protected void addInstances(SubCommand... instances) {
		for(SubCommand instance : instances) {
			if(this.instances.get(instance.getCommand()) != null) throw new SimilarCommandNamesException(instance, this.instances.get(instance.getCommand()));
			this.instances.put(instance.getCommand(), instance);
		}
	}

	protected void runSubCommands(String command, String[] args) {
		instances.get(args[0]).runCommand(command, args);
	}

	public abstract String getDescription();
	public abstract String getSyntax();

	@Override public @NotNull String getCommand() {
		return command;
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getExecute() {
		return execute;
	}
	public void setExecute(String execute) {
		this.execute = execute;
	}
}
