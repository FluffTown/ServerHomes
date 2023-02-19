package com.zip.serverhomes;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
		HomeCommands home_cmds = new HomeCommands(getConfig());
		HomeAsCommand homeas_cmd = new HomeAsCommand(getConfig());
		
		final String[] commands = {"sethome", "delhome", "home", "homeas"};
		final CommandExecutor[] executors = {home_cmds, home_cmds, home_cmds, homeas_cmd};
		final TabCompleter[] tab_completers = {home_cmds, home_cmds, home_cmds, homeas_cmd};
		
		for(int i=0; i<commands.length; i++) {
			getCommand(commands[i]).setExecutor(executors[i]);
			getCommand(commands[i]).setTabCompleter(tab_completers[i]);
		}
	}
	
	@Override
	public void onDisable() {
		saveConfig();
	}
}