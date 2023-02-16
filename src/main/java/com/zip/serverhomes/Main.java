package com.zip.serverhomes;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
		HomeCommand homecmd = new HomeCommand(getConfig());
		final String[] commands = {"sethome", "delhome", "home"};
		for(String s : commands) {
			getCommand(s).setExecutor(homecmd);
			getCommand(s).setTabCompleter(homecmd);
		}
	}
	
	@Override
	public void onDisable() {
		saveConfig();
	}
}